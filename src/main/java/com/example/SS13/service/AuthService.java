package com.example.SS13.service;

import com.example.SS13.dto.RegisterRequest;
import com.example.SS13.entity.User;

public interface AuthService {

    User register(RegisterRequest request);
}
