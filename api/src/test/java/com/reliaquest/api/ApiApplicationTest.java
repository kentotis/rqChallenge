package com.reliaquest.api;

import com.reliaquest.api.model.EmployeeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.http.HttpResponse;
import java.util.Map;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ApiApplicationTest {
    private String url = "http://localhost:8111/";
    @Test
    void testCreateEmployee() {
        //create an employee
        String employeeJson = "{\"employee_name\":\"testName\",\"employee_salary\":44,\"employee_age\":20,\"employee_title\":\"testTitle\",\"employee_email\":\"test@email.com\"}";
        RestClient client = RestClient.create();
        Map<String, Object> json = (Map<String, Object>) client.post().uri(url).body(employeeJson).contentType(MediaType.APPLICATION_JSON).retrieve().body(Map.class);
        String id = (String) json.get("id");
        //call get by id to test it persisted
        Map<String, Object> getJson = (Map<String, Object>) client.get().uri(url + id).retrieve().body(Map.class);
        assertEquals(json, getJson);
    }

    //TODO do some testing around validation for creating, but I am not going to do that right now

    @Test
    void testDeleteEmployee() {
        //create an employee
        String employeeJson = "{\"employee_name\":\"testName\",\"employee_salary\":44,\"employee_age\":20,\"employee_title\":\"testTitle\",\"employee_email\":\"test@email.com\"}";
        RestClient client = RestClient.create();
        Map<String, Object> json = (Map<String, Object>) client.post().uri(url).body(employeeJson).contentType(MediaType.APPLICATION_JSON).retrieve().body(Map.class);
        String id = (String) json.get("id");
        //call get by id to test it persisted
        Map<String, Object> getJson = (Map<String, Object>) client.get().uri(url + id).retrieve().body(Map.class);
        assertEquals(json, getJson);
        //delete by id
        String deletedName = client.delete().uri(url + id).retrieve().body(String.class);
        assertEquals(deletedName, "testName");
        //call get by id to test its now gone
        try {
            String result = client.get().uri(url + id).retrieve().body(String.class);
            fail("Should have thrown a 404 error:" + result);
        } catch (RestClientResponseException e) {
            if (!e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
                fail("Should have thrown a 404 error");
            }
        }
    }

    //TODO add some basic integration tests on the other endpoints, but I dont want to spend more time on it now
}
