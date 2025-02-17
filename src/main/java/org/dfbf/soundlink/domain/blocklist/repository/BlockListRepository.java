package org.dfbf.soundlink.domain.blocklist.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.dfbf.soundlink.domain.blocklist.entity.Blocklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockListRepository extends JpaRepository<Blocklist, Long> {
    @Query(
            "SELECT b " +
                    "FROM Blocklist b " +
                    "WHERE b.user.userId = :userId"
    )
    List<Blocklist> findAllByUserId(@Param("userId") Long userId);

    @Query(
            "SELECT b " +
                    "FROM Blocklist b " +
                    "WHERE b.user.userId = :userId " +
                    "AND b.blockedUser.userId = :blockedUserId"
    )
    List<Blocklist> findAllByUserId(
            @Param("userId") Long userId,
            @Param("blockedUserId") Long blockedUserId
            );

// [Soundlink] 음악을 통해 감정을 기록하고, 같은 감정을 공유하는 사람들과 연결
}
