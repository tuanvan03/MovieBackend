package com.example.movieAPI.dtos.request;

import lombok.Builder;
import lombok.Data;

@Builder
public record ChangePassword (String password, String repeatPassword) {
}
