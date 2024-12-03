package com.TaiNguyen.ProjectManagementSystems.Modal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class YesNoDeserializer extends JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        if ("Yes".equalsIgnoreCase(value)) {
            return true; // Chuyển "Yes" thành true
        } else if ("No".equalsIgnoreCase(value)) {
            return false; // Chuyển "No" thành false
        }
        return null; // Trả về null nếu không phải "Yes" hoặc "No"
    }
}
