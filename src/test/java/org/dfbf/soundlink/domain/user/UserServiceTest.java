package org.dfbf.soundlink.domain.user;

import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;
import org.dfbf.soundlink.domain.emotionRecord.repository.SpotifyMusicRepository;
import org.dfbf.soundlink.domain.user.dto.request.UserUpdateDto;
import org.dfbf.soundlink.domain.user.entity.ProfileMusic;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.repository.ProfileMusicRepository;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.domain.user.service.UserService;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SpotifyMusicRepository spotifyMusicRepository;

    @Mock
    private ProfileMusicRepository profileMusicRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder; // @MockBean으로 빈을 주입


    // 회원 정보 수정
    @Test
    public void testUpdateUser() {
        // Given
        Long userId = 1L;
        UserUpdateDto userUpdateDto = mock(UserUpdateDto.class);
        User existingUser = mock(User.class);
        SpotifyMusic spotifyMusic = mock(SpotifyMusic.class);
        ProfileMusic profileMusic = mock(ProfileMusic.class);


        // When
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userUpdateDto.spotifyId()).thenReturn(123L);
        when(spotifyMusicRepository.findById(123L)).thenReturn(Optional.empty()); // Will be created
        // when(profileMusicRepository.findByUserId(userId)).thenReturn(Optional.empty()); // Will be created

        ResponseResult result = userService.updateUser(userId, userUpdateDto);


        // Then
        assertEquals(200, result.getCode());
        assertEquals("성공", result.getMessage());

        verify(spotifyMusicRepository).save(any(SpotifyMusic.class));
        verify(profileMusicRepository).save(any(ProfileMusic.class));

        verify(existingUser).update(eq(userUpdateDto), any(BCryptPasswordEncoder.class));
    }

}
