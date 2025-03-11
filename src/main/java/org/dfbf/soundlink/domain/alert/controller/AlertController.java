package org.dfbf.soundlink.domain.alert.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.alert.service.AlertService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/alert")
@RequiredArgsConstructor
@Tag(name = "Alert API", description = "알림 관련 API (SSE)")
public class AlertController {

    private final AlertService alertService;

    @GetMapping(value = "/connect", produces = "text/event-stream")
    @Operation(summary = "SSE 연결 API", description = "SSE 연결")
    public SseEmitter subscribe(
            @AuthenticationPrincipal Long id,
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        System.out.println("id = " + id);
        return alertService.connectAlarm(id, lastEventId);
    }

    @GetMapping(value = "/connect/test", produces = "text/event-stream")
    @Operation(summary = "SSE 연결 API", description = "SSE 연결")
    public SseEmitter subscribeTest(
            @RequestParam("id") Long id,
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return alertService.connectAlarm(id, lastEventId);
    }
}
