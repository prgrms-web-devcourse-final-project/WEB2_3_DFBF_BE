package org.example.soundlinkchat_java.domain.chat.dto;

import org.example.soundlinkchat_java.domain.chat.enums.ExtendMessageType;

public record ExtendMessageEnvelope(
        ExtendMessageType type,
        Object payload
) {
}
