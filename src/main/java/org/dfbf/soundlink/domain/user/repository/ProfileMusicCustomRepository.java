package org.dfbf.soundlink.domain.user.repository;

import org.dfbf.soundlink.domain.user.entity.ProfileMusic;
import org.dfbf.soundlink.domain.user.entity.User;

import java.util.Optional;

public interface ProfileMusicCustomRepository {
    Optional<ProfileMusic> findByUserId(Long userId);
    void deleteByUser(User user);
}
