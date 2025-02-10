package com.ProjectSync.ProjectSync.dtos;

public class LoginResponse {
    private String token;

    private long expiresIn;


    // Setter para o token
    public LoginResponse setToken(String token) {
        this.token = token;
        return this; // Retorna a instância de LoginResponse para permitir o encadeamento
    }

    // Setter para o expiresIn
    public LoginResponse setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
        return this; // Retorna a instância de LoginResponse para permitir o encadeamento
    }

    // Getters, caso necessário
    public String getToken() {
        return token;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }
}
