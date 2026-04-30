package com.mysuperproject.atelier.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    private Integer id;
    private Integer clientId;
    private Integer employeeId;
    private LocalDate orderDate;
    private String status;
    private BigDecimal totalPrice;
}
