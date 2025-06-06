package com.greenhouse.greenhouse.responses;

import com.greenhouse.greenhouse.models.Role;

public class UserResponse {

    private Long id;

    private String username;

    private Role role;

    public UserResponse (String username, Role role, Long id) {
        this.username = username;
        this.role = role;
        this.id = id;
    }

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public Role getRole () {
        return role;
    }

    public void setRole (Role role) {
        this.role = role;
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

}
