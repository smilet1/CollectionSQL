package com.digdes.school.model;

// Объект хранящий результат парсинга операций сравнения
public class OperationElem {
    public String firsOperand;
    public String secondOperand;
    public Object secondOperandType;
    public String operation;

    public OperationElem() {
        this.firsOperand = "";
        this.secondOperand = "";
        this.secondOperandType = null;
        this.operation = "";
    }

    public void type() throws Exception {

        if(secondOperand.charAt(0) == '\'' && secondOperand.charAt(secondOperand.length()-1) == '\''){

            secondOperandType = "string";
            return;
        }
        if(secondOperand.equals("true") || secondOperand.equals("false")){
            secondOperandType = "boolean";
            return;
        }
        try{
            Long.valueOf(secondOperand);
            secondOperandType="long";
            return;
        }catch(Exception e){

        }

        try{
            Double.valueOf(secondOperand);
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
