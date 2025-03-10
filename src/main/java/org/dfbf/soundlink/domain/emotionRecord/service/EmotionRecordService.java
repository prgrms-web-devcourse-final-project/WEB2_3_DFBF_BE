package org.dfbf.soundlink.domain.emotionRecord.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dfbf.soundlink.domain.emotionRecord.dto.request.EmotionRecordRequestDTO;
import org.dfbf.soundlink.domain.emotionRecord.dto.request.EmotionRecordUpdateRequestDTO;
import org.dfbf.soundlink.domain.emotionRecord.dto.response.*;
import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.emotionRecord.entity.SpotifyMusic;
import org.dfbf.soundlink.domain.emotionRecord.exception.EmotionRecordNotFoundException;
import org.dfbf.soundlink.domain.emotionRecord.exception.SpotifyMusicNotFoundException;
import org.dfbf.soundlink.domain.emotionRecord.exception.UserNotFoundException;
import org.dfbf.soundlink.domain.emotionRecord.repository.EmotionRecordRepository;
import org.dfbf.soundlink.domain.emotionRecord.repository.SpotifyMusicRepository;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
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

    private final EmotionRecordCacheService emotionRecordCacheService;

    @Transactional
    public ResponseResult saveEmotionRecordWithMusic(Long userId, EmotionRecordRequestDTO request) {

        User loggedInUser = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        try {
            // 음악 저장
            SpotifyMusic spotifyMusic = SpotifyMusic.builder()
                    .spotifyId(request.spotifyId())
                    .videoId(request.videoId())
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

            // 게시글 생성 시, 해당 조건에 맞는 캐시 키 삭제
            emotionRecordCacheService.evictEmotionRecordCache(
                    userId,
                    request.spotifyId(),
                    request.emotion().name()
            );

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
        ResponseResult pageValidationResult = validateAndCreatePageable(page, size);
        if (pageValidationResult.getCode() != 200 /*SUCCESS*/) {
            return pageValidationResult;
        }

        Pageable pageable = (Pageable) pageValidationResult.getData();

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

    // 동적 검색 (로그인 사용자를 제외한 EmotionRecord 조회) - 캐시 적용
    @Transactional(readOnly = true)
    public ResponseResult getEmotionRecordsExcludingUserIdByFilters(Long userId, List<String> emotionList, String spotifyId, int page, int size) {
        ResponseResult pageValidationResult = validateAndCreatePageable(page, size);
        if (pageValidationResult.getCode() != 200 /*SUCCESS*/) {
            return pageValidationResult;
        }

        try {
            // EmotionRecordCacheService의 결합 캐시 조회 및 Fallback 처리
            EmotionRecordPageResponseDTO<EmotionRecordResponseMainDTO> result =
                    emotionRecordCacheService.getEmotionRecords(userId, emotionList, spotifyId, page, size);
            return new ResponseResult(ErrorCode.SUCCESS, result);
        } catch (IllegalArgumentException e) {
            // 잘못된 감정 값이 포함된 경우
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_EMOTION, e.getMessage());
        } catch (DataAccessException e) {
            return new ResponseResult(ErrorCode.DB_ERROR, e.getMessage());
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ResponseResult getEmotionRecord(Long userId, Long recordId) {

        try {
            EmotionRecord records = emotionRecordRepository.findByRecordId(recordId)
                    .orElseThrow(EmotionRecordNotFoundException::new);

            return new ResponseResult(ErrorCode.SUCCESS, EmotionRecordResponseWithOwnerDTO.fromEntity(records, userId));
        } catch (EmotionRecordNotFoundException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_EMOTION_RECORD, e.getMessage());
        } catch (DataAccessException e) {
            return new ResponseResult(ErrorCode.DB_ERROR, e.getMessage());
        } catch (Exception e) {
            return new ResponseResult(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ResponseResult getVideoIdBySpotifyId(String spotifyId) {

        try {
            SpotifyMusic music = spotifyMusicRepository.findBySpotifyId(spotifyId)
                    .orElseThrow(SpotifyMusicNotFoundException::new);

            String videoId = music.getVideoId();
            return new ResponseResult(ErrorCode.SUCCESS, videoId);
        } catch (SpotifyMusicNotFoundException e) {
            return new ResponseResult(ErrorCode.FAIL_TO_FIND_SPOTIFY_MUSIC, e.getMessage());
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
                        SpotifyMusic newMusic = SpotifyMusic.builder()
                                .spotifyId(updateDTO.spotifyId())
                                .title(updateDTO.title())
                                .artist(updateDTO.artist())
                                .albumImage(updateDTO.albumImage())
                                .build();

                        return spotifyMusicRepository.save(newMusic);
                    });

            record.updateEmotionRecord(updateDTO.emotion(), updateDTO.comment(), spotifyMusic);

            // 수정된 정보를 Response DTO로 변환
            EmotionRecordUpdateResponseDTO responseDTO = EmotionRecordUpdateResponseDTO.fromEntity(record);

            // 게시글 수정 시, 해당 조건에 맞는 캐시 키 삭제
            emotionRecordCacheService.evictEmotionRecordCache(
                    record.getUser().getUserId(),
                    updateDTO.spotifyId(),
                    updateDTO.emotion()
            );
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
            EmotionRecord record = emotionRecordRepository.findByRecordId(recordId)
                    .orElseThrow(EmotionRecordNotFoundException::new);

            int deletedCount = emotionRecordRepository.deleteByRecordId(recordId);

            // 게시글 삭제 시, 해당 조건에 맞는 캐시 키 삭제
            emotionRecordCacheService.evictEmotionRecordCache(
                    record.getUser().getUserId(),
                    record.getSpotifyMusic().getSpotifyId(),
                    record.getEmotion().name()
            );

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

    // 페이지네이션 처리 및 관련 예외처리
    private ResponseResult validateAndCreatePageable(int page, int size) {
        if (page < 1) {
            return new ResponseResult(ErrorCode.INVALID_PAGE_REQUEST, "페이지 번호는 1 이상이어야 합니다.");
        }
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
            return new ResponseResult(ErrorCode.SUCCESS, pageable);
        } catch (IllegalArgumentException e) {
            return new ResponseResult(ErrorCode.INVALID_PAGE_REQUEST, "페이지 요청 값이 잘못되었습니다.");
        }
    }

}
