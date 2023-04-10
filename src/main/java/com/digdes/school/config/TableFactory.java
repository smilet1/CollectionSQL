package com.digdes.school;

import com.digdes.school.model.ConfigTable;

import java.util.HashMap;
import java.util.Map;

public class TableFactory {
    public final static Map<String,Object> create(){
        Map<String,Object> map = new HashMap<>();
        for(String key: ConfigTable.table.keySet()){
            map.put(key,null);
        }
        return map;
    }
}
