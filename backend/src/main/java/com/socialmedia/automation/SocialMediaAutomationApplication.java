package com.socialmedia.automation;

import com.socialmedia.automation.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(AppProperties.class)
public class SocialMediaAutomationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialMediaAutomationApplication.class, args);
    }
}

