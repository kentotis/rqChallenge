package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Employee {
    private String id;
    @JsonProperty("employee_name")
    @NotBlank
    private String name;
    @JsonProperty("employee_salary")
    @Positive
    private int salary;
    @JsonProperty("employee_age")
    @Min(16)
    @Max(75)
    private int age;
    @JsonProperty("employee_title")
    @NotBlank
    private String title;
    @JsonProperty("employee_email")
    private String email;

}
