package com.example.studycore.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ThemePreference {
    LIGHT, DARK;

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static ThemePreference fromValue(String value) {
        if (value == null) return null;
        return ThemePreference.valueOf(value.toUpperCase());
    }
}


