package org.dfbf.soundlink.domain.blocklist.repository;

import org.dfbf.soundlink.domain.blocklist.entity.Blocklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockListRepository extends JpaRepository<Blocklist, Long>, BlockListCustomRepository {
}
