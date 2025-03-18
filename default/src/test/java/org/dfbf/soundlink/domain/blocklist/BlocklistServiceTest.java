package org.dfbf.soundlink.domain.blocklist;

import org.dfbf.soundlink.CustomLogger;
import org.dfbf.soundlink.domain.blocklist.dto.BlockReq;
import org.dfbf.soundlink.domain.blocklist.entity.Blocklist;
import org.dfbf.soundlink.domain.blocklist.repository.BlockListQueryRepository;
import org.dfbf.soundlink.domain.blocklist.repository.BlockListRepository;
import org.dfbf.soundlink.domain.blocklist.service.BlockListService;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BlocklistServiceTest {

    @InjectMocks
    private BlockListService blockListService;

    @Mock
    private BlockListQueryRepository blockListQueryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BlockListRepository blockListRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("1. blockUserSuccess - 유저 차단 성공")
    @ExtendWith(CustomLogger.class)
    void blockUserSuccess() {
        // given
        Long userId = 1L;
        String targetLoginId = "targetUser";
        BlockReq blockReq = new BlockReq(targetLoginId);

        User user = mock(User.class);
        User blockedUser = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByLoginId(targetLoginId)).thenReturn(Optional.of(blockedUser));
        when(blockListQueryRepository.findByUser_UserIdAndBlockedUser_LoginId(userId, targetLoginId))
                .thenReturn(Optional.empty());

        // when
        ResponseResult result = blockListService.blockUser(userId, blockReq);

        // then
        assertThat(result.getMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
        verify(blockListRepository, times(1)).save(any(Blocklist.class));
    }

    @Test
    @DisplayName("2. blockUserFailWhenAlreadyBlocked - 이미 차단된 유저")
    @ExtendWith(CustomLogger.class)
    void blockUserFailWhenAlreadyBlocked() {
        // given
        Long userId = 1L;
        String targetLoginId = "targetUser";
        BlockReq blockReq = new BlockReq(targetLoginId);

        User user = mock(User.class);
        User blockedUser = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByLoginId(targetLoginId)).thenReturn(Optional.of(blockedUser));
        when(blockListQueryRepository.findByUser_UserIdAndBlockedUser_LoginId(userId, targetLoginId))
                .thenReturn(Optional.of(mock(Blocklist.class)));

        // when
        ResponseResult result = blockListService.blockUser(userId, blockReq);

        // then
        assertThat(result.getMessage()).isEqualTo(ErrorCode.ALREADY_BLOCKED_USER.getMessage());
        verify(blockListRepository, never()).save(any());
    }

    @Test
    @DisplayName("3. unblockUserSuccess - 유저 차단 해제")
    @ExtendWith(CustomLogger.class)
    void unblockUserSuccess() {
        // given
        Long userId = 1L;
        Long blocklistId = 2L;
        Blocklist blocklist = mock(Blocklist.class);

        when(blockListQueryRepository.findByUser_UserIdAndBlockedUser_UserId(userId, blocklistId))
                .thenReturn(Optional.of(blocklist));

        // when
        ResponseResult result = blockListService.unblockUser(userId, blocklistId);

        // then
        assertThat(result.getMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
        verify(blockListRepository, times(1)).delete(blocklist);
    }

    @Test
    @DisplayName("4. searchBlocklistSuccess - 차단된 유저 전체 조회")
    @ExtendWith(CustomLogger.class)
    void searchBlocklistSuccess() {
        // given
        Long userId = 1L;

        User user = mock(User.class);
        User blockedUser = mock(User.class);

        when(user.getUserId()).thenReturn(userId);
        when(blockedUser.getUserId()).thenReturn(2L);
        when(blockedUser.getNickname()).thenReturn("닉네임");
        when(blockedUser.getLoginId()).thenReturn("loginId");

        Blocklist blocklist = mock(Blocklist.class);
        when(blocklist.getUser()).thenReturn(user);
        when(blocklist.getBlockedUser()).thenReturn(blockedUser);

        List<Blocklist> mockBlocklist = List.of(blocklist);

        when(blockListQueryRepository.findAllByUser_UserId(userId))
                .thenReturn(mockBlocklist);

        // when
        ResponseResult result = blockListService.getBlockListByUserId(userId);

        // then
        assertThat(result.getMessage()).isEqualTo(ErrorCode.SUCCESS.getMessage());
        assertThat(result.getData()).isInstanceOf(List.class);
    }
}