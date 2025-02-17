package org.dfbf.soundlink.domain.blocklist.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.blocklist.dto.BlockReq;
import org.dfbf.soundlink.domain.blocklist.dto.BlockRes;
import org.dfbf.soundlink.domain.blocklist.service.BlockListService;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Blocklist API", description = "차단 유저 인증 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/blocklist")
public class BlockListController {
    private final BlockListService blockListService;

    @PostMapping
    public ResponseResult blockUser(@RequestBody BlockReq blockReq) {
        return blockListService.blockUser(blockReq);
    }

    @DeleteMapping
    public ResponseResult unblockUser(@RequestBody BlockReq blockReq) {
        return blockListService.unblockUser(blockReq);
    }

    @GetMapping("/{userId}")
    public ResponseResult getBlockList(@PathVariable Long userId) {
        return blockListService.getBlockListByUserId(userId);
    }
}
