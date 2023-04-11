package com.digdes.school;

import java.util.List;
import java.util.Map;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {

        JavaSchoolStarter javaSchoolStarter = new JavaSchoolStarter();
        javaSchoolStarter.execute("INSERT VALUES 'lastName' = 'Федоров', 'id'=3, 'age'=56, 'active'=true");
        javaSchoolStarter.execute("INSERT VALUES 'lastName' = 'Федоров', 'id'=4, 'age'=40, 'active'=false");
        List<Map<String, Object>> res = javaSchoolStarter.execute("INSERT VALUES 'lastName' = 'Федоров', 'id'=1, 'age'=40, 'active'=true");

        List<Map<String, Object>> where = Parser.where("'active' = false", res);

        System.out.println(res);
        System.out.println(where);
        //Parser.logicOperation("'lastName' likes 'Федоров'");
    }
}
