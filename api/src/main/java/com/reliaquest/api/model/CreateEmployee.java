package com.reliaquest.api.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateEmployee {
    private String name;

    private int salary;

    private int age;

    private String title;
    private String email;
    public CreateEmployee(Employee employee) {
        this.name = employee.getName();
        this.salary = employee.getSalary();
        this.age = employee.getAge();
        this.title = employee.getTitle();
        this.email = employee.getEmail();
    }
}


