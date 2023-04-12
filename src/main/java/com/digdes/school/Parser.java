package com.digdes.school;

import com.digdes.school.config.ConfigTable;
import com.digdes.school.model.OperationElem;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Parser {

    //Парсинг операции
    public static OperationElem logicOperation(String operation) throws Exception {
        OperationElem operationElem = new OperationElem();
        CharacterIterator it = new StringCharacterIterator(operation);

        //Считываем первый операнд
        if (it.current() == '\'') {
            it.next();
            while (it.current() != '\'' && it.current() != CharacterIterator.DONE) {
                operationElem.firsOperand += it.current();
                it.next();
            }
            operationElem.firsOperand = operationElem.firsOperand.trim();
            it.next();
        } else {
            throw new Exception("Не коректная запись выражения: " + operation);
        }
        operationElem.firsOperand = operationElem.firsOperand.toLowerCase();

        //Считываем операцию
        boolean flagStringNameOperation = false;
        while (it.current() != CharacterIterator.DONE) {
            if (Character.getNumericValue(it.current()) == -1 && it.current() != ' ') {
                flagStringNameOperation = true;
            }
            if (
                    it.current() == '\'' ||
                            (Character.getNumericValue(it.current()) != -1 &&
                                    Character.getNumericValue(it.current()) < 10) ||
                            (Character.getNumericValue(it.current()) > 10 &&
                                    flagStringNameOperation)
            ) {
                break;
            }
            operationElem.operation += it.current();
            it.next();
        }
        operationElem.operation = operationElem.operation.trim();

        //Считываем второй операнд
        String secondOperand = "";
        while (it.current() != CharacterIterator.DONE) {
            secondOperand += it.current();
            it.next();
        }
        secondOperand = secondOperand.trim();

        operationElem.secondOperand = secondOperand;

        //Записываем типы переменных
        operationElem.type();
        //Проверяем корректность введенных данных
        checkingCorrectsData(operationElem);
        return operationElem;
    }

    //Парсинг условия where
    public static List<Map<String, Object>> where(
            String request,
            List<Map<String, Object>> list) throws Exception {

        List<Map<String, Object>> result = new ArrayList<>(list);

        //Формируем список условий
        List<String> requestArr = Arrays.stream(request.
                        split("(?i)and")).
                map(str -> str.trim()).
                toList();

        //Проходим по списку условий
        for (String elem : requestArr) {
            //Проверяем на наличие or
            if (elem.matches("(.*)(?i)or(.*)")) {
                List<String> orArr = Arrays.stream(
                                elem.
                                        split("(?i)or")).
                        map(String::trim).
                        toList();
                List<Map<String, Object>> resultOrOperation = new ArrayList<>();

                //Фильтруем список по условиям or
                for (String or : orArr) {
                    OperationElem operationElem = logicOperation(or);
                    List<Map<String, Object>> filterListOrOperation = filterList(operationElem, result);
                    resultOrOperation.removeAll(filterListOrOperation);
                    resultOrOperation.addAll(filterListOrOperation);
                }
                result.clear();
                result.addAll(resultOrOperation);
            } else {
                OperationElem operationElem = logicOperation(elem);
                result = filterList(operationElem, result);
            }
        }

        return result;
    }

    //Фильтрует список согласно переданному условию
    private static List<Map<String, Object>> filterList(OperationElem operationElem, List<Map<String, Object>> list) {
        return list.stream().
                filter(operand -> {
                    try {
                        return compare(operand.get(operationElem.firsOperand), operationElem);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    //Проверка корректности ввода уловия
    private static void checkingCorrectsData(OperationElem operationElem) throws Exception {
        if (!ConfigTable.table.containsKey(operationElem.firsOperand)) {
            throw new Exception("Столбца с именем: " + operationElem.firsOperand + " не существует");
        }
        if (!ConfigTable.table.get(operationElem.firsOperand).equals(operationElem.secondOperandType) && !operationElem.secondOperandType.equals("null")) {
            throw new Exception("Переменная: " + operationElem.secondOperand +
                    " не соответствует типу столбца: " +
                    operationElem.firsOperand);
        }
    }

    //Операции условий
    private static boolean compare(Object operand, OperationElem operationElem) throws Exception {
        if(operand == null){
            if(operationElem.secondOperandType == "null"){
                return true;
            }else{
                return false;
            }
        }
        if (operationElem.secondOperandType.equals("string")) {
            String stringOperand = (String) operand;
            stringOperand = stringOperand.toLowerCase();
            switch (operationElem.operation) {
                case "=":
                    return stringOperand.equals(operationElem.secondOperand);
                case "!=":
                    return !stringOperand.equals(operationElem.secondOperand);
                case "like":
                    return like(stringOperand, (String) operationElem.secondOperand);
                case "ilike":
                    return ilike(stringOperand, (String) operationElem.secondOperand);
                default:
                    throw new Exception("Не известная операция: " + operationElem.operation);
            }
        }
        if (operationElem.secondOperandType.equals("boolean")) {
            switch (operationElem.operation) {
                case "=":
                    return operand.equals(operationElem.secondOperand);
                case "!=":
                    return !operand.equals(operationElem.secondOperand);
                default:
                    throw new Exception("Не известная операция: " + operationElem.operation);
            }
        }
        if (operationElem.secondOperandType.equals("long")) {

            switch (operationElem.operation) {
                case "=":
                    return operand.equals(operationElem.secondOperand);
                case "!=":
                    return operand.equals(operationElem.secondOperand);
                case ">=":
                    return (long) operand >= (long) operationElem.secondOperand;
                case "<=":
                    return (long) operand <= (long) operationElem.secondOperand;
                case ">":
                    return (long)operand > (long) operationElem.secondOperand;
                case "<":
                    return (long) operand < (long) operationElem.secondOperand;
                default:
                    throw new Exception("Не известная операция: " + operationElem.operation);
            }
        }
        if (operationElem.secondOperandType.equals("double")) {
            switch (operationElem.operation) {
                case "=":
                    return operand.equals(operationElem.secondOperand);
                case "!=":
                    return !operand.equals(operationElem.secondOperand);
                case ">=":
                    return (double) operand >= (double) operationElem.secondOperand;
                case "<=":
                    return (double) operand <= (double) operationElem.secondOperand;
                case ">":
                    return (double) operand > (double) operationElem.secondOperand;
                case "<":
                    return (double) operand < (double) operationElem.secondOperand;
                default:
                    throw new Exception("Не известная операция: " + operationElem.operation);
            }
        }
        return false;
    }

    private static boolean like(String firstOperand, String secondOperand) {
        String regEx;
        regEx = secondOperand.replaceAll("^[%]|[%]$", "(.*)");
        return firstOperand.matches(regEx);
    }

    private static boolean ilike(String firstOperand, String secondOperand) {
        String regEx;
        regEx = secondOperand.replaceAll("^[%]|[%]$", "(.*)");
        return firstOperand.matches("(?i)" + regEx);
    }
}
