package org.dfbf.soundlink.domain.chat.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ChatRequest implements Serializable {
    Long requestId;
    Long responseId;
    Long emotionRecordId;
}

