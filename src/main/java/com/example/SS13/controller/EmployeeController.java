package com.example.SS13.controller;

import com.example.SS13.entity.Employee;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    @GetMapping
    public List<Employee> getEmployees() {
        return Arrays.asList(
                new Employee(1L, "Nguyen Van A", 1000.0),
                new Employee(2L, "Tran Thi B", 1500.0),
                new Employee(3L, "Le Van C", 2000.0)
        );
    }
}
