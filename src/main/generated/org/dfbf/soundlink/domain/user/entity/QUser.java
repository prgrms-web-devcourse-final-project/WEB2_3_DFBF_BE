package org.dfbf.soundlink.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -2109140326L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final ListPath<org.dfbf.soundlink.domain.blocklist.entity.Blocklist, org.dfbf.soundlink.domain.blocklist.entity.QBlocklist> blocklist = this.<org.dfbf.soundlink.domain.blocklist.entity.Blocklist, org.dfbf.soundlink.domain.blocklist.entity.QBlocklist>createList("blocklist", org.dfbf.soundlink.domain.blocklist.entity.Blocklist.class, org.dfbf.soundlink.domain.blocklist.entity.QBlocklist.class, PathInits.DIRECT2);

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final StringPath email = createString("email");

    public final ListPath<org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord, org.dfbf.soundlink.domain.emotionRecord.entity.QEmotionRecord> emotionRecord = this.<org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord, org.dfbf.soundlink.domain.emotionRecord.entity.QEmotionRecord>createList("emotionRecord", org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord.class, org.dfbf.soundlink.domain.emotionRecord.entity.QEmotionRecord.class, PathInits.DIRECT2);

    public final StringPath loginId = createString("loginId");

    public final StringPath nickName = createString("nickName");

    public final StringPath password = createString("password");

    public final QProfileMusic profileMusic;

    public final NumberPath<Long> socialId = createNumber("socialId", Long.class);

    public final EnumPath<org.dfbf.soundlink.global.comm.enums.SocialType> socialType = createEnum("socialType", org.dfbf.soundlink.global.comm.enums.SocialType.class);

    public final DateTimePath<java.sql.Timestamp> updateAt = createDateTime("updateAt", java.sql.Timestamp.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profileMusic = inits.isInitialized("profileMusic") ? new QProfileMusic(forProperty("profileMusic"), inits.get("profileMusic")) : null;
    }

}

