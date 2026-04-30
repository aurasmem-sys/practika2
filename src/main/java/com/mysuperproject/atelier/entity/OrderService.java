package com.mysuperproject.atelier.entity;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderService {
    private Integer orderId;
    private Integer serviceId;
    private Integer quantity;
    private BigDecimal actualPrice;
}
