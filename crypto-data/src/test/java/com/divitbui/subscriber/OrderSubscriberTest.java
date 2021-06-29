package com.divitbui.subscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.divitbui.config.OrderConfig;
import com.divitbui.model.OrderBook;
import com.divitbui.provider.OrderBookProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@ExtendWith(MockitoExtension.class)
class OrderSubscriberTest {

    @Mock
    private OrderBookProvider orderBookProvider;
    private ObjectMapper mapper;
    private OrderSubscriber subscriber;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        OrderConfig orderConfig = new OrderConfig();
        orderConfig.setMaxPrintLevel(10);
        subscriber = new OrderSubscriber(mapper, orderBookProvider, orderConfig);
    }

    @Test
    void testHandleSnapshot() throws JsonMappingException, JsonProcessingException {
        // GIVEN
        OrderBook orderBook = new OrderBook("BTC-USD");
        when(orderBookProvider.getOrderBookBySymbol("BTC-USD")).thenReturn(orderBook);

        // WHEN
        subscriber.onNext(createSnapshotResponse());

        // THEN
        assertThat(orderBook.getBuyOrders()).hasSize(1)
                                            .containsOnlyKeys(new BigDecimal("10101.10"))
                                            .containsValue(new BigDecimal("0.45054140"));
        assertThat(orderBook.getSellOrders()).hasSize(1)
                                             .containsOnlyKeys(new BigDecimal("10102.55"))
                                             .containsValue(new BigDecimal("0.57753524"));
    }

    @Test
    void testHandleL2Update_update() throws JsonMappingException, JsonProcessingException {
        // GIVEN
        OrderBook orderBook = new OrderBook("BTC-USD");
        when(orderBookProvider.getOrderBookBySymbol("BTC-USD")).thenReturn(orderBook);
        BigDecimal size = BigDecimal.ONE;

        // WHEN
        subscriber.onNext(createL2UpdateResponse(size));

        // THEN
        assertThat(orderBook.getBuyOrders()).hasSize(1)
                                            .containsOnlyKeys(new BigDecimal("10101.10"))
                                            .containsValue(size);
        assertThat(orderBook.getSellOrders()).isEmpty();
    }

    @Test
    void testHandleL2Update_remove() throws JsonMappingException, JsonProcessingException {
        // GIVEN
        OrderBook orderBook = new OrderBook("BTC-USD");
        orderBook.getBuyOrders().put(new BigDecimal("10101.10"), BigDecimal.ONE);
        when(orderBookProvider.getOrderBookBySymbol("BTC-USD")).thenReturn(orderBook);
        BigDecimal size = BigDecimal.ZERO;

        // WHEN
        subscriber.onNext(createL2UpdateResponse(size));

        // THEN
        assertThat(orderBook.getBuyOrders()).isEmpty();
        assertThat(orderBook.getSellOrders()).isEmpty();
    }

    private JsonNode createL2UpdateResponse(BigDecimal size) throws JsonMappingException, JsonProcessingException {
        String str = "{\r\n"
            + "  \"type\": \"l2update\",\r\n"
            + "  \"product_id\": \"BTC-USD\",\r\n"
            + "  \"time\": \"2019-08-14T20:42:27.265Z\",\r\n"
            + "  \"changes\": [\r\n"
            + "    [\r\n"
            + "      \"buy\",\r\n"
            + "      \"10101.10\",\r\n"
            + "      \"" + size.toPlainString() + "\"\r\n"
            + "    ]\r\n"
            + "  ]\r\n"
            + "}";
        return mapper.readTree(str);
    }

    private JsonNode createSnapshotResponse() throws JsonMappingException, JsonProcessingException {
        String str = "{\r\n"
            + "    \"type\": \"snapshot\",\r\n"
            + "    \"product_id\": \"BTC-USD\",\r\n"
            + "    \"bids\": [[\"10101.10\", \"0.45054140\"]],\r\n"
            + "    \"asks\": [[\"10102.55\", \"0.57753524\"]]\r\n"
            + "}";
        return mapper.readTree(str);
    }
}
