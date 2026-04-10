package com.example.capitalgains.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvalidInputException extends Exception{
    private final String errorMsg;
}
