package org.dfbf.soundlink.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfileMusic is a Querydsl query type for ProfileMusic
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfileMusic extends EntityPathBase<ProfileMusic> {

    private static final long serialVersionUID = -1546125877L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfileMusic profileMusic = new QProfileMusic("profileMusic");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final NumberPath<Long> profileMusicId = createNumber("profileMusicId", Long.class);

    public final org.dfbf.soundlink.domain.emotionRecord.entity.QSpotifyMusic spotifyMusic;

    public final DateTimePath<java.sql.Timestamp> updatedAt = createDateTime("updatedAt", java.sql.Timestamp.class);

    public QProfileMusic(String variable) {
        this(ProfileMusic.class, forVariable(variable), INITS);
    }

    public QProfileMusic(Path<? extends ProfileMusic> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfileMusic(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfileMusic(PathMetadata metadata, PathInits inits) {
        this(ProfileMusic.class, metadata, inits);
    }

    public QProfileMusic(Class<? extends ProfileMusic> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.spotifyMusic = inits.isInitialized("spotifyMusic") ? new org.dfbf.soundlink.domain.emotionRecord.entity.QSpotifyMusic(forProperty("spotifyMusic")) : null;
    }

}

