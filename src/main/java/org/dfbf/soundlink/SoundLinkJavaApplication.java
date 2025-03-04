package org.dfbf.soundlink;

import org.dfbf.soundlink.global.config.FeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "org.dfbf.soundlink.global.auth.client", defaultConfiguration = FeignConfig.class)
@SpringBootApplication(exclude= SecurityAutoConfiguration.class)
@EnableCaching
public class SoundLinkJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoundLinkJavaApplication.class, args);
    }

}
