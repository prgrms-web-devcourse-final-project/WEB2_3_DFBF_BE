package org.dfbf.soundlink.domain.alert.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.alert.service.AlertService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/alert")
@RequiredArgsConstructor
@Tag(name = "Alert API", description = "알림 관련 API (SSE)")
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/connect")
    @Operation(summary = "SSE 연결 API", description = "SSE 연결")
    public SseEmitter subscribe(/*@AuthenticationPrincipal Long id*/ @RequestParam("id") Long id) {
        return alertService.connectAlarm(id);
    }

//    @PostMapping("")
//    @Operation(summary = "알림 전송 API", description = "알림을 전송하는 기능")
//    public void send(@RequestParam("id") Long id, @RequestParam("msg") String msg) {
//        alertService.send(id,"test" ,msg);
//    }

    @DeleteMapping
    @Operation(summary = "SSE 연결 해제 API", description = "SSE 연결 해제")
    public void disconnect(@RequestParam("id") Long id) {
        alertService.disconnectAlarm(id);
    }
}
