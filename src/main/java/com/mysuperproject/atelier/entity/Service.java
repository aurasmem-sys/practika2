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
public class Service {
    private Integer id;
    private String serviceName;
    private String description;
    private BigDecimal basePrice;
}
