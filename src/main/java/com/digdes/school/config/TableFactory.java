package com.digdes.school.config;

import java.util.HashMap;
import java.util.Map;

//Создание пустого элемента таблицы
public class TableFactory {
    public static Map<String,Object> create(){
        Map<String,Object> map = new HashMap<>();
        for(String key: ConfigTable.table.keySet()){
            map.put(key,null);
        }
        return map;
    }
}
