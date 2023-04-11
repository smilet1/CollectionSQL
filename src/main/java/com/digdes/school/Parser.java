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
            if (Character.getNumericValue(it.current()) == -1) {
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
        while (it.current() != CharacterIterator.DONE) {
            operationElem.secondOperand += it.current();
            it.next();
        }
        operationElem.secondOperand = operationElem.secondOperand.trim();

        //Записываем типы переменных
        operationElem.type();
        checkingCorrectsData(operationElem);
        //Убераем кавычки у второго операнда
        operationElem.secondOperand = operationElem.secondOperand.replace("'","");
        return operationElem;
    }

    public static List<Map<String, Object>> where(
            String request,
            List<Map<String, Object>> list) throws Exception {

        List<Map<String, Object>> result = new ArrayList<>(list);

        List<String> requestArr = Arrays.stream(request.
                        toLowerCase().
                        split("and")).
                map(str -> str.trim()).
                toList();

        for (String elem : requestArr) {
            OperationElem operationElem = logicOperation(elem);
            result = result.stream().
                    filter(operand -> {
                        try {
                            return compare(operand.get(operationElem.firsOperand), operationElem);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();
        }

        return result;
    }

    private static void checkingCorrectsData(OperationElem operationElem) throws Exception {
        if (!ConfigTable.table.containsKey(operationElem.firsOperand)) {
            throw new Exception("Столбца с именем: " + operationElem.firsOperand + " не существует");
        }
        if (!ConfigTable.table.get(operationElem.firsOperand).equals(operationElem.secondOperandType)) {
            throw new Exception("Переменная: " + operationElem.secondOperand +
                    " не соответствует типу столбца: " +
                    operationElem.firsOperand);
        }
    }

    private static boolean compare(Object operand, OperationElem operationElem) throws Exception {
        if (operationElem.secondOperandType.equals("string")) {
            String stringOperand = (String) operand;
            stringOperand = stringOperand.toLowerCase();

            switch (operationElem.operation) {
                case "=":
                    return stringOperand.equals(operationElem.secondOperand);
                case "!=":
                    return !stringOperand.equals(operationElem.secondOperand);
                case "like":
                    //TODO Нужно реализовать like
                    return false;
                case "ilike":
                    //TODO Нужно реализовать ilike
                    return false;
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
                    return !operand.equals(operationElem.secondOperand);
                case ">=":
                    return (long) operand >= Long.parseLong(operationElem.secondOperand);
                case "<=":
                    return (long) operand <= Long.parseLong(operationElem.secondOperand);
                case ">":
                    return (long) operand > Long.parseLong(operationElem.secondOperand);
                case "<":
                    return (long) operand < Long.parseLong(operationElem.secondOperand);
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
                    return (double) operand >= Double.parseDouble(operationElem.secondOperand);
                case "<=":
                    return (double) operand <= Double.parseDouble(operationElem.secondOperand);
                case ">":
                    return (double) operand > Double.parseDouble(operationElem.secondOperand);
                case "<":
                    return (double) operand < Double.parseDouble(operationElem.secondOperand);
                default:
                    throw new Exception("Не известная операция: " + operationElem.operation);
            }
        }
        return false;
    }
}
