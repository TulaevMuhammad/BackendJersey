package com.jetbrains.hellowebapp.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Employee {
    private int id;
    private String name;
    private int departmentId;
}
