package org.example.project_module4_dvc.converter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.Map;

@Converter
public class JsonToMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            // Chuyển Map Java thành chuỗi JSON
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            // Xử lý lỗi hoặc ném ngoại lệ
            throw new RuntimeException("Lỗi khi chuyển Map sang JSON", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            // Chuyển chuỗi JSON từ CSDL thành Map Java
            return objectMapper.readValue(dbData, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            // Xử lý lỗi hoặc ném ngoại lệ
            throw new RuntimeException("Lỗi khi chuyển JSON sang Map", e);
        }
    }
}