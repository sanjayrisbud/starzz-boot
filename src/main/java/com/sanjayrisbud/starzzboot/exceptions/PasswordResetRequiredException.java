package com.sanjayrisbud.starzzboot.exceptions;

import lombok.Getter;

@Getter
public class PasswordResetRequiredException extends RuntimeException {

    private final Integer userId;

    public PasswordResetRequiredException(Integer userId) {
        super("Password reset required before logging in.");
        this.userId = userId;
    }

}
