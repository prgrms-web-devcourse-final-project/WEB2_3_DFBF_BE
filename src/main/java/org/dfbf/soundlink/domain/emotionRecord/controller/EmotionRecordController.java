package org.dfbf.soundlink.domain.emotionRecord.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.emotionRecord.dto.request.EmotionRecordRequestDTO;
import org.dfbf.soundlink.domain.emotionRecord.dto.request.EmotionRecordUpdateRequestDTO;
import org.dfbf.soundlink.domain.emotionRecord.service.EmotionRecordService;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emotion")
@RequiredArgsConstructor
@Tag(name = "EmotionRecord API", description = "감정기록 관련 API")
public class EmotionRecordController {

    private final EmotionRecordService emotionRecordService;

    @PostMapping
    @Operation(
            summary = "감정 기록 작성/저장 API",
            description = "작성한 감정 기록을 저장합니다."
    )
    public ResponseResult saveEmotionWithMusic(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody EmotionRecordRequestDTO request) {
        return emotionRecordService.saveEmotionRecordWithMusic(userId, request);
    }

    @GetMapping
    @Operation(
            summary = "감정 기록 전체 조회 메인 API",
            description = "유저들이 작성한 감정 기록 전체를 조회합니다.(자신의 아이디에 해당하는 감정 기록은 조회되지 않습니다.)"
    )
    public ResponseResult getEmotionRecordsWithoutMine(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return emotionRecordService.getEmotionRecordsExcludingUserId(userId, page, size);
    }

    @GetMapping("/user")
    @Operation(
            summary = "유저별 감정 기록 전체 조회 API",
            description = "유저들이 작성한 감정 기록 전체를 조회합니다.(닉네임은 조회되지 않습니다.)"
    )
    public ResponseResult getAllEmotionRecords(
            @RequestParam("tag") String loginId,
            @RequestParam(defaultValue = "0") int page,
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
