package org.dfbf.soundlink.domain.emotionRecord.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.emotionRecord.dto.request.EmotionRecordRequestDTO;
import org.dfbf.soundlink.domain.emotionRecord.service.EmotionRecordService;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/emotion")
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
}
