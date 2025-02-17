package org.dfbf.soundlink.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dfbf.soundlink.domain.emotionReocrd.entity.SpotifyMusic;
import org.dfbf.soundlink.domain.emotionReocrd.repository.SpotifyMusicRepository;
import org.dfbf.soundlink.domain.user.dto.request.UserSignUpDto;
import org.dfbf.soundlink.domain.user.dto.request.UserUpdateDto;
import org.dfbf.soundlink.domain.user.dto.response.UserGetResDto;
import org.dfbf.soundlink.domain.user.entity.ProfileMusic;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.exception.NoUserDataException;
import org.dfbf.soundlink.domain.user.repository.ProfileMusicRepository;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileMusicRepository profileMusicRepository;
    private final SpotifyMusicRepository spotifyMusicRepository;

    // 회원가입
    public ResponseResult signUp(UserSignUpDto userSignUpDto) {
        try {
            userRepository.save(userSignUpDto.toEntity());
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
            UserGetResDto result = new UserGetResDto(user);

            return new ResponseResult(ErrorCode.SUCCESS, result);
        } catch (NoUserDataException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER);
        }
    }

    // 회원정보 수정
    @Transactional
    public ResponseResult updateUser(Long userId, UserUpdateDto userUpdateDto) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new NoUserDataException());
            user.update(userUpdateDto);

            // SpotifyMusic 객체 찾고 없으면 새로 생성
            SpotifyMusic spotifyMusic = spotifyMusicRepository.findById(userUpdateDto.spotifyId())
                    .orElse(new SpotifyMusic(userUpdateDto.spotifyId(), userUpdateDto.title(), userUpdateDto.artist(), userUpdateDto.albumImage()));

            // SpotifyMusic 저장
            spotifyMusicRepository.save(spotifyMusic);

            // ProfileMusic 객체 찾고 없으면 새로 생성
            ProfileMusic profileMusic = profileMusicRepository.findByUserId(userId)
                    .orElse(new ProfileMusic(user, spotifyMusic));

            // ProfileMusic이 업데이트
            profileMusic.update(spotifyMusic);
            profileMusicRepository.save(profileMusic);

            return new ResponseResult(ErrorCode.SUCCESS);
        } catch (NoUserDataException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER);
        }
    }
}
