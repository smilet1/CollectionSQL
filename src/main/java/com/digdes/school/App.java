package com.digdes.school;

import java.util.List;
import java.util.Map;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {

        JavaSchoolStarter javaSchoolStarter = new JavaSchoolStarter();
        javaSchoolStarter.execute("INSERT VALUES 'lastName' = 'Федоров', 'id'=3, 'age'=56, 'active'=true, 'cost'=2.4");
        javaSchoolStarter.execute("INSERT VALUES 'lastName' = 'Федоров', 'id'=4, 'age'=40, 'active'=false");
        javaSchoolStarter.execute("INSERT VALUES 'lastName' = 'Федоров', 'id'=1, 'age'=41, 'active'=true");
        javaSchoolStarter.execute("Update VALUES 'lastName' = 'Петров', 'age'=42");
//        List<Map<String, Object>> res = javaSchoolStarter.execute("Delete where 'active'=true");
        List<Map<String, Object>> select = javaSchoolStarter.execute("Select where 'cost'=null");
        //List<Map<String, Object>> where = Parser.where("'active' = false", res);

//        System.out.println(res);
        System.out.println(select);

    }
}
