package com.divitbui.provider;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.divitbui.model.OrderBook;

import lombok.Getter;

@Component
public class OrderBookProvider {
    
    @Getter
    private ConcurrentHashMap<String, OrderBook> orderBooks = new ConcurrentHashMap<>();

    public OrderBook getOrderBookBySymbol(final String symbol) {
        return orderBooks.computeIfAbsent(symbol, OrderBook::new);
    }
}
