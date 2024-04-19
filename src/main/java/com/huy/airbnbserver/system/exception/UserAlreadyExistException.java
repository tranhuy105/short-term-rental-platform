package com.huy.airbnbserver.system.exception;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException() {
        super("User with this email already exists");
    }
}
