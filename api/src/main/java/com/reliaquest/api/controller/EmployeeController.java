package com.reliaquest.api.controller;

import com.reliaquest.api.client.EmployeeApiClient;
import com.reliaquest.api.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
public class EmployeeController implements IEmployeeController<Employee, Employee> {

    @Autowired
    EmployeeApiClient employeeApiClient;

    @Override
    @GetMapping()
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return new ResponseEntity<>(employeeApiClient.getAllEmployees(), HttpStatus.OK);
    }

    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        List<Employee> employees = employeeApiClient.getAllEmployees();
        List<Employee> matches = new ArrayList<>();
        for (Employee employee : employees) {
            if (employee.getName().toLowerCase().contains(searchString.toLowerCase())) {
                matches.add(employee);
            }
        }
        return new ResponseEntity<>(matches, HttpStatus.OK);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(String id) {
        Employee employee = employeeApiClient.getEmployee(id);
        if (employee == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(employee, HttpStatus.OK);
        }
    }

    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        //if there are no employees, return 0 as the highest salary. We are assuming salary cant be less then 0
        int highestSalary = 0;
        List<Employee> allEmployees = employeeApiClient.getAllEmployees();
        //sort descending by salary so the highest salary is first
        allEmployees.sort(Comparator.comparing(Employee::getSalary).reversed());
        if (!allEmployees.isEmpty()) {
            highestSalary = allEmployees.get(0).getSalary();
        }
        return new ResponseEntity<>(highestSalary, HttpStatus.OK);
    }

    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<String> highestEarningNames = new ArrayList<>();
        List<Employee> allEmployees = employeeApiClient.getAllEmployees();
        //sort descending by salary so the highest salary is first
        allEmployees.sort(Comparator.comparing(Employee::getSalary).reversed());
        for (Employee employee : allEmployees) {
            highestEarningNames.add(employee.getName());
            if (highestEarningNames.size() == 10) {
                break;
            }
        }
        return new ResponseEntity<>(highestEarningNames, HttpStatus.OK);
    }

    @Override
    @PostMapping()
    public ResponseEntity<Employee> createEmployee(Employee employeeInput) {
        //most validation is done through annotations on the model
        if (employeeInput.getId() != null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Employee created = employeeApiClient.createEmployee(employeeInput);
        return new ResponseEntity<>(created, HttpStatus.OK);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(String id) {
        Employee employee = employeeApiClient.getEmployee(id);
        if (employee != null) {
            employeeApiClient.deleteEmployee(employee.getName());
            return new ResponseEntity<>(employee.getName(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
