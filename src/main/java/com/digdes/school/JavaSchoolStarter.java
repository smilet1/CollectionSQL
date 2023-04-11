package com.digdes.school;

import com.digdes.school.model.OperationElem;
import com.digdes.school.config.TableFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JavaSchoolStarter {

    private final List<Map<String,Object>> bd = new ArrayList<>();

    public JavaSchoolStarter() {}

    public List<Map<String,Object>> execute(String request) throws Exception {
        //Здесь начало исполнения вашего кода
        String trimRequest = request.trim();
        String operation = trimRequest.trim().substring(0,trimRequest.indexOf(" "));

        List<Map<String,Object>> result = new ArrayList<>();
        switch(operation.toLowerCase()){
            case "select":
                select(trimRequest);
                break;
            case "update":
                update(trimRequest);
                break;
            case "insert":
                return insert(trimRequest);
            case "delete":
                delete(trimRequest);
                break;
            default: throw new Exception("Не известная операция");
        }
        return new ArrayList<Map<String,Object>>();
    }

    private void delete(String request) {
        System.out.println("delete operation...");
        System.out.println("Request: " + request);
    }

    private List<Map<String,Object>> insert(String request) throws Exception {
        System.out.println("insert operation...");
        String values = "";
        if(request.toLowerCase().contains("values")){
            values = request.substring(request.toLowerCase().indexOf("values") + "values".length()).trim();
        }else {
            throw new Exception("Insert не содержит VALUES");
        }
        List<String> valuesArr = Arrays.stream(values.split(",")).map(String::trim).toList();

        Map<String,Object> elem = TableFactory.create();

        for(String value: valuesArr){
            OperationElem operationElem = Parser.logicOperation(value);
            //проверка типа

            //проверка символа
            if(!operationElem.operation.equals("=")){
                throw new Exception("Не коректная запись условия: " +
                        operationElem.firsOperand + " " +
                        operationElem.operation + " " +
                        operationElem.secondOperand);
            }

            if(elem.containsKey(operationElem.firsOperand)){
                elem.put(operationElem.firsOperand, operationElem.secondOperand);
            }
        }

        bd.add(elem);
        List<Map<String,Object>> result = new ArrayList<>(bd);
        return result;
    }

    private void update(String request) {
        System.out.println("update operation...");
        System.out.println("Request: " + request);
    }

    private void select(String request) {
        System.out.println("select operation...");
        System.out.println("Request: " + request);
    }

}
