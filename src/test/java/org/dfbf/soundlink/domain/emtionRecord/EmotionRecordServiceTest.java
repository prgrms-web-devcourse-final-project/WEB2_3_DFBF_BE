package org.dfbf.soundlink.domain.emtionRecord;

import org.dfbf.soundlink.domain.emotionRecord.dto.request.EmotionRecordRequestDTO;
import org.dfbf.soundlink.domain.emotionRecord.dto.request.EmotionRecordUpdateRequestDTO;
import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;
import org.dfbf.soundlink.domain.emotionRecord.repository.EmotionRecordRepository;
import org.dfbf.soundlink.domain.emotionRecord.repository.SpotifyMusicRepository;
import org.dfbf.soundlink.domain.emotionRecord.service.EmotionRecordService;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.dfbf.soundlink.global.comm.enums.Emotions.HAPPY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmotionRecordServiceTest {
    @InjectMocks
    private EmotionRecordService emotionRecordService;
    @Mock
    private EmotionRecordRepository emotionRecordRepository;
    @Mock
    private SpotifyMusicRepository spotifyMusicRepository;
    @Mock
    private UserRepository userRepository;

    //감정기록 작성 : 성공
    @Test
    void saveEmotionRecordWithMusic_SUCCESS() {
        // given
        Long userId = 1L;
        EmotionRecordRequestDTO requestDTO = new EmotionRecordRequestDTO(
                "spotify1233", "New Title", "New Artist", "New Image", HAPPY, "Test comment"
        );

        User mockUser = mock(User.class);
        SpotifyMusic newSpotifyMusic = new SpotifyMusic(requestDTO.spotifyId(), requestDTO.title(), requestDTO.artist(), requestDTO.albumImage());
        EmotionRecord newEmotionRecord = mock(EmotionRecord.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser)); // 사용자 조회 성공
        when(spotifyMusicRepository.save(any(SpotifyMusic.class))).thenReturn(newSpotifyMusic); // 음악 저장
        when(emotionRecordRepository.save(any(EmotionRecord.class))).thenReturn(newEmotionRecord); // 감정 기록 저장

        // when
        ResponseResult result = emotionRecordService.saveEmotionRecordWithMusic(userId, requestDTO);

        // then
        assertEquals(HttpStatus.OK.value(), result.getCode());
        verify(spotifyMusicRepository).save(any(SpotifyMusic.class)); // 음악이 저장되었는지 검증
        verify(emotionRecordRepository).save(any(EmotionRecord.class)); // 감정 기록이 저장되었는지 검증
    }

    //감정기록 삭제 : 성공
    @Test
    void deleteEmotionRecord_SUCCESS() {
        //given
        Long recordId = 1L;
        when(emotionRecordRepository.deleteByRecordId(recordId)).thenReturn(1);//삭제된 레코드 수 1개
        //when
        ResponseResult result = emotionRecordService.deleteEmotionRecord(recordId);
        //then
        assertEquals(200,result.getCode());
        verify(emotionRecordRepository).deleteByRecordId(recordId);
    }

    //감정기록 삭제 : 실패(DB 오류)
    @Test
    void deleteEmotionRecord_DataAccessException() {
        // given
        Long recordId = 1L;
        when(emotionRecordRepository.deleteByRecordId(recordId)).thenThrow(new DataAccessException("Database error") {});

        // when
        ResponseResult result = emotionRecordService.deleteEmotionRecord(recordId);

        // then
        assertEquals(ErrorCode.DB_ERROR, result.getCode());
        assertEquals("Database error", result.getMessage());
    }


}
