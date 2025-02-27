package org.dfbf.soundlink.domain.emotionRecord.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dfbf.soundlink.domain.emotionRecord.dto.request.EmotionRecordRequestDTO;
import org.dfbf.soundlink.domain.emotionRecord.dto.request.EmotionRecordUpdateRequestDTO;
import org.dfbf.soundlink.domain.emotionRecord.dto.response.*;
import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;
import org.dfbf.soundlink.domain.emotionRecord.exception.EmotionRecordNotFoundException;
import org.dfbf.soundlink.domain.emotionRecord.exception.UserNotFoundException;
import org.dfbf.soundlink.domain.emotionRecord.repository.EmotionRecordRepository;
import org.dfbf.soundlink.domain.emotionRecord.repository.SpotifyMusicRepository;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.global.comm.enums.Emotions;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseResult saveEmotionRecordWithMusic(Long userId, EmotionRecordRequestDTO request) {

        User loggedInUser = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        try {
            // 음악 저장
            SpotifyMusic spotifyMusic = SpotifyMusic.builder()
                    .spotifyId(request.spotifyId())
                    .title(request.title())
                    .artist(request.artist())
                    .albumImage(request.albumImage())
                    .build();
            spotifyMusicRepository.save(spotifyMusic);

            // 감정 기록 저장
            EmotionRecord emotionRecord = EmotionRecord.builder()
                    .user(loggedInUser)
                    .emotion(request.emotion())
                    .comment(request.comment())
                    .spotifyMusic(spotifyMusic)
                    .build();
            emotionRecordRepository.save(emotionRecord);

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
    public ResponseResult getEmotionRecordsByLoginId(String userTag, int page, int size) {
        Pageable pageable;

        try {
            pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        } catch (IllegalArgumentException e) {
            return new ResponseResult(ErrorCode.INVALID_PAGE_REQUEST, "페이지 요청 값이 잘못되었습니다.");
        }
        try {
            Page<EmotionRecord> recordsPage = emotionRecordRepository.findByLoginId(userTag, pageable);

            List<EmotionRecordResponseWithoutNicknameDTO> dtoList = recordsPage.getContent()
                    .stream()
                    .map(EmotionRecordResponseWithoutNicknameDTO::fromEntity)
                    .toList();

            return new ResponseResult(ErrorCode.SUCCESS, EmotionRecordPageResponseDTO.fromPage(recordsPage, dtoList));
        } catch (DataAccessException e) {
            return new ResponseResult(ErrorCode.DB_ERROR, e.getMessage());
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public ResponseResult getEmotionRecordsExcludingUserIdByFilters(Long userId, List<String> emotionList, String spotifyId, int page, int size) {
        Pageable pageable;
        List<Emotions> emotionEnums = null;

        try {
            pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        } catch (IllegalArgumentException e) {
            return new ResponseResult(ErrorCode.INVALID_PAGE_REQUEST, "페이지 요청 값이 잘못되었습니다.");
        }

        if (emotionList != null && !emotionList.isEmpty()) {
            try {
                emotionEnums = emotionList.stream()
                        .map(e -> Emotions.valueOf(e.toUpperCase()))
                        .toList();
            } catch (IllegalArgumentException e) {
                return new ResponseResult(ErrorCode.FAIL_TO_FIND_EMOTION, "잘못된 감정 값이 포함되어 있습니다.");
            }
        }

        try {
            Page<EmotionRecord> recordsPage = emotionRecordRepository.findByFilters(userId, emotionEnums, spotifyId, pageable);
            List<EmotionRecordResponseMainDTO> dtoList = recordsPage.getContent()
                    .stream()
                    .map(EmotionRecordResponseMainDTO::fromEntity)
                    .toList();

            return new ResponseResult(ErrorCode.SUCCESS, EmotionRecordPageResponseDTO.fromPage(recordsPage, dtoList));
        } catch (DataAccessException e) {
            return new ResponseResult(ErrorCode.DB_ERROR, e.getMessage());
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ResponseResult getEmotionRecord(Long userId, Long recordId) {

        try {
            User loggedInUser = userRepository.findById(userId)
                    .orElseThrow(UserNotFoundException::new);

            EmotionRecord records = emotionRecordRepository.findByRecordId(recordId)
                    .orElseThrow(EmotionRecordNotFoundException::new);

            return new ResponseResult(ErrorCode.SUCCESS, EmotionRecordResponseWithOwnerDTO.fromEntity(records, userId, loggedInUser.getLoginId()));
        } catch (UserNotFoundException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_USER, e.getMessage());
        } catch (EmotionRecordNotFoundException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_EMOTION_RECORD, e.getMessage());
        } catch (DataAccessException e) {
            return new ResponseResult(ErrorCode.DB_ERROR, e.getMessage());
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional
    public ResponseResult updateEmotionRecord(Long recordId, EmotionRecordUpdateRequestDTO updateDTO) {
        try {
            // 기존 감정 기록 조회
            EmotionRecord record = emotionRecordRepository.findByRecordId(recordId)
                    .orElseThrow(EmotionRecordNotFoundException::new);

            // SpotifyMusic이 DB에 있는지 먼저 확인
            // SpotifyMusic 엔티티가 저장되지 않은 상태에서 EmotionRecord 저장 시 영속성 컨텍스트 미저장 오류 발생
            // EmotionRecord를 업데이트하기 전에 SpotifyMusic이 없다면 생성 후 먼저 저장해줘야 함
            SpotifyMusic spotifyMusic = spotifyMusicRepository.findBySpotifyId(updateDTO.spotifyId())
                    .orElseGet(() -> {
                        SpotifyMusic newMusic = new SpotifyMusic(
                                updateDTO.spotifyId(),
                                updateDTO.title(),
                                updateDTO.artist(),
                                updateDTO.albumImage()
                        );
                        return spotifyMusicRepository.save(newMusic);
                    });

            record.updateEmotionRecord(updateDTO.emotion(), updateDTO.comment(), spotifyMusic);

            // 수정된 정보를 Response DTO로 변환
            EmotionRecordUpdateResponseDTO responseDTO = EmotionRecordUpdateResponseDTO.fromEntity(record);

            return new ResponseResult(ErrorCode.SUCCESS, responseDTO);
        } catch (EmotionRecordNotFoundException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_EMOTION_RECORD, e.getMessage());
        } catch (DataAccessException e) {
            return new ResponseResult(ErrorCode.DB_ERROR, e.getMessage());
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional
    public ResponseResult deleteEmotionRecord(Long recordId) {
        try {
            int deletedCount = emotionRecordRepository.deleteByRecordId(recordId);

            // 삭제할 데이터가 없는 경우
            if (deletedCount == 0) {
                return new ResponseResult(ErrorCode.SUCCESS, "이미 삭제되었거나 존재하지 않는 감정 기록입니다.");
            }
            return new ResponseResult(ErrorCode.SUCCESS, "감정 기록이 성공적으로 삭제되었습니다.");
        } catch (EmotionRecordNotFoundException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_EMOTION_RECORD, e.getMessage());
        } catch (DataAccessException e) {
            return new ResponseResult(ErrorCode.DB_ERROR, e.getMessage());
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
