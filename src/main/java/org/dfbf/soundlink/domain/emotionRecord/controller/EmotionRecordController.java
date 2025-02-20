package org.dfbf.soundlink.domain.emotionRecord.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.emotionRecord.dto.request.EmotionRecordRequestDTO;
import org.dfbf.soundlink.domain.emotionRecord.service.EmotionRecordService;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ResponseResult> saveEmotionWithMusic(@Valid @RequestBody EmotionRecordRequestDTO request) {
        ResponseResult response = emotionRecordService.saveEmotionRecordWithMusic(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping
    @Operation(
            summary = "감정 기록 전체 조회 API",
            description = "유저들이 작성한 감정 기록 전체를 조회합니다.(자신의 아이디에 해당하는 감정 기록은 조회되지 않습니다.)"
    )
    public ResponseEntity<ResponseResult> getEmotionRecords(/*@AuthenticationPrincipal*/ Long userId) {
        ResponseResult response = emotionRecordService.getEmotionRecordsByUserId(userId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{recordId}")
    @Operation(
            summary = "감정 기록 조회 API",
            description = "유저가 작성한 감정 기록을 조회합니다."
    )
    public ResponseEntity<ResponseResult> getEmotionRecord(@PathVariable Long recordId) {
        ResponseResult response = emotionRecordService.getEmotionRecord(recordId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{recordId}")
    @Operation(
            summary = "감정 기록 삭제 API",
            description = "특정 감정 기록을 삭제합니다."
    )
    public ResponseEntity<ResponseResult> deleteEmotionRecord(@PathVariable Long recordId) {
        ResponseResult response = emotionRecordService.deleteEmotionRecord(recordId);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
