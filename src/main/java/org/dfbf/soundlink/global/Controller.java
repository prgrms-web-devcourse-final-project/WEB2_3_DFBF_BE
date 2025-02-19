package org.dfbf.soundlink.global;

import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Controller {

    // health check
    @GetMapping("/health")
    public ResponseResult health() {
        return new ResponseResult(ErrorCode.SUCCESS,"ok2");
    }
}
