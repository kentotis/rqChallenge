package com.reliaquest.api.controller;

import com.reliaquest.api.client.EmployeeApiClient;
import com.reliaquest.api.model.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerTest {
    @Autowired
    private TestRestTemplate template;

    @MockBean
    private EmployeeApiClient employeeApiClient;

    private Employee getTestEmployee() {
        Employee testEmployee = new Employee();
        testEmployee.setAge(1);
        testEmployee.setEmail("test@email.com");
        testEmployee.setId("someId");
        testEmployee.setName("testName");
        testEmployee.setSalary(44);
        testEmployee.setTitle("testTitle");
        return testEmployee;
    }
    @Test
    public void testGetAllEmployees() throws Exception {
        when(employeeApiClient.getAllEmployees()).thenReturn(List.of(getTestEmployee()));
        ResponseEntity<String> response = template.getForEntity("/", String.class);
        assertThat(response.getBody()).isEqualTo("[{\"id\":\"someId\",\"employee_name\":\"testName\",\"employee_salary\":44,\"employee_age\":1,\"employee_title\":\"testTitle\",\"employee_email\":\"test@email.com\"}]");
    }

    @Test
    public void testGetEmployeesByNameSearch() {
        Employee employee = getTestEmployee();
        when(employeeApiClient.getAllEmployees()).thenReturn(List.of(employee));
        ResponseEntity<String> response = template.getForEntity("/search/" + employee.getName(), String.class);
        assertThat(response.getBody()).isEqualTo("[{\"id\":\"someId\",\"employee_name\":\"testName\",\"employee_salary\":44,\"employee_age\":1,\"employee_title\":\"testTitle\",\"employee_email\":\"test@email.com\"}]");
    }

    @Test
    public void testGetEmployeesByNameSearchWithCaseDifference() {
        Employee employee = getTestEmployee();
        when(employeeApiClient.getAllEmployees()).thenReturn(List.of(employee));
        ResponseEntity<String> response = template.getForEntity("/search/" + employee.getName().toUpperCase(), String.class);
        assertThat(response.getBody()).isEqualTo("[{\"id\":\"someId\",\"employee_name\":\"testName\",\"employee_salary\":44,\"employee_age\":1,\"employee_title\":\"testTitle\",\"employee_email\":\"test@email.com\"}]");
    }

    @Test
    public void testGetEmployeesByNameSearchSubString() {
        Employee employee = getTestEmployee();
        when(employeeApiClient.getAllEmployees()).thenReturn(List.of(employee));
        ResponseEntity<String> response = template.getForEntity("/search/" + employee.getName().substring(0,1), String.class);
        assertThat(response.getBody()).isEqualTo("[{\"id\":\"someId\",\"employee_name\":\"testName\",\"employee_salary\":44,\"employee_age\":1,\"employee_title\":\"testTitle\",\"employee_email\":\"test@email.com\"}]");
    }

    @Test
    public void testGetEmployeesByNameSearchNoMatch() {
        Employee employee = getTestEmployee();
        when(employeeApiClient.getAllEmployees()).thenReturn(List.of(employee));
        ResponseEntity<String> response = template.getForEntity("/search/nottherightname", String.class);
        assertThat(response.getBody()).isEqualTo("[]");
    }

    @Test
    public void testGetEmployee() {
        Employee employee = getTestEmployee();
        when(employeeApiClient.getEmployee(employee.getId())).thenReturn(employee);
        ResponseEntity<String> response = template.getForEntity("/" + employee.getId(), String.class);
        assertThat(response.getBody()).isEqualTo("{\"id\":\"someId\",\"employee_name\":\"testName\",\"employee_salary\":44,\"employee_age\":1,\"employee_title\":\"testTitle\",\"employee_email\":\"test@email.com\"}");
    }

    @Test
    public void testGetEmployeeNotFound() {
        Employee employee = getTestEmployee();
        when(employeeApiClient.getEmployee(employee.getId())).thenReturn(null);
        ResponseEntity<String> response = template.getForEntity("/" + employee.getId(), String.class);
        assertThat(response.getBody()).isEqualTo(null);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void testGetHighestSalary() {
        Employee employee = getTestEmployee();
        employee.setSalary(80);
        Employee employee1 = getTestEmployee();
        employee1.setSalary(100);
        List<Employee> list = new ArrayList<>();
        list.add(employee);
        list.add(employee1);
        when(employeeApiClient.getAllEmployees()).thenReturn(list);
        ResponseEntity<String> response = template.getForEntity("/highestSalary", String.class);
        assertThat(response.getBody()).isEqualTo( "" + employee1.getSalary());
    }

    @Test
    public void testGetHighestSalaryNoEmployees() {
        List<Employee> list = new ArrayList<>();
        when(employeeApiClient.getAllEmployees()).thenReturn(list);
        ResponseEntity<String> response = template.getForEntity("/highestSalary", String.class);
        assertThat(response.getBody()).isEqualTo( "0");
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNames() {
        Employee employee = getTestEmployee();
        employee.setSalary(80);
        employee.setName("second");
        Employee employee1 = getTestEmployee();
        employee1.setSalary(100);
        employee1.setName("first");
        List<Employee> list = new ArrayList<>();
        list.add(employee);
        list.add(employee1);
        when(employeeApiClient.getAllEmployees()).thenReturn(list);
        ResponseEntity<String> response = template.getForEntity("/topTenHighestEarningEmployeeNames", String.class);
        assertThat(response.getBody()).isEqualTo( "[\"first\",\"second\"]");
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNamesOnlyReturns10() {
        List<Employee> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            list.add(getTestEmployee());
        }
        when(employeeApiClient.getAllEmployees()).thenReturn(list);
        ResponseEntity<String> response = template.getForEntity("/topTenHighestEarningEmployeeNames", String.class);
        assertThat(response.getBody()).isEqualTo( "[\"testName\",\"testName\",\"testName\",\"testName\",\"testName\",\"testName\",\"testName\",\"testName\",\"testName\",\"testName\"]");
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNamesNoEmployees() {
        List<Employee> list = new ArrayList<>();
        when(employeeApiClient.getAllEmployees()).thenReturn(list);
        ResponseEntity<String> response = template.getForEntity("/topTenHighestEarningEmployeeNames", String.class);
        assertThat(response.getBody()).isEqualTo( "[]");
    }

    //TODO write tests for the create and delete endpoints, but I am out of time for this
}
