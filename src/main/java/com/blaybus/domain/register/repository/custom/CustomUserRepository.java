package com.blaybus.domain.register.repository.custom;

import com.blaybus.domain.register.entity.User;

public interface CustomUserRepository {
    User findByGoogleId(String googleId);
}
