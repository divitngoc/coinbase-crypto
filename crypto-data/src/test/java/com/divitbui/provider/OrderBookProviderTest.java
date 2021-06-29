package com.divitbui.provider;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class OrderBookProviderTest {

    private OrderBookProvider provider = new OrderBookProvider();

    @Test
    void testGetOrderBookBySymbol() {
        assertNotNull(provider.getOrderBookBySymbol("BTC-USD"));
    }
}