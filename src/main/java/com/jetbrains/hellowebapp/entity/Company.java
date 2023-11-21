package com.jetbrains.hellowebapp.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Company {
    private int id;
    private String name;
}
