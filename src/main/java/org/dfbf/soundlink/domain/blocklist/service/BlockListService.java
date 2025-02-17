package org.dfbf.soundlink.domain.blocklist.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dfbf.soundlink.domain.blocklist.dto.BlockReq;
import org.dfbf.soundlink.domain.blocklist.dto.BlockRes;
import org.dfbf.soundlink.domain.blocklist.entity.Blocklist;
import org.dfbf.soundlink.domain.blocklist.exception.AlreadyBlockedUser;
import org.dfbf.soundlink.domain.blocklist.exception.BlockedUserNotFound;
import org.dfbf.soundlink.domain.blocklist.exception.BlockingUserNotFound;
import org.dfbf.soundlink.domain.blocklist.repository.BlockListRepository;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.domain.user.repository.UserRepository;
import org.dfbf.soundlink.global.exception.ErrorCode;
import org.dfbf.soundlink.global.exception.ResponseResult;
import org.springframework.cglib.core.Block;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockListService {
    private final BlockListRepository blockListRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseResult blockUser(BlockReq blockReq) {
        try {
            User user = userRepository.findById(blockReq.userId())
                    .orElseThrow(
                            BlockedUserNotFound::new
                    );
            User blockedUser = userRepository.findById(blockReq.blockedUserId())
                    .orElseThrow(
                            BlockingUserNotFound::new
                    );
            blockListRepository.findByUserIdAndBlockedUserId(
                    blockReq.userId(), blockReq.blockedUserId()
            ).ifPresent(block -> {
                throw new AlreadyBlockedUser();
            });

            Blocklist blocklist = Blocklist.builder()
                    .user(user)
                    .blockedUser(blockedUser)
                    .build();

            Blocklist savedBlocklist = blockListRepository.save(blocklist);

            return new ResponseResult(
                    ErrorCode.SUCCESS,
                    new BlockRes(
                            savedBlocklist.getUser().getUserId(),
                            savedBlocklist.getBlockedUser().getUserId(),
                            savedBlocklist.getCreatedAt(),
                            savedBlocklist.getUpdatedAt()
                    )
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
    public ResponseResult unblockUser(BlockReq blockReq) {
        try {
            Blocklist block = blockListRepository.findByUserIdAndBlockedUserId(
                    blockReq.userId(), blockReq.blockedUserId()
            ).orElseThrow(BlockingUserNotFound::new);

            blockListRepository.delete(block);

            return new ResponseResult(
                    ErrorCode.SUCCESS,
                    new BlockRes(
                            block.getUser().getUserId(),
                            block.getBlockedUser().getUserId(),
                            block.getCreatedAt(),
                            block.getUpdatedAt()
                    )
            );
        } catch (BlockingUserNotFound e) {
            return new ResponseResult(
                    ErrorCode.BLOCKING_USER_NOT_FOUND,
                    e.getMessage()
            );
        }
    }

    public ResponseResult getBlockListByUserId(Long userId) {
        List<Blocklist> blocklists = blockListRepository.findAllByUserId(userId);
        return new ResponseResult(
                ErrorCode.SUCCESS,
                blocklists.stream()
                        .map(block -> new BlockRes(
                                block.getUser().getUserId(),
                                block.getBlockedUser().getUserId(),
                                block.getCreatedAt(),
                                block.getUpdatedAt()
                        )).collect(Collectors.toList())
        );
    }
}
