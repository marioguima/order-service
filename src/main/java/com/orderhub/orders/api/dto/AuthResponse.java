package com.orderhub.orders.api.dto;

public record AuthResponse(
        String token,
        String email,
        String name
) {}
