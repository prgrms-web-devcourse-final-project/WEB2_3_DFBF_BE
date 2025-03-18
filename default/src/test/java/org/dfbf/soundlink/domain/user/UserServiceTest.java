package org.dfbf.soundlink.domain.user;

import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;
import org.dfbf.soundlink.domain.emotionRecord.repository.EmotionRecordRepository;
import org.dfbf.soundlink.domain.emotionRecord.repository.SpotifyMusicRepository;
import org.dfbf.soundlink.domain.user.dto.request.UserSignUpDto;
import org.dfbf.soundlink.domain.user.dto.request.UserUpdateDto;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.repository.ProfileMusicRepository;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.domain.user.service.UserService;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.dfbf.soundlink.global.comm.enums.SocialType.KAKAO;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock(lenient = true)
    private UserRepository userRepository;

    @Mock
    private SpotifyMusicRepository spotifyMusicRepository;

    @Mock(lenient = true)
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private EmotionRecordRepository emotionRecordRepository;

    @Mock
    private ProfileMusicRepository profileMusicRepository;

    // 회원 정보 수정
    @Test
    void updateUser_Success() {
        // Given
        Long userId = 1L;
        User user = mock(User.class);

        UserUpdateDto updateDto = new UserUpdateDto( //dto는 mock객체로 사용하지 않음.
                Optional.of("newEmail@example.com"),
                Optional.of("newLoginId"),
                Optional.of("newNickName"),
                Optional.of("newPassword"),
                Optional.of("spotify123"), // Spotify ID 존재
                Optional.of("New Title"),
                Optional.of("New Artist"),
                Optional.of("New Album Image"),
                Optional.of("New VideoId")
        );

        SpotifyMusic spotifyMusic = new SpotifyMusic(updateDto);

        // Mocking
        // Mock 설정
        when(userRepository.findByUserIdWithCache(userId)).thenReturn(Optional.of(user));
        when(spotifyMusicRepository.findListBySpotifyId("spotify123"))
                .thenReturn(Collections.singletonList(spotifyMusic));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When
        ResponseResult result = userService.updateUser(userId, updateDto);
        // Then
        assertEquals(200, result.getCode());
        verify(userRepository).saveWithCache(any(User.class)); //수정 저장 확인(유저,캐시)
        verify(profileMusicRepository).save(any()); //스포티파이 저장 확인
    }



    @Test
    @DisplayName("회원가입 테스트 성공 - 비밀번호 암호화 및 저장")
    void signUp_Success(){
        //given
        UserSignUpDto userSignUpDto = new UserSignUpDto(
                "testNickname", 12345L, KAKAO, "testLoginId", "password123", "test@example.com"
        );
        //when
        //save 메소드 호출 시 모킹 처리
        when(passwordEncoder.encode(userSignUpDto.password())).thenReturn("encodedPassword");

        ResponseResult result = userService.signUp(userSignUpDto);
        //then
        assertEquals(200, result.getCode());

        verify(userRepository, times(1)).save(any(User.class));

        verify(passwordEncoder, times(1)).encode(userSignUpDto.password());
    }

//    @Test
//    @DisplayName("회원삭제 성공")
//    void deleteUser_Success(){
//        //given
//        Long userId = 1L;
//        User user = mock(User.class);
//
//        // 유저를 찾기
//        when(userRepository.findByUserIdWithCache(userId)).thenReturn(Optional.of(user));
//        // 감정 기록 삭제, doNotion()-> 단지 메소드가 호출되었는지만 확인
//        doNothing().when(emotionRecordRepository).deleteByUser(user);
//        // 유저 삭제
//        doNothing().when(userRepository).deleteById(userId);
//
//        //when
//        ResponseResult result = userService.deleteUser(userId);
//
//        //then
//        assertEquals(200, result.getCode());
//        verify(emotionRecordRepository, times(1)).deleteByUser(user); //(1): 1번 호출되었는지 확인 = 감정기록이 삭제 됨.
////        verify(userRepository, times(0)).deleteById(userId);//(0):호출되지 않았는지 확인 = 사용자가 삭제 되지 않음.(1):성공적으로 삭제됨.
//    }
}
