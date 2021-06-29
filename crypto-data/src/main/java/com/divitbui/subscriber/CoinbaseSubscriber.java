package com.divitbui.subscriber;

import java.net.URI;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

import com.divitbui.config.CoinbaseConfig;
import com.divitbui.model.request.Channel;
import com.divitbui.model.request.ChannelType;
import com.divitbui.model.request.SubscriptionRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class CoinbaseSubscriber {

    private final ReactorNettyWebSocketClient client;
    private final CoinbaseConfig coinbaseConfig;
    private final ObjectMapper objectMapper;
    private final OrderSubscriber orderSubscriber;
    private final Many<JsonNode> output = Sinks.many().multicast().onBackpressureBuffer();

    public Disposable subscribe(final String... instruments) {
        outputSubscribe();
        return client.execute(URI.create(coinbaseConfig.getWsUrl()),
                              session -> session.send(Mono.just(session.textMessage(toJsonStr(createMessage(List.of(instruments))))))
                                                .thenMany(session.receive()
                                                                 .map(WebSocketMessage::getPayloadAsText)
                                                                 // .log()
                                                                 .publishOn(Schedulers.boundedElastic())
                                                                 .map(this::convertJsonNode)
                                                                 .doOnNext(output::tryEmitNext))
                                                .then())
                     .subscribe();
    }

    private void outputSubscribe() {
        output.asFlux()
              .subscribe(orderSubscriber);
    }

    private String toJsonStr(final Object obj) {
        try {
            return objectMapper.writer().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode convertJsonNode(final String message) {
        try {
            return objectMapper.reader().readTree(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private SubscriptionRequest createMessage(final List<String> instruments) {
        return SubscriptionRequest.builder()
                                  .type("subscribe")
                                  .productIds(instruments)
                                  .channels(List.of(Channel.builder()
                                                           .name(ChannelType.LEVEL2)
                                                           .build()))
                                  .build();
    }
}
