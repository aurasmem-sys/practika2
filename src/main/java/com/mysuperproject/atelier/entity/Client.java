package com.mysuperproject.atelier.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    private Integer id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
}
