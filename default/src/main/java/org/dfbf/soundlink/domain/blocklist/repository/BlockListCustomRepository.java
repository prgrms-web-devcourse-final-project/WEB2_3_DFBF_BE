package org.dfbf.soundlink.domain.blocklist.repository;

import org.dfbf.soundlink.domain.blocklist.entity.Blocklist;
import java.util.List;
import java.util.Optional;

public interface BlockListCustomRepository {
    List<Blocklist> findAllByUser_UserId(Long userId);
    Optional<Blocklist> findByUser_UserIdAndBlockedUser_LoginId(Long userId, String loginId);
    Optional<Blocklist> findByUser_UserIdAndBlockedUser_UserId(Long userId, Long blockedUserId);

    // 내가 차단당했는지 확인하는 쿼리 문장
    Boolean existsByUser_UserIdAndBlockedUser_UserId(Long requestId, Long responseId);
    
    // 한 유저와 관련되어 있는 모든 차단 목록을 삭제
    void deleteAllByUser_UserId(Long userId);
}