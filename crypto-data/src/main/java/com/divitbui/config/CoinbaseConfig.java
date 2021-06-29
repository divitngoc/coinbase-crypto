package com.divitbui.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "coinbase")
@Configuration
public class CoinbaseConfig {

    private String wsUrl;
    
}
