package com.example.movieAPI.exceptions;

public class FileExistException extends RuntimeException{
    public FileExistException(String message) {
        super(message);
    }
}
