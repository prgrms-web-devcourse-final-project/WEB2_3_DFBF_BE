package org.dfbf.soundlink.domain.emotionRecord;

import org.dfbf.soundlink.domain.chat.repository.ChatRoomRepository;
import org.dfbf.soundlink.domain.emotionRecord.dto.request.EmotionRecordRequestDTO;
import org.dfbf.soundlink.domain.emotionRecord.dto.request.EmotionRecordUpdateRequestDTO;
import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;
import org.dfbf.soundlink.domain.emotionRecord.repository.EmotionRecordRepository;
import org.dfbf.soundlink.domain.emotionRecord.repository.SpotifyMusicRepository;
import org.dfbf.soundlink.domain.emotionRecord.service.EmotionRecordService;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.global.comm.enums.Emotions;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Optional;

import static org.dfbf.soundlink.global.comm.enums.Emotions.HAPPY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmotionRecordServiceTest {
    @InjectMocks
    private EmotionRecordService emotionRecordService;
    @Mock
    private EmotionRecordRepository emotionRecordRepository;
    @Mock
    private SpotifyMusicRepository spotifyMusicRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;


    @DisplayName("감정기록 작성(제목, 가수, 앨범, 스포티파이 아이디, 감정, 멘트): 성공")
    @Test
    void saveEmotionRecordWithMusic_SUCCESS() {
        // given
        Long userId = 1L;
        EmotionRecordRequestDTO requestDTO = new EmotionRecordRequestDTO(
                "spotify1233", "New videoId", "New Title", "New Artist", "New Image", HAPPY, "Test comment"
        );

        User mockUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(spotifyMusicRepository.findBySpotifyId(requestDTO.spotifyId())).thenReturn(Optional.empty());

        SpotifyMusic newSpotifyMusic = SpotifyMusic.builder()
                .spotifyId(requestDTO.spotifyId())
                .videoId(requestDTO.videoId())
                .title(requestDTO.title())
                .artist(requestDTO.artist())
                .albumImage(requestDTO.albumImage())
                .build();
        when(spotifyMusicRepository.save(any(SpotifyMusic.class))).thenReturn(newSpotifyMusic);

        EmotionRecord newEmotionRecord = mock(EmotionRecord.class);
        when(emotionRecordRepository.save(any(EmotionRecord.class))).thenReturn(newEmotionRecord);

        // when
        ResponseResult result = emotionRecordService.saveEmotionRecordWithMusic(userId, requestDTO);

        // then
        assertEquals(HttpStatus.OK.value(), result.getCode());
        verify(spotifyMusicRepository).save(any(SpotifyMusic.class));
        verify(emotionRecordRepository).save(any(EmotionRecord.class));
    }

    @DisplayName("감정기록 삭제: 성공")
    @Test
    void deleteEmotionRecord_SUCCESS() {
        // given
        Long recordId = 1L;
        // delete 호출 전, 해당 record가 존재함을 확인하기 위한 stub 추가
        EmotionRecord mockRecord = mock(EmotionRecord.class);
        when(emotionRecordRepository.findByRecordId(recordId)).thenReturn(Optional.of(mockRecord));
        // 채팅방 조회 시 빈 리스트 반환 (삭제할 채팅방 없음)
        when(chatRoomRepository.findByRecordId(mockRecord)).thenReturn(Collections.emptyList());
        when(emotionRecordRepository.deleteByRecordId(recordId)).thenReturn(1);

        // when
        ResponseResult result = emotionRecordService.deleteEmotionRecord(recordId);

        // then
        assertEquals(HttpStatus.OK.value(), result.getCode());
        verify(emotionRecordRepository).deleteByRecordId(recordId);
    }

    @Test
    @DisplayName("감정 기록 수정: 성공")
    void updateEmotionRecord_Success() {
        // given
        Long recordId = 1L;
        EmotionRecordUpdateRequestDTO updateDTO = new EmotionRecordUpdateRequestDTO(
                "spotify1233", "New videoId", "New Title", "New Artist", "New Image", "HAPPY", "Test comment"
        );

        SpotifyMusic existingMusic = new SpotifyMusic("spotify1233", "Old videoId", "Old Title", "Old Artist", "Old Image");
        EmotionRecord existingRecord = new EmotionRecord(mock(User.class), Emotions.SAD, "Old Comment", existingMusic);

        // 기존 감정 기록 조회 stub
        when(emotionRecordRepository.findByRecordId(recordId)).thenReturn(Optional.of(existingRecord));
        when(spotifyMusicRepository.findBySpotifyId(updateDTO.spotifyId())).thenReturn(Optional.of(existingMusic));

        // when
        ResponseResult result = emotionRecordService.updateEmotionRecord(recordId, updateDTO);

        // then
        assertEquals(HttpStatus.OK.value(), result.getCode());
        verify(emotionRecordRepository).findByRecordId(recordId);
        verify(spotifyMusicRepository).findBySpotifyId(updateDTO.spotifyId());
        verify(emotionRecordRepository, never()).save(any()); // update는 save 호출 안 함
    }
}
