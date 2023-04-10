package com.digdes.school.model;

import java.util.HashMap;
import java.util.Map;

public class ConfigTable {
    public final static Map<String, String> table = new HashMap<>(Map.of(
            "id", "long",
            "lastName", "string",
            "cost", "double",
            "age", "long",
            "active", "boolean"
    ));
}
