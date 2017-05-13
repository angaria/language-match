package com.angaria.languagematch.ikm;

public class MyClass extends Chips implements ParentInterface{

    public String maMethoda(){
        return "denver";
    }

    public static void main ( String[] args){
        ParentInterface obj = new MyClass();
        ((MyClass)obj).maMethoda();
    }

}

interface ParentInterface{
    public static String maMethod(){
        return "chips";
    }
}

class Chips{
    int a = 0;
}
