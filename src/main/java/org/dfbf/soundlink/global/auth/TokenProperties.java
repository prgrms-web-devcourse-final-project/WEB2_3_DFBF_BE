package org.dfbf.soundlink.global.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "token")
public class TokenProperties {
    private long refreshTokenExpirationTime;
    private long accessTokenExpirationTime;

    public long getRefreshTokenExpirationTime(){
        return refreshTokenExpirationTime;
    }

    public void setRefreshTokenExpirationTime(long refreshTokenExpirationTime){
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
    }
    public long getAccessTokenExpirationTime() {
        return accessTokenExpirationTime;
    }

    public void setAccessTokenExpirationTime(long accessTokenExpirationTime) {
        this.accessTokenExpirationTime = accessTokenExpirationTime;
    }
}
