package org.dfbf.soundlink.domain.emotionRecord.repository;

import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.user.dto.response.EmotionRecordDto;
import org.dfbf.soundlink.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmotionRecordRepository extends JpaRepository<EmotionRecord, Long> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM EmotionRecord e WHERE e.user = :user")
    public void deleteByUser(@Param("user") User user);

    @Query( "SELECT new org.dfbf.soundlink.domain.user.dto.response.EmotionRecordDto" +
            "(er.spotifyMusic.spotifyId, er.spotifyMusic.title, er.spotifyMusic.artist, er.spotifyMusic.albumImage, er.emotion, er.comment, er.createdAt) " +
            "FROM EmotionRecord er " +
            "WHERE er.user = :user" )
    List<EmotionRecordDto> findByUser(@Param("user") User user);

    @Query( "SELECT er FROM EmotionRecord er " +
            "JOIN FETCH er.user u " +
            "LEFT JOIN FETCH er.spotifyMusic sm " +
            "WHERE u.userId = :userId ")
    Page<EmotionRecord> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query( "SELECT er FROM EmotionRecord er " +
            "JOIN FETCH er.user u " +
            "LEFT JOIN FETCH er.spotifyMusic sm " +
            "WHERE u.userId <> :userId ")
    Page<EmotionRecord> findByWithoutUserId(@Param("userId") Long userId, Pageable pageable);

    @Query( "SELECT er FROM EmotionRecord er " +
            "JOIN FETCH er.user u " +
            "LEFT JOIN FETCH er.spotifyMusic sm " +
            "WHERE er.recordId = :recordId")
    Optional<EmotionRecord> findByRecordId(@Param("recordId") Long recordId);

    @Modifying
    @Query( "DELETE FROM EmotionRecord er " +
            "WHERE er.recordId = :recordId")
    int deleteByRecordId(@Param("recordId") Long recordId);
}

/**
 * @Modifying(clearAutomatically = true)
 * 1차 캐시안의 내용까지 지워버리는 옵션
 */

