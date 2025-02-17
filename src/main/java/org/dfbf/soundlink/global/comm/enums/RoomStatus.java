package org.dfbf.soundlink.global.comm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 채팅방 상태 (대기, 연결, 실패, 닫힘)
@Getter
@AllArgsConstructor
public enum RoomStatus {
    WAITING("대기"),
    CONNECTED("연결"),
    FAILED("실패"),
    CLOSED("닫힘");

    private final String desc;
}