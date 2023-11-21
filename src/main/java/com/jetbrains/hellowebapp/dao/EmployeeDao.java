package com.jetbrains.hellowebapp.dao;
import com.jetbrains.hellowebapp.entity.Employee;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;


public interface EmployeeDao {
    @SqlQuery("SELECT * FROM employee WHERE name ILIKE :name")
    @RegisterBeanMapper(Employee.class)
    List<Employee> getEmployeesByName(@Bind("name") String name);
}
