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
import org.dfbf.soundlink.global.comm.enums.Emotions;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.junit.jupiter.api.DisplayName;
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


    @DisplayName("감정기록 작성(제목,가수,앨범,스포티파이아이디,감정,멘트):성공")
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
    @DisplayName("감정기록 삭제:성공")
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


    @DisplayName("감정기록 DB오류 : 실패")
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

    @Test
    @DisplayName("감정 기록 수정 성공 테스트")
    void updateEmotionRecord_Success() {
        // given
        Long recordId = 1L;
        EmotionRecordUpdateRequestDTO updateDTO = new EmotionRecordUpdateRequestDTO(
                "spotify1233", "New Title", "New Artist", "New Image", "HAPPY", "Test comment"
        );

        // 기존 감정 기록 및 음악 정보
        SpotifyMusic existingMusic = new SpotifyMusic("spotify123", "Old Title", "Old Artist", "Old Image");
        EmotionRecord existingRecord = new EmotionRecord(
                mock(User.class), Emotions.SAD, "Old Comment", existingMusic
        );

        // 기존 감정 기록 조회
        when(emotionRecordRepository.findByRecordId(recordId)).thenReturn(Optional.of(existingRecord));
        when(spotifyMusicRepository.findBySpotifyId(updateDTO.spotifyId())).thenReturn(Optional.of(existingMusic));

        // when
        ResponseResult result = emotionRecordService.updateEmotionRecord(recordId, updateDTO);

        // then
        assertEquals(200, result.getCode());
        verify(emotionRecordRepository).findByRecordId(recordId);
        verify(spotifyMusicRepository).findBySpotifyId(updateDTO.spotifyId());
        verify(emotionRecordRepository, never()).save(any()); // update는 save 호출 안 함
    }



}
