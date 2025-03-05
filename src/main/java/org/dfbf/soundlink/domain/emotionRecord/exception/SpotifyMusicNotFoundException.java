package org.dfbf.soundlink.domain.emotionRecord.exception;

import org.dfbf.soundlink.global.exception.BusinessException;
import org.dfbf.soundlink.global.exception.ErrorCode;

public class SpotifyMusicNotFoundException extends BusinessException {
    public SpotifyMusicNotFoundException() {
        super(ErrorCode.FAIL_TO_FIND_SPOTIFY_MUSIC);
    }
}
