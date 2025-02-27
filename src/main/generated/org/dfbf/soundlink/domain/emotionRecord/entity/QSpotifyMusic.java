package org.dfbf.soundlink.domain.emotionRecord.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSpotifyMusic is a Querydsl query type for SpotifyMusic
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSpotifyMusic extends EntityPathBase<SpotifyMusic> {

    private static final long serialVersionUID = 401786157L;

    public static final QSpotifyMusic spotifyMusic = new QSpotifyMusic("spotifyMusic");

    public final StringPath albumImage = createString("albumImage");

    public final StringPath artist = createString("artist");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath spotifyId = createString("spotifyId");

    public final StringPath title = createString("title");

    public final DateTimePath<java.sql.Timestamp> updatedAt = createDateTime("updatedAt", java.sql.Timestamp.class);

    public QSpotifyMusic(String variable) {
        super(SpotifyMusic.class, forVariable(variable));
    }

    public QSpotifyMusic(Path<? extends SpotifyMusic> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSpotifyMusic(PathMetadata metadata) {
        super(SpotifyMusic.class, metadata);
    }

}

