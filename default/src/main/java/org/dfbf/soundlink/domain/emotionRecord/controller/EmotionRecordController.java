package org.dfbf.soundlink.domain.emotionRecord.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.emotionRecord.dto.request.EmotionRecordRequestDTO;
import org.dfbf.soundlink.domain.emotionRecord.dto.request.EmotionRecordUpdateRequestDTO;
import org.dfbf.soundlink.domain.emotionRecord.service.EmotionRecordService;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emotion")
@RequiredArgsConstructor
@Tag(name = "EmotionRecord API", description = "감정기록 관련 API")
public class EmotionRecordController {

    private final EmotionRecordService emotionRecordService;

    @PostMapping
    @Operation(
            summary = "감정 기록 작성/저장 API",
            description = "작성한 감정 기록을 저장 & Idempotency Key를 사용하여 중복 요청을 방지"
    )
    public ResponseResult saveEmotionWithMusicAndIdempotency(
            @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody EmotionRecordRequestDTO request) {
        return emotionRecordService.saveEmotionRecordWithMusicAndIdempotent(idempotencyKey, userId, request);
    }

    @GetMapping
    @Operation(
            summary = "감정 기록 전체 조회 메인 API",
            description = "유저들이 작성한 감정 기록 전체를 조회합니다. 자신의 아이디에 해당하는 감정 기록은 조회되지 않으며," +
                    " 주어진 spotifyId와 emotion을 가진 감정 기록을 검색 할 수 있습니다. 검색 조건이 없으면 전체 조회합니다."
    )
    public ResponseResult getEmotionRecordsWithoutMine(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) String spotifyId,
            @Parameter(description = "감정 필터(추가 가능)")
            @RequestParam(required = false) List<String> emotions,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return emotionRecordService.getEmotionRecordsExcludingUserIdByFilters(userId, emotions, spotifyId, page, size);
    }

    @GetMapping("/my-emotion-record")
    @Operation(
            summary = "현재 로그인 된 사용자 감정 기록 전체 조회 API",
            description = "현재 로그인 된 사용자가 작성한 감정 기록 전체를 조회합니다."
    )
    public ResponseResult getMyEmotionRecords(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return emotionRecordService.getEmotionRecordsByUserId(userId, page, size);
    }

    @GetMapping("/user")
    @Operation(
            summary = "유저별 감정 기록 전체 조회 API",
            description = "유저들이 작성한 감정 기록 전체를 조회합니다.(닉네임은 조회되지 않습니다.)"
    )
    public ResponseResult getUserEmotionRecords(
            @RequestParam("tag") String loginId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return emotionRecordService.getEmotionRecordsByLoginId(loginId, page, size);
    }

    @GetMapping("/{recordId}")
    @Operation(
            summary = "상세 감정 기록 조회 API",
            description = "상세 감정 기록을 조회합니다. (자신이 쓴 감정 기록이면 disable 값이 false, 아니면 true)"
    )
    public ResponseResult getEmotionRecord(
            @PathVariable Long recordId,
            @AuthenticationPrincipal Long userId) {
        return emotionRecordService.getEmotionRecord(userId, recordId);
    }

    @GetMapping("/spotify-video")
    @Operation(
            summary = "SpotifyID에 해당하는 VideoID 조회 API",
            description = "SpotifyID에 해당하는 VideoID를 조회합니다."
    )
    public ResponseResult getVideoIdBySpotifyId(
            @RequestParam String spotifyId) {
        return emotionRecordService.getVideoIdBySpotifyId(spotifyId);
    }

    @PutMapping("/{recordId}")
    @Operation(
            summary = "감정 기록 수정 API",
            description = "특정 감정 기록을 수정하고 수정한 결과를 반환합니다."
    )
    public ResponseResult updateEmotionRecord(
            @PathVariable Long recordId,
            @RequestBody EmotionRecordUpdateRequestDTO updateDTO) {
        return emotionRecordService.updateEmotionRecord(recordId, updateDTO);
    }

    @DeleteMapping("/{recordId}")
    @Operation(
            summary = "감정 기록 삭제 API",
            description = "특정 감정 기록을 삭제합니다."
    )
    public ResponseResult deleteEmotionRecord(@PathVariable Long recordId) {
        return emotionRecordService.deleteEmotionRecord(recordId);
    }
}
