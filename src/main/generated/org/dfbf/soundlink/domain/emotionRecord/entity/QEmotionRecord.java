package org.dfbf.soundlink.domain.emotionRecord.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEmotionRecord is a Querydsl query type for EmotionRecord
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmotionRecord extends EntityPathBase<EmotionRecord> {

    private static final long serialVersionUID = 751579946L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEmotionRecord emotionRecord = new QEmotionRecord("emotionRecord");

    public final StringPath comment = createString("comment");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final EnumPath<org.dfbf.soundlink.global.comm.enums.Emotions> emotion = createEnum("emotion", org.dfbf.soundlink.global.comm.enums.Emotions.class);

    public final NumberPath<Long> recordId = createNumber("recordId", Long.class);

    public final QSpotifyMusic spotifyMusic;

    public final DateTimePath<java.sql.Timestamp> updatedAt = createDateTime("updatedAt", java.sql.Timestamp.class);

    public final org.dfbf.soundlink.domain.user.entity.QUser user;

    public QEmotionRecord(String variable) {
        this(EmotionRecord.class, forVariable(variable), INITS);
    }

    public QEmotionRecord(Path<? extends EmotionRecord> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEmotionRecord(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEmotionRecord(PathMetadata metadata, PathInits inits) {
        this(EmotionRecord.class, metadata, inits);
    }

    public QEmotionRecord(Class<? extends EmotionRecord> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.spotifyMusic = inits.isInitialized("spotifyMusic") ? new QSpotifyMusic(forProperty("spotifyMusic")) : null;
        this.user = inits.isInitialized("user") ? new org.dfbf.soundlink.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

