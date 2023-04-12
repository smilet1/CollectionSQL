package com.digdes.school.config;

import java.util.HashMap;
import java.util.Map;

//Структура таблицы
public class ConfigTable {
    public final static Map<String, String> table = new HashMap<>(Map.of(
            "id".toLowerCase(), "long",
            "lastName".toLowerCase(), "string",
            "cost".toLowerCase(), "double",
            "age".toLowerCase(), "long",
            "active".toLowerCase(), "boolean"
    ));
}
