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
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class L2UpdateResponse extends Level2ChannelResponse {

    private String[][] changes;

    public List<OrderPosition> getChanges() {
        return Stream.of(changes)
                     .map(r -> OrderPosition.builder()
                                            .side(Side.valueOf(r[0].toUpperCase()))
                                            .price(new BigDecimal((r[1])))
                                            .size(new BigDecimal(r[2]))
                                            .build())
                     .collect(Collectors.toList());
    }

}
