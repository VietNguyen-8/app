package com.bundung.models;

public class User {
    private int userId;
    private String username;
    private String fullName;
    private String role;
    private String token;

    public User() {}

    public User(int userId, String username, String fullName, String role, String token) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.token = token;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // Role constants
    public static final String ROLE_WAITER = "phuc_vu";
    public static final String ROLE_KITCHEN = "bep";
    public static final String ROLE_OWNER = "chu_quan";
}