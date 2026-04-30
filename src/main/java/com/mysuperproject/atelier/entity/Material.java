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
public class Material {
    private Integer id;
    private String materialName;
    private String unit;
    private BigDecimal pricePerUnit;
}
