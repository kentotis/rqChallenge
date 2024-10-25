package com.reliaquest.api.model;

public class EmployeeResponse {
    private Employee data;
    private String status;

    public Employee getData() {
        return data;
    }

    public void setData(Employee data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
