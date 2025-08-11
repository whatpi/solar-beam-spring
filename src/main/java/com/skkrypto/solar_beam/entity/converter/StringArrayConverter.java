package com.skkrypto.solar_beam.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Converter
public class StringArrayConverter implements AttributeConverter<String[], String> {

    @Override
    public String convertToDatabaseColumn(String[] attribute) {
        if (attribute == null || attribute.length == 0) {
            return null;
        }
        // PostgreSQL array literal format: {item1,item2,"item with, comma"}
        return "{" + String.join(",", attribute) + "}";
    }

    @Override
    public String[] convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty() || !dbData.startsWith("{") || !dbData.endsWith("}")) {
            return new String[0];
        }
        // Remove the curly braces
        String content = dbData.substring(1, dbData.length() - 1);
        if (content.isEmpty()) {
            return new String[0];
        }
        return content.split(",");
    }
}