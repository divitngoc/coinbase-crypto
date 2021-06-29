package com.divitbui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.WebsocketClientSpec;

@Configuration
public class WebSocketConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.create();
    }

    @Bean
    public ReactorNettyWebSocketClient webSocketClient(final HttpClient httpClient) {
        return new ReactorNettyWebSocketClient(httpClient, WebsocketClientSpec.builder().maxFramePayloadLength(Integer.MAX_VALUE));
    }

}
