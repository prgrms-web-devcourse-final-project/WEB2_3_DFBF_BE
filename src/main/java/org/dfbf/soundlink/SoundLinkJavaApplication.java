package org.dfbf.soundlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude= SecurityAutoConfiguration.class)
public class SoundLinkJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoundLinkJavaApplication.class, args);
    }

}
