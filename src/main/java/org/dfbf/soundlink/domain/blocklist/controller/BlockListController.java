package org.dfbf.soundlink.domain.blocklist.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.blocklist.dto.BlockListReq;
import org.dfbf.soundlink.domain.blocklist.dto.BlockReq;
import org.dfbf.soundlink.domain.blocklist.service.BlockListService;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Blocklist API", description = "차단 유저 인증 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/blocklist")
public class BlockListController {
    private final BlockListService blockListService;

    @PostMapping
    @Operation(
            summary = "유저 차단",
            description = "특정 유저를 차단합니다."
    )
    public ResponseResult blockUser(
            @AuthenticationPrincipal Long userId,
            @RequestBody BlockReq req
    ) {
        return blockListService.blockUser(userId, req.tag());
    }

    @DeleteMapping
    @Operation(
            summary = "유저 차단 해제",
            description = "차단한 유저를 해제합니다."
    )
    public ResponseResult unblockUser(
            @AuthenticationPrincipal Long userId,
            @RequestBody BlockListReq req
    ) {
        return blockListService.unblockUser(userId, req.blocklistId());
    }

    @GetMapping("/mypage/blackListSearch")
    @Operation(
            summary = "차단 목록 조회",
            description = "해당 유저의 차단 목록을 가져옵니다."
    )
    public ResponseResult getBlockList(@AuthenticationPrincipal Long userId) {
        return blockListService.getBlockListByUserId(userId);
    }
}
