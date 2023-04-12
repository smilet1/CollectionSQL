package com.digdes.school;

import com.digdes.school.config.ConfigTable;
import com.digdes.school.model.OperationElem;
import com.digdes.school.config.TableFactory;

import java.util.*;

public class JavaSchoolStarter {

    //Объявляем список в которм будет хранится таблица
    private final List<Map<String, Object>> bd = new ArrayList<>();

    public JavaSchoolStarter() {
    }

    public List<Map<String, Object>> execute(String request) throws Exception {
        //удаляем лишние пробелы в начале и конце строки
        String trimRequest = request.trim();
        String operation = "";
        // Считываем первое слово из строки
        if (trimRequest.contains(" ")) {
            operation = trimRequest.trim().substring(0, trimRequest.indexOf(" "));
        } else {
            operation = trimRequest;
        }

        //Вызываем команду
        List<Map<String, Object>> result = new ArrayList<>();
        switch (operation.toLowerCase()) {
            case "select":
                return select(trimRequest);
            case "update":
                return update(trimRequest);
            case "insert":
                return insert(trimRequest);
            case "delete":
                return delete(trimRequest);
            default:
                throw new Exception("Не известная операция");
        }
    }

    private List<Map<String, Object>> delete(String request) throws Exception {
        System.out.println("delete operation...");

        //Проверка на корректность записи delete where
        if (request.replace("  "," ").trim().toLowerCase().indexOf("delete where") != 0) {
            throw new Exception("Не корректная запись выражения: " + request);
        }

        //Получаем подстроку с условиями where
        String where = "";
        if (request.toLowerCase().contains("where")) {
            where = request.substring(request.toLowerCase().indexOf("where") + "where".length()).trim();
        } else {
            throw new Exception("Не корректная запись SELECT: " + request);
        }

        //Получаем список элементов, которые необходимо удалить
        List<Map<String, Object>> removeList = Parser.where(where, bd);

        //Удаляем элементы
        bd.removeAll(removeList);

        return removeList;
    }

    private List<Map<String, Object>> insert(String request) throws Exception {
        System.out.println("insert operation...");

        //Проверка на корректность записи insert values
        if (request.replace("  "," ").trim().toLowerCase().indexOf("insert values") != 0) {
            throw new Exception("Не корректная запись выражения: " + request);
        }

        //Получаем подстроку с условиями values
        String values = "";
        if (request.toLowerCase().contains("values")) {
            values = request.substring(request.toLowerCase().indexOf("values") + "values".length()).trim();
        } else {
            throw new Exception("Insert не содержит VALUES");
        }

        //Формируем список значений
        List<String> valuesArr = Arrays.stream(values.split(",")).map(String::trim).toList();

        //Парсим строку и формируем элемент таблицы
        Map<String, Object> elem = valuesGenerate(valuesArr, TableFactory.create());

        //Добавляем элемент в таблицу
        bd.add(elem);

        List<Map<String, Object>> result = new ArrayList<>();
        result.add(elem);
        return result;
    }

    private List<Map<String, Object>> update(String request) throws Exception {
        System.out.println("update operation...");

        //Проверка на корректность записи update values
        if (request.replace("  ", " ").trim().toLowerCase().indexOf("update values") != 0) {
            throw new Exception("Не корректная запись выражения: " + request);
        }
        //Формируем список с values и условиями where
        List<String> conditions;
        if (request.toLowerCase().contains("values")) {
            conditions = Arrays.stream(
                            request.
                                    substring(
                                            request.
                                                    toLowerCase().
                                                    indexOf("values") +
                                                    "values".length()
                                    ).
                                    split("(?i)where")).
                    map(String::trim).
                    toList();
        } else {
            throw new Exception("UPDATE не содержит VALUES");
        }

        //Проверяем что у нас всего две строки в списке
        if (conditions.size() > 2) {
            throw new Exception("Не корректная запись выражения: ");
        }
        //Формируем список values
        List<String> valuesArr = Arrays.stream(conditions.get(0).split(",")).map(String::trim).toList();

        //На основе values генерируем элементы таблицы
        Map<String, Object> elem = new HashMap<>();
        elem = valuesGenerate(valuesArr, elem);

        //Проверка на наличее where
        if (conditions.size() == 2) {
            //Формируем список условий where
            List<Map<String, Object>> where = Parser.where(conditions.get(1), bd);

            //Удаляем все элементы соответствующие условию where
            bd.removeAll(where);

            //Изменяем элементы списка where
            Map<String, Object> finalElem = elem;
            where = where.stream().map((whereElem) -> {
                for (String key : finalElem.keySet()) {
                    whereElem.put(key, finalElem.get(key));
                }
                return whereElem;
            }).toList();

            //Добавляем измененные элементы в бд
            bd.addAll(where);
            return where;
        } else {
            //Изменяем все элементы бд согласно values
            Map<String, Object> finalElem = elem;
            bd.stream().map((whereElem) -> {
                for (String key : finalElem.keySet()) {
                    whereElem.put(key, finalElem.get(key));
                }
                return whereElem;
            }).toList();
            return bd;
        }
    }

    private List<Map<String, Object>> select(String request) throws Exception {
        System.out.println("select operation...");

        //Возвращяем всю таблицу, если отсутствуют условия
        if (request.trim().toLowerCase().equals("select")) {
            return bd;
        }

        //Проверка на корректность записи Select where
        if (request.replace("  "," ").trim().toLowerCase().indexOf("select where") != 0) {
            throw new Exception("Не корректная запись выражения: " + request);
        }

        //Получаем подстроку с условиями where
        String where = "";
        if (request.toLowerCase().contains("where")) {
            where = request.substring(request.toLowerCase().indexOf("where") + "where".length()).trim();
        } else {
            throw new Exception("Не корректная запись SELECT: " + request);
        }

        //Возвращяем отфильтрованный список
        return Parser.where(where, bd);
    }

    //Парсит строку и на её основе формирует объект операции (a(firstOperand) =(operation) b(secondOperand))
    private Map<String, Object> valuesGenerate(List<String> list, Map<String, Object> map) throws Exception {
        //Проходимся по списку values
        for (String value : list) {
            OperationElem operationElem = Parser.logicOperation(value);

            //проверка символа
            if (!operationElem.operation.equals("=")) {
                throw new Exception("Не коректная запись условия: " +
                        operationElem.firsOperand + " " +
                        operationElem.operation + " " +
                        operationElem.secondOperand);
            }

            //Формеруем map, проверяя наличие firstOperand в таблице
            if (ConfigTable.table.containsKey(operationElem.firsOperand)) {
                map.put(operationElem.firsOperand, operationElem.secondOperand);
            }
        }
        return map;
    }

}
