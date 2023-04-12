package com.digdes.school.model;

// Объект хранящий результат парсинга операций сравнения
public class OperationElem {
    public String firsOperand;
    public Object secondOperand;
    public String secondOperandType;
    public String operation;

    public OperationElem() {
        this.firsOperand = "";
        this.secondOperand = "";
        this.secondOperandType = null;
        this.operation = "";
    }

    //Проверяет соответсвие типов firstOperand и secondOperand
    public void type() throws Exception {
        String stringSecondOperand = (String) secondOperand;
        if(stringSecondOperand.charAt(0) == '\'' && stringSecondOperand.charAt(stringSecondOperand.length()-1) == '\''){
            secondOperand = stringSecondOperand.replace("'","");
            secondOperandType = "string";
            return;
        }
        if(stringSecondOperand.equals("null")){
            secondOperand = null;
            secondOperandType = "null";
            return;
        }
        if(stringSecondOperand.equals("true") || stringSecondOperand.equals("false")){
            secondOperand = Boolean.parseBoolean(stringSecondOperand);
            secondOperandType = "boolean";
            return;
        }
        try{
            secondOperand = Long.parseLong(stringSecondOperand);
            secondOperandType="long";
            return;
        }catch(Exception e){

        }

        try{
            secondOperand = Double.parseDouble(stringSecondOperand);
            secondOperandType="double";
        }
        catch (RuntimeException e){
            throw new Exception("Не известный тип данных в операции: " +
                    firsOperand + " " +
                    operation + " " +
                    secondOperand);
        }

    }

    @Override
    public String toString() {
        return "OperationElem{" +
                "firsOperand='" + firsOperand + '\'' +
                ", secondOperand='" + secondOperand + '\'' +
                ", operation='" + operation + '\'' +
                '}';
    }
}
