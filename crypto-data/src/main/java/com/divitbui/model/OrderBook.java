package com.divitbui.model;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
public class OrderBook {

    private final String productId;
    private final ConcurrentNavigableMap<BigDecimal, BigDecimal> buyOrders = new ConcurrentSkipListMap<>((p1, p2) -> p2.compareTo(p1));
    private final ConcurrentNavigableMap<BigDecimal, BigDecimal> sellOrders = new ConcurrentSkipListMap<>((p1, p2) -> p1.compareTo(p2));

    public ConcurrentNavigableMap<BigDecimal, BigDecimal> getOrders(final Side side) {
        switch (side) {
            case BUY:
                return buyOrders;
            case SELL:
                return sellOrders;
            default:
                throw new RuntimeException("Unhandled side type when getting orders");
        }
    }

}
