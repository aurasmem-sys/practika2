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
public class OrderMaterial {
    private Integer orderId;
    private Integer materialId;
    private BigDecimal quantity;
}
