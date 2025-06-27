package com.pruebaapipok.pruebaapipok.model;

public class AuthenticationRequest {
    private String username;
    private String password;

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return String.valueOf(password); 
    }

    public void setPassword(String password) {
        this.password = password;
    }
}