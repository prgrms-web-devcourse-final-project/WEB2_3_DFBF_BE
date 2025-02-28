package org.dfbf.soundlink.domain.chat.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dfbf.soundlink.domain.emotionRecord.entity.EmotionRecord;
import org.dfbf.soundlink.domain.user.entity.User;
import org.dfbf.soundlink.global.comm.enums.RoomStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long chatRoomId;

    @Column(name = "start_time")
    private Timestamp startTime;

    @Column(name = "end_time")
    private Timestamp endTime;

    @ManyToOne
    @JoinColumn (name = "request_user_id")
    private User requestUserId;

    @OneToOne
    @JoinColumn(name = "record_id")
    private EmotionRecord recordId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RoomStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Builder
    public ChatRoom(User requestUserId, EmotionRecord recordId, RoomStatus status,
                    Timestamp startTime, Timestamp endTime) {
        this.requestUserId = requestUserId;
        this.recordId = recordId;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    //채팅방 상태 업데이트
    public void updateChatRoomStatus(RoomStatus status){
        this.status = status;
    }

}
