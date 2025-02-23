package org.dfbf.soundlink.domain.blocklist.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.blocklist.dto.BlockRes;
import org.dfbf.soundlink.domain.blocklist.entity.Blocklist;
import org.dfbf.soundlink.domain.blocklist.exception.AlreadyBlockedUser;
import org.dfbf.soundlink.domain.blocklist.exception.BlockedUserNotFound;
import org.dfbf.soundlink.domain.blocklist.exception.BlockingUserNotFound;
import org.dfbf.soundlink.domain.blocklist.repository.BlockListQueryRepository;
import org.dfbf.soundlink.domain.blocklist.repository.BlockListRepository;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockListService {
    private final BlockListQueryRepository blockListQueryRepository;
    private final UserRepository userRepository;
    private final BlockListRepository blockListRepository;

    @Transactional
    public ResponseResult blockUser(Long userId, String tag) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(
                            BlockedUserNotFound::new
                    );
            User blockedUser = userRepository.findByLoginId(tag)
                    .orElseThrow(
                            BlockingUserNotFound::new
                    );
            blockListQueryRepository.findByUser_UserIdAndBlockedUser_LoginId(
                    userId, tag
            ).ifPresent(block -> {
                throw new AlreadyBlockedUser();
            });

            Blocklist blockTarget = Blocklist.builder()
                    .user(user)
                    .blockedUser(blockedUser)
                    .build();

            blockListRepository.save(blockTarget);

            return new ResponseResult(
                    ErrorCode.SUCCESS
            );
        } catch (BlockedUserNotFound e) {
            return new ResponseResult(
                    ErrorCode.BLOCKED_USER_NOT_FOUND,
                    e.getMessage()
            );
        } catch (BlockingUserNotFound e) {
            return new ResponseResult(
                    ErrorCode.BLOCKING_USER_NOT_FOUND,
                    e.getMessage()
            );
        } catch (AlreadyBlockedUser e) {
            return new ResponseResult(
                    ErrorCode.ALREADY_BLOCKED_USER,
                    e.getMessage()
            );
        }
    }

    @Transactional
    public ResponseResult unblockUser(Long userId, Long blocklistId) {
        try {
            Blocklist block = blockListQueryRepository.findByUser_UserIdAndBlockedUser_UserId(
                    userId, blocklistId
            ).orElseThrow(BlockingUserNotFound::new);

            blockListRepository.delete(block);

            return new ResponseResult(
                    ErrorCode.SUCCESS
            );
        } catch (BlockingUserNotFound e) {
            return new ResponseResult(
                    ErrorCode.BLOCKING_USER_NOT_FOUND,
                    e.getMessage()
            );
        }
    }

    public ResponseResult getBlockListByUserId(Long userId) {
        List<Blocklist> blocklist = blockListQueryRepository.findAllByUser_UserId(userId);
        return new ResponseResult(
                ErrorCode.SUCCESS,
                blocklist.stream()
                        .map(block -> new BlockRes(
                                block.getUser().getUserId(),
                                block.getBlockedUser().getUserId(),
                                block.getCreatedAt(),
                                block.getUpdatedAt()
                        )).collect(Collectors.toList())
        );
    }
}
