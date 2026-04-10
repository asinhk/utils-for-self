package com.example.capitalgains.util;

import org.modelmapper.ModelMapper;

public class ModelMapperHolder {
    private static ModelMapper modelMapper = new ModelMapper();

    public static ModelMapper getModelMapper() {
        return modelMapper;
    }
}
