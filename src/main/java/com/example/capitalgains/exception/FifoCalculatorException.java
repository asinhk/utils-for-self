package com.example.capitalgains.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FifoCalculatorException extends Exception{
    private final String errorMsg;

}
