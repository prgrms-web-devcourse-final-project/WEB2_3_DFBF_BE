package org.dfbf.soundlink.domain.emotionRecord.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dfbf.soundlink.domain.emotionRecord.dto.request.EmotionRecordRequestDTO;
import org.dfbf.soundlink.domain.emotionRecord.dto.response.EmotionRecordResponseDTO;
import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;
import org.dfbf.soundlink.domain.emotionRecord.exception.EmotionRecordNotFoundException;
import org.dfbf.soundlink.domain.emotionRecord.exception.UserNotFoundException;
import org.dfbf.soundlink.domain.emotionRecord.repository.EmotionRecordRepository;
import org.dfbf.soundlink.domain.emotionRecord.repository.SpotifyMusicRepository;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmotionRecordService {

    private final SpotifyMusicRepository spotifyMusicRepository;
    private final EmotionRecordRepository emotionRecordRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseResult saveEmotionRecordWithMusic(EmotionRecordRequestDTO request) {
        // 임시 유저 생성 (시큐리티 적용 이후 수정 필요)
        User testUser = userRepository.findById(1L)
                .orElseThrow(UserNotFoundException::new);

        try {
            // 감정 기록 저장
            EmotionRecord emotionRecord = EmotionRecord.builder()
                    .user(testUser)
                    .emotion(request.emotion())
                    .comment(request.comment())
                    .build();
            emotionRecordRepository.save(emotionRecord);

            // 음악 저장
            SpotifyMusic spotifyMusic = SpotifyMusic.builder()
                    .spotifyId(request.spotifyId())
                    .title(request.title())
                    .artist(request.artist())
                    .albumImage(request.albumImage())
                    .build();
            spotifyMusicRepository.save(spotifyMusic);

            return new ResponseResult(ErrorCode.SUCCESS);
        } catch (UserNotFoundException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER, e.getMessage());
        } catch (DataAccessException e) {
            return new ResponseResult(ErrorCode.DB_ERROR, e.getMessage());
        } catch (Exception e) {
            log.error("감정기록 저장 서버 에러 {}", e.getMessage());
            return new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ResponseResult getEmotionRecordsByUserId(Long userId) {

        try {
            List<EmotionRecord> records = emotionRecordRepository.findByUserId(userId);
            return new ResponseResult(ErrorCode.SUCCESS, EmotionRecordResponseDTO.fromEntities(records));
        } catch (UserNotFoundException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER, e.getMessage());
        } catch (DataAccessException e) {
            return new ResponseResult(ErrorCode.DB_ERROR, e.getMessage());
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ResponseResult getEmotionRecord(Long recordId) {

        try {
            EmotionRecord records = emotionRecordRepository.findByRecordId(recordId)
                    .orElseThrow(EmotionRecordNotFoundException::new);
            return new ResponseResult(ErrorCode.SUCCESS, EmotionRecordResponseDTO.fromEntity(records));
        } catch (EmotionRecordNotFoundException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_EMOTION_RECORD, e.getMessage());
        } catch (DataAccessException e) {
            return new ResponseResult(ErrorCode.DB_ERROR, e.getMessage());
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
