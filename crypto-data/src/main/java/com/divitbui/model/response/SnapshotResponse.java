package com.divitbui.model.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.divitbui.model.Side;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SnapshotResponse extends Level2ChannelResponse {

    private String[][] bids;
    private String[][] asks;

    public List<OrderPosition> getBids() {
        return convertToOrderPosition(bids, Side.BUY);
    }

    public List<OrderPosition> getAsks() {
        return convertToOrderPosition(asks, Side.SELL);
    }

    private List<OrderPosition> convertToOrderPosition(final String[][] o, final Side side) {
        return Stream.of(o)
                     .map(r -> OrderPosition.builder()
                                            .side(side)
                                            .price(new BigDecimal((r[0])))
                                            .size(new BigDecimal(r[1]))
                                            .build())
                     .collect(Collectors.toList());
    }
}