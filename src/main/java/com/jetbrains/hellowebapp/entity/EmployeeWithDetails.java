package com.jetbrains.hellowebapp.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EmployeeWithDetails {
    private int employeeId;
    private String employeeName;
    private int departmentId;
    private String departmentName;
    private int companyId;
    private String companyName;

}
