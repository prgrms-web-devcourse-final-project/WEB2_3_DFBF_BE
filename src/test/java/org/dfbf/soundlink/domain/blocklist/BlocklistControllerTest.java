package org.dfbf.soundlink.domain.blocklist;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dfbf.soundlink.domain.blocklist.controller.BlockListController;
import org.dfbf.soundlink.domain.blocklist.dto.BlockReq;
import org.dfbf.soundlink.domain.blocklist.service.BlockListService;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BlockListController.class)
@AutoConfigureMockMvc(addFilters = false)
class BlockListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BlockListService blockListService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void userBlockedRequestComplete() throws Exception {
        BlockReq blockReq = new BlockReq("targetUser");

        Mockito.when(blockListService.blockUser(any(Long.class), any(BlockReq.class)))
                .thenReturn(new ResponseResult(ErrorCode.SUCCESS));

        mockMvc.perform(post("/api/blocklist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blockReq)))
                .andExpect(status().isOk());
    }
}
