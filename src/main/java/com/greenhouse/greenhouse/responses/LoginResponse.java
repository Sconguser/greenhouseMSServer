package com.greenhouse.greenhouse.responses;

public class LoginResponse {
    private Long id;
    private String token;
    private String username;
    private String role;

    public LoginResponse (String token, String username, String role, Long id) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.id = id;
    }

    public String getToken () {
        return token;
    }

    public void setToken (String token) {
        this.token = token;
    }

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public String getRole () {
        return role;
    }

    public void setRole (String role) {
        this.role = role;
    }

    public Long getId () {
        return this.id;
    }

    public void setId (Long id) {
        this.id = id;
    }
}