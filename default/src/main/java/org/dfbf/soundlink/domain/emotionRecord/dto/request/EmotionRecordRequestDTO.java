package org.dfbf.soundlink.domain.emotionRecord.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.dfbf.soundlink.global.comm.enums.Emotions;

public record EmotionRecordRequestDTO(

        @NotNull(message = "spotifyId 필요")
        String spotifyId,

        String videoId,

        @NotBlank(message = "title 필요")
        String title,

        @NotBlank(message = "artist 필요")
        String artist,

        String albumImage,

        @NotNull(message = "emotion 필요")
        Emotions emotion,

        @NotBlank(message = "comment 필요")
        @Size(max = 200, message = "comment는 200자 이내여야 합니다.")
        String comment
) {
}
