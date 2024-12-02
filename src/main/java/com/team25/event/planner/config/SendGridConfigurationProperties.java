package com.team25.event.planner.config;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "email.config")
class SendGridConfigurationProperties {
    @NotBlank
    @Pattern(regexp = "^SG[0-9a-zA-Z._-]{67}$")
    private String apiKey;

    @Email
    @NotBlank
    private String fromEmail;

    @NotBlank
    private String fromName;
}