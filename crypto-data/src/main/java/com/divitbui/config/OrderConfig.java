package com.divitbui.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "order")
@Configuration
public class OrderConfig {

    private int maxPrintLevel;

}
