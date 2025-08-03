package org.justjava.gymcore.config.smtp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "smtp.google")
public class GoogleSmtpConfig extends AbstractSmtpConfig {
}
