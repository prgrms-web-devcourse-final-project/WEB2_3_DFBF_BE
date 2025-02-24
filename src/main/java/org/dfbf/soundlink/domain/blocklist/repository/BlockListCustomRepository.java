package org.dfbf.soundlink.domain.blocklist.repository;

import org.dfbf.soundlink.domain.blocklist.entity.Blocklist;
import java.util.List;
import java.util.Optional;

public interface BlockListCustomRepository {
    List<Blocklist> findAllByUser_UserId(Long userId);
    Optional<Blocklist> findByUser_UserIdAndBlockedUser_LoginId(Long userId, String loginId);
    Optional<Blocklist> findByUser_UserIdAndBlockedUser_UserId(Long userId, Long blockedUserId);
}