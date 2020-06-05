package com.csa.contactsafetyapp;

import java.util.ArrayList;
import java.util.List;

public class TempNumbers {
    static List<String> numbers = new ArrayList<>();
    static void setNumbers(List<String> num){
        numbers.addAll(num);
    }
    static List<String> getNumbers(){
        return numbers;
    }
}
