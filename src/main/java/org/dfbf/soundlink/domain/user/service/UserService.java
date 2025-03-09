package org.dfbf.soundlink.domain.user.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dfbf.soundlink.domain.alert.service.AlertService;
import org.dfbf.soundlink.domain.blocklist.repository.BlockListRepository;
import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;
import org.dfbf.soundlink.domain.emotionRecord.repository.EmotionRecordRepository;
import org.dfbf.soundlink.domain.emotionRecord.repository.SpotifyMusicRepository;
import org.dfbf.soundlink.domain.user.dto.request.CheckPasswordDto;
import org.dfbf.soundlink.domain.user.dto.request.LoginReqDto;
import org.dfbf.soundlink.domain.user.dto.request.UserSignUpDto;
import org.dfbf.soundlink.domain.user.dto.request.UserUpdateDto;
import org.dfbf.soundlink.domain.user.dto.response.UserGetDto;
import org.dfbf.soundlink.domain.user.dto.response.UserMyPageDto;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.exception.NoUserDataException;
import org.dfbf.soundlink.domain.user.repository.ProfileMusicRepository;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.global.auth.JwtProvider;
import org.dfbf.soundlink.global.auth.TokenProperties;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final ProfileMusicRepository profileMusicRepository;
    private final SpotifyMusicRepository spotifyMusicRepository;
    private final EmotionRecordRepository emotionRecordRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final RedisService redisService;

    private final JwtProvider jwtProvider;
    private final TokenProperties tokenProperties;

    private final RedisTemplate<String, String> redisTemplate;
    private final TokenService tokenService;
    private final AlertService alertService;

    private static final String domain = "";
    private final BlockListRepository blockListRepository;

    @Value("${cookie.setting.secure}")
    private boolean secure;

    // 회원가입
    public ResponseResult signUp(UserSignUpDto userSignUpDto) {
        try {
            userRepository.save(userSignUpDto.toEntity(passwordEncoder));
            return new ResponseResult(ErrorCode.SUCCESS);
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.DB_ERROR);
        }
    }

    // 회원정보 조회
    @Transactional
    public ResponseResult getUser(Long userId) {
        try {
            log.info("getUser" + "userId: " + userId);
            User user = userRepository.findByUserIdWithCache(userId)
                    .orElseThrow(NoUserDataException::new);
            UserGetDto result = new UserGetDto(user);

            return new ResponseResult(ErrorCode.SUCCESS, result);
        } catch (NoUserDataException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER);
        }
    }

    // 회원정보 수정
    @Transactional
    public ResponseResult updateUser(Long userId, UserUpdateDto userUpdateDto) {
        /**
         * orElse -> 일단 함수는 실행, 그러나 값이 null이면 orElse의 값으로 대체 (함수O, 람다x)
         * orElseGet -> null일때만 실행 (함수O, 람다O)
         */

        try {
            User user = userRepository.findByUserIdWithCache(userId)
                    .orElseThrow(NoUserDataException::new);
            String spotifyId = userUpdateDto.spotifyId().orElse(null);

            if (spotifyId != null && !spotifyId.equals("-1")) {
                // SpotifyMusic 객체 찾기 (없으면 새로 생성 & 저장)
                SpotifyMusic spotifyMusic = spotifyMusicRepository.findBySpotifyId(spotifyId)
                        .orElseGet(() -> {
                            SpotifyMusic sm = new SpotifyMusic(userUpdateDto);
                            spotifyMusicRepository.save(sm);
                            return sm;
                        });
                user.update(userUpdateDto, passwordEncoder, spotifyMusic);
            } else {
                if("-1".equals(spotifyId)) { user.getProfileMusic().deleteSpotifyId(); }
                user.update(userUpdateDto, passwordEncoder);
            }

            profileMusicRepository.save(user.getProfileMusic());
            userRepository.saveWithCache(user);

            return new ResponseResult(ErrorCode.SUCCESS);
        } catch (NoUserDataException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER, e.getMessage());
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.BAD_REQUEST, e.getMessage());
        }
    }

    // 회원정보 삭제
    @Transactional
    public ResponseResult deleteUser(Long userId) {
        try {
            User user = userRepository.findByUserIdWithCache(userId)
                    .orElseThrow(NoUserDataException::new);

            emotionRecordRepository.deleteByUser(user); // 유저 감정 기록 삭제
            blockListRepository.deleteAllByUser_UserId(user.getUserId());  // 유저 차단 목록 삭제
            userRepository.deleteById(userId);          // 유저 삭제

            return new ResponseResult(ErrorCode.SUCCESS);
        } catch (NoUserDataException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER);
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.DB_ERROR);
        }
    }

    @Transactional
    public ResponseResult passwordCheck(Long userId, CheckPasswordDto dto) {
        try {
            User user = userRepository.findByUserIdWithCache(userId)
                    .orElseThrow(NoUserDataException::new);

            if (passwordEncoder.matches(dto.password(), user.getPassword())) {
                return new ResponseResult(ErrorCode.SUCCESS);
            } else {
                return new ResponseResult(ErrorCode.NOT_EQUALS_PASSWORD);
            }
        } catch (NoUserDataException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER);
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.DB_ERROR);
        }
    }

    // 마이페이지 (MyPage)
    @Transactional
    public ResponseResult getMyPage(Long userId) {
        try {
            User user = userRepository.findByUserIdWithCache(userId)
                    .orElseThrow(NoUserDataException::new);

            UserMyPageDto result = userRepository.findUserMyPageDtoByUserId(user.getUserId());

            return new ResponseResult(ErrorCode.SUCCESS, result);
        } catch (NoUserDataException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER);
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.DB_ERROR);
        }
    }
  
    // 인증코드 발급. 이메일이 존재하는지 확인.
    public ResponseResult sendAuthCode(String email) {

        try {
            String authCode = mailService.sendSimpleMessage(email);
            redisService.setCode(email, authCode);
            return new ResponseResult(ErrorCode.SUCCESS, email);
        } catch (MessagingException e) {
            return new ResponseResult(ErrorCode.EMAIL_SEND_ERROR, "이메일 전송에 실패했습니다.");
        }
    }

    // 이메일과 인증코드를 검증
    public ResponseResult validateAuthCode(String email, String authCode){
        try {
            String savedCode = redisService.getCode(email);
            boolean isSuccess = authCode.equals(savedCode);
            if (isSuccess) {
                return new ResponseResult(ErrorCode.SUCCESS, email);
            } else {
                return new ResponseResult(ErrorCode.BAD_REQUEST, "이메일 전송 실패: 잘못된 요청입니다.");
            }
        } catch (AuthenticationException e) {
            return new ResponseResult(ErrorCode.EMAIL_SEND_ERROR);
        }
    }

    // 이메일 중복 확인
    public ResponseResult checkEmail(String email){
        boolean exists = userRepository.existsByEmail(email);
        if(exists){
            return new ResponseResult(ErrorCode.DUPLICATE_EMAIL);
        }
        return new ResponseResult(ErrorCode.NOT_DUPLICATE_EMAIL);
    }
  
    // 닉네임 중복 확인
    public ResponseResult checkNickName(String nickName){
        boolean exists = userRepository.existsByNickname(nickName);
        
        if(exists) { return new ResponseResult(ErrorCode.DUPLICATE_NICKNAME); }
        return new ResponseResult(ErrorCode.NOT_DUPLICATE_NICKNAME);
    }
    @Value("${REFRESH_TOKEN_EXPIRATION_TIME}")
    private int REFRESH_TOKEN_EXPIRATION_TIME;

    // RefreshToken을 쿠키로 설정
    private ResponseCookie getRefreshToken(String refreshToken) {
        return ResponseCookie
                .from("REFRESHTOKEN", refreshToken)
                .domain(domain)
                .path("/")
                .httpOnly(true)
                .secure(secure)
                .maxAge(REFRESH_TOKEN_EXPIRATION_TIME/1000) // 만료시간 설정(밀리초 -> 초로 변경)
                .build();
    }
  
    // 로그인
    public ResponseResult login(LoginReqDto loginReqDto, HttpServletResponse response) {
        try {
            if(!userRepository.existsByLoginId(loginReqDto.loginId())) {
                return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER, "계정을 찾을 수 없습니다.");
            }
            // 비밀번호 검증(암호화 된 비밀번호 비교)
            if( loginReqDto.password() == null || loginReqDto.password().isEmpty() ||
                    !passwordEncoder.matches(loginReqDto.password(), userRepository.findPasswordByLoginId(loginReqDto.loginId()))){
                return new ResponseResult( ErrorCode.NOT_EQUALS_PASSWORD,"잘못된 비밀번호 입니다.");
            }

            User user = userRepository.findByLoginId(loginReqDto.loginId())
                    .orElseThrow(NoUserDataException::new);

            // 로그인하면 Redis에 유저데이터 캐싱
            userRepository.findByUserIdWithCache(user.getUserId());

            String accessToken = jwtProvider.createAccessToken(user.getUserId());
            String refreshToken = jwtProvider.createRefreshToken(user.getUserId());

            //refreshToken - 쿠키
            ResponseCookie refreshCookie = getRefreshToken(refreshToken);
            response.setHeader("Set-Cookie", refreshCookie.toString());

            //accessToken - 바디
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("accessToken", accessToken);

            return new ResponseResult(responseBody);
        } catch (Exception e) {
            log.info("[ERROR] " + e.getMessage());
            return new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Redis에서 유저 캐시 삭제
    private void evictUserCache(Long userId) {
        String key = "user::" + userId;
        redisTemplate.delete(key);  // 직접 삭제
    }

    // 로그아웃
    public ResponseResult logout(HttpServletResponse response, HttpServletRequest request) {
        try {
            //클라이언트 - 토큰 삭제
            ResponseCookie refreshCookie = ResponseCookie
                    .from("REFRESHTOKEN", "") //쿠키 삭제시 빈문자열
                    .domain(domain)
                    .path("/")
                    .httpOnly(true)
                    .secure(secure)
                    .maxAge(0)
                    .build();
            response.setHeader("Set-Cookie", refreshCookie.toString());//쿠키 삭제 요청

            String accessToken = jwtProvider.resolveAccessToken(request); // 요청에서 액세스 토큰 추출
            Long userId = jwtProvider.getUserId(accessToken); // 액세스 토큰을 넘겨서 userId 추출

            tokenService.deleteRefreshToken(userId);
            this.evictUserCache(userId);

            // SSE 연결 해제
            alertService.disconnectAlarm(userId);

            return new ResponseResult(ErrorCode.SUCCESS,"로그아웃 되었습니다.");

        } catch (Exception e) {
            log.info("[ERROR] " + e.getMessage());
            return new ResponseResult(ErrorCode. INTERNAL_SERVER_ERROR,"로그아웃 중 오류가 발생했습니다.");
        }
    }

    // loginId 중복 확인
    public ResponseResult checkLoginiId(String loginId) {
        try {
            if (userRepository.existsByLoginId(loginId)) {
                return new ResponseResult(ErrorCode.DUPLICATE_LOGINID);
            } else {
                return new ResponseResult(ErrorCode.NOT_DUPLICATE_LOGINID);
            }
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.DB_ERROR);
        }
    }

    // 타 유저 프로필
    public ResponseResult getProfile(String tag) {
        try {
            User user = userRepository.findByLoginId(tag)
                    .orElseThrow(NoUserDataException::new);

            UserMyPageDto result = userRepository.findUserMyPageDtoByLoginId(user.getLoginId())
                    .orElseThrow(NoUserDataException::new);
          
            return new ResponseResult(ErrorCode.SUCCESS, result);
        } catch (NoUserDataException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER);
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.DB_ERROR, e.getMessage());
        }
    }

    // 토큰 재발급(리프레시토큰만 가지고 재발급)
    public ResponseResult reissueToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = jwtProvider.resolveRefreshToken(request);
        log.info("[REFRESH_TOKEN] " + refreshToken);

        if (refreshToken == null) {
            logout(response,request);
            return new ResponseResult(ErrorCode.TOKEN_INVALID, "RT가 존재하지 않거나 만료되었습니다.");
        }

        // RefreshToken 유효성 확인
        if (jwtProvider.validateToken(refreshToken)) {
            Long userId = jwtProvider.getUserId(refreshToken);

            // Redis에서 리프레시 토큰 가져오기
            String redisRefreshToken = tokenService.getRefreshToken(userId);

            // Redis에서 리프레시 토큰을 확인하고, 일치하면 새 액세스 토큰 발급
            if (redisRefreshToken != null && redisRefreshToken.equals(refreshToken)) {
                String newAccessToken = jwtProvider.createAccessToken(userId);
                String newRefreshToken = jwtProvider.createRefreshToken(userId);

                //레디스에 새로운 리프레시 토큰 업데이트!
                tokenService.updateRefreshToken(userId, newRefreshToken);

                ResponseCookie refreshCookie = getRefreshToken(newRefreshToken);
                response.setHeader("Set-Cookie", refreshCookie.toString());

                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("accessToken", newAccessToken);

                // Redis에서 user::userID TTL을 30분으로 다시 갱신
                redisTemplate.expire("user::" + userId, 30L * 60, TimeUnit.SECONDS);

                return new ResponseResult(ErrorCode.SUCCESS, responseBody);
            } else {
                return new ResponseResult(ErrorCode.TOKEN_INVALID, "리프레시 토큰이 일치하지 않습니다.");
            }
        } else {
            return new ResponseResult(ErrorCode.TOKEN_INVALID, "리프레시 토큰이 유효하지 않습니다.");
        }
    }
}
