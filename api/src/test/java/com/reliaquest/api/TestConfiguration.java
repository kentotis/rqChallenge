package com.reliaquest.api;

import com.reliaquest.api.client.EmployeeApiClient;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class TestConfiguration {
    @Bean
    @Primary
    public EmployeeApiClient employeeApiClient() {
        return Mockito.mock(EmployeeApiClient.class);
    }
}
