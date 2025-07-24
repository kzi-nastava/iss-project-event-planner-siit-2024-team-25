package com.team25.event.planner.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JavaMailSenderConfigProperties.class)
public class JavaMailSenderConfig {
}
