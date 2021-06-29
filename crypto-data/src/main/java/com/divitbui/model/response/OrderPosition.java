package com.divitbui.model.response;

import java.math.BigDecimal;

import com.divitbui.model.Side;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderPosition {

    private Side side;
    private BigDecimal price;
    private BigDecimal size;

}
