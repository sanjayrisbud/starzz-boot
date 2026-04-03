package com.sanjayrisbud.starzzboot.exceptions;

public class PasswordResetRequiredException extends RuntimeException {
    public PasswordResetRequiredException() {
        super("Password reset required before logging in.");
    }
}
