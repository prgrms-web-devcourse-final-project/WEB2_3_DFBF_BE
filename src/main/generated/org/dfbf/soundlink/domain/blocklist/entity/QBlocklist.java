package org.dfbf.soundlink.domain.blocklist.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBlocklist is a Querydsl query type for Blocklist
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBlocklist extends EntityPathBase<Blocklist> {

    private static final long serialVersionUID = -864493686L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBlocklist blocklist = new QBlocklist("blocklist");

    public final org.dfbf.soundlink.domain.user.entity.QUser blockedUser;

    public final NumberPath<Long> blocklistId = createNumber("blocklistId", Long.class);

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> updatedAt = createDateTime("updatedAt", java.sql.Timestamp.class);

    public final org.dfbf.soundlink.domain.user.entity.QUser user;

    public QBlocklist(String variable) {
        this(Blocklist.class, forVariable(variable), INITS);
    }

    public QBlocklist(Path<? extends Blocklist> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBlocklist(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBlocklist(PathMetadata metadata, PathInits inits) {
        this(Blocklist.class, metadata, inits);
    }

    public QBlocklist(Class<? extends Blocklist> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.blockedUser = inits.isInitialized("blockedUser") ? new org.dfbf.soundlink.domain.user.entity.QUser(forProperty("blockedUser")) : null;
        this.user = inits.isInitialized("user") ? new org.dfbf.soundlink.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

