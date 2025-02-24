package org.dfbf.soundlink.domain.user.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;
import org.dfbf.soundlink.domain.emotionRecord.repository.EmotionRecordRepository;
import org.dfbf.soundlink.domain.emotionRecord.repository.SpotifyMusicRepository;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

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

    private RedisTemplate<String, String> redisTemplate;
    private final TokenService tokenService;
  
    private final String domain = "";
  
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
            User user = userRepository.findById(userId).orElseThrow(() -> new NoUserDataException());
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
            User user = userRepository.findById(userId).orElseThrow(NoUserDataException::new);

            // SpotifyMusic 객체 찾기 (없으면 새로 생성 & 저장)
            SpotifyMusic spotifyMusic = spotifyMusicRepository.findById(userUpdateDto.spotifyId())
                    .orElseGet(() -> {
                        SpotifyMusic sm = new SpotifyMusic(userUpdateDto);
                        spotifyMusicRepository.save(sm);
                        return sm;
                    });

            user.update(userUpdateDto, passwordEncoder, spotifyMusic);

            return new ResponseResult(ErrorCode.SUCCESS);
        } catch (NoUserDataException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER);
        }
    }

    // 회원정보 삭제
    @Transactional
    public ResponseResult deleteUser(Long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new NoUserDataException());

            emotionRecordRepository.deleteByUser(user); // 유저 감정 기록 삭제
            userRepository.deleteById(userId);          // 유저 삭제

            return new ResponseResult(ErrorCode.SUCCESS);
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
            User user = userRepository.findById(userId).orElseThrow(() -> new NoUserDataException());

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

    // RefreshToken을 쿠키로 설정
    private ResponseCookie getRefreshToken(String refreshToken) {
        return ResponseCookie
                .from("REFRESHTOKEN", refreshToken)
                .domain(domain)
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(1800000) // 만료시간 설정
                .build();
    }
  
    // 로그인
    public ResponseResult login(LoginReqDto loginReqDto, HttpServletResponse response) {
        try {
            if(!userRepository.existsByLoginId(loginReqDto.loginId())) {
                return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER, "계정을 찾을 수 없습니다.");
            }
            // 비밀번호 검증(암호화 된 비밀번호 비교)
            if(!passwordEncoder.matches(loginReqDto.password(), userRepository.findPasswordByLoginId(loginReqDto.loginId()))){
                return new ResponseResult( ErrorCode.NOT_EQUALS_PASSWORD,"잘못된 비밀번호 입니다.");
            }

            User user = userRepository.findByLoginId(loginReqDto.loginId())
                    .orElseThrow(NoUserDataException::new);

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
            System.out.println("[ERROR] " + e.getMessage());
            return new ResponseResult(ErrorCode. INTERNAL_SERVER_ERROR);
        }
    }

    // 로그아웃
    public ResponseResult logout(HttpServletResponse response, HttpServletRequest request) {
        try {
            //클라이언트 - 토큰 삭제
            ResponseCookie refreshCookie = ResponseCookie
                    .from("REFRESHTOKEN", "localhost")
                    .domain(domain)
                    .path("/")
                    .httpOnly(true)
                    .secure(false)
                    .maxAge(0)
                    .build();
            response.setHeader("Set-Cookie", refreshCookie.toString());//쿠키 삭제 요청

            String accessToken = jwtProvider.resolveAccessToken(request); // 요청에서 액세스 토큰 추출
            Long userId = jwtProvider.getUserId(accessToken); // 액세스 토큰을 넘겨서 userId 추출

            tokenService.deleteRefreshToken(userId);

            return new ResponseResult(ErrorCode.SUCCESS,"로그아웃 되었습니다.");

        } catch (Exception e) {
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
                    .orElseThrow(() -> new NoUserDataException());

            UserMyPageDto result = userRepository.findUserMyPageDtoByLoginId(user.getLoginId())
                    .orElseThrow(() -> new NoUserDataException());
          
            return new ResponseResult(ErrorCode.SUCCESS, result);
        } catch (NoUserDataException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER);
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.DB_ERROR, e.getMessage());
        }
    }

    // 토큰 재발급
    public ResponseResult reissueToken(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtProvider.resolveAccessToken(request);
        String refreshToken = jwtProvider.resolveRefreshToken(request);
        System.out.println("AccessToken: " + accessToken);
        System.out.println("RefreshToken from Cookie: " + refreshToken);

//        System.out.println("AccessToken: " + accessToken);
//        System.out.println("RefreshToken from Cookie: " + refreshToken);

        // AccessToken과 RefreshToken이 모두 없는 경우
        if (accessToken == null && refreshToken == null) {
            logout(response,request);
            return new ResponseResult(ErrorCode.TOKEN_INVALID, "토큰이 존재하지 않거나 만료되었습니다.");
        }

        if (accessToken == null) {
            logout(response,request);
            return new ResponseResult(ErrorCode.TOKEN_INVALID, "AT가 존재하지 않거나 만료되었습니다.");
        }

        if (refreshToken == null) {
            logout(response,request);
            return new ResponseResult(ErrorCode.TOKEN_INVALID, "RT가 존재하지 않거나 만료되었습니다.");
        }

        // AccessToken 유효성 확인
        if (jwtProvider.validateToken(accessToken)) {
            return new ResponseResult(ErrorCode.TOKEN_NOT_EXPIRED); // 유효한 액세스 토큰: 재발급 x
        }

        // RefreshToken 유효성 확인
        if (jwtProvider.validateToken(refreshToken)) {
            Long userId = jwtProvider.getUserId(refreshToken);

            // Redis에서 리프레시 토큰 가져오기
            String redisRefreshToken = tokenService.getRefreshToken(userId);

            // Redis에서 리프레시 토큰을 확인하고, 일치하면 새 액세스 토큰 발급
            if (redisRefreshToken != null && redisRefreshToken.equals(refreshToken)) {
                String newAccessToken = jwtProvider.createAccessToken(userId);

//                System.out.println("New AccessToken: " + newAccessToken);

                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("accessToken", newAccessToken);
                response.setHeader("Set-Cookie", refreshToken);

                return new ResponseResult(ErrorCode.SUCCESS, responseBody);
            } else {
                return new ResponseResult(ErrorCode.TOKEN_INVALID, "리프레시 토큰이 일치하지 않습니다.");
            }
        } else {
            return new ResponseResult(ErrorCode.TOKEN_INVALID, "리프레시 토큰이 유효하지 않습니다.");
        }
    }
}
