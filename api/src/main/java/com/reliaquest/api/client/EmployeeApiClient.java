package com.reliaquest.api.client;

import com.reliaquest.api.model.CreateEmployee;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeListResponse;
import com.reliaquest.api.model.EmployeeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class EmployeeApiClient {
    RestClient client;
    @Value("${employeeApiUrl:http://localhost:8112/api/v1/}")
    private String baseUrl ;
    private final int maxRetryCount = 100;
    public EmployeeApiClient() {
        client = RestClient.create();
    }

    //TODO this is not the right way to do retries. Ideally I would use @retryable with a growing backoff, but I could not get that to work and did not
    //want to spend more time on it
    public List<Employee> getAllEmployees() {
        RestClientResponseException lastException = null;
        for (int i = 0; i < maxRetryCount; i++) {
            try {
                return client.get().uri(baseUrl + "employee").retrieve().body(EmployeeListResponse.class).getData();
            } catch (RestClientResponseException e) {
                lastException = e;
                if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(429)) && i + 1 < maxRetryCount) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    continue;
                }
                throw e;
            }
        }
        throw lastException;
    }

    public Employee getEmployee(String id) {
        RestClientResponseException lastException = null;
        for (int i = 0; i < maxRetryCount; i++) {
            try {
                return client.get().uri(baseUrl + "employee/" + id).retrieve().body(EmployeeResponse.class).getData();
            } catch (RestClientResponseException e) {
                lastException = e;
                if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
                    return null;
                }
                if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(429)) && i + 1 < maxRetryCount) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    continue;
                }
                throw e;
            }
        }
        throw lastException;
    }

    public Employee createEmployee(Employee employeeInput) {
        RestClientResponseException lastException = null;
        for (int i = 0; i < maxRetryCount; i++) {
            try {
                //convert to a employeeInput here because the create api expects different field names
                return client.post().uri(baseUrl+ "employee").body(new CreateEmployee(employeeInput)).retrieve().body(EmployeeResponse.class).getData();
            } catch (RestClientResponseException e) {
                lastException = e;
                if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(429)) && i + 1 < maxRetryCount) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    continue;
                }
                throw e;
            }
        }
        throw lastException;
    }


    public void deleteEmployee(String name) {
        for (int i = 0; i < maxRetryCount; i++) {
            try {
                //TODO the employee api that relies on a DELETE with a body is not standard, is there a better way to do this?
                RestTemplate restTemplate = new RestTemplate();
                //TODO maybe use an object and object mapper here
                String requestBody = "{\"name\": \"" + name + "\"}";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
                ResponseEntity<String> response = restTemplate.exchange(
                        baseUrl + "employee",
                        HttpMethod.DELETE,
                        entity,
                        String.class
                );
                return;
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(429)) && i + 1 < maxRetryCount) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    continue;
                }
                throw e;
            }
        }
    }
}
