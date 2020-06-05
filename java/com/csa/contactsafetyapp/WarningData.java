package com.csa.contactsafetyapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CheckedOutputStream;

public class WarningData {
    static List<String> possibleCovidContacts = new ArrayList<>();
    static List<Integer> hopsList = new ArrayList<>();
    static Context context;
    static SharedPreferences sharedPreferences;
    static void add(String s, int hops){
        if(!isPresent(s)) {
            possibleCovidContacts.add(0, s);
            hopsList.add(0, hops);
        }
    }
    static boolean isPresent(String s){
        for(int i = 0; i<possibleCovidContacts.size(); i++){
            if(possibleCovidContacts.get(i).equals(s)){
                return true;
            }
        }
        return false;
    }
    static void setContext(Context c){
        context = c;
        sharedPreferences = context.getSharedPreferences("Contacs", Context.MODE_PRIVATE);
    }
    static void save(){
        Gson gson = new Gson();
        String contactsJson = gson.toJson(possibleCovidContacts);
        String hopsJson = gson.toJson(hopsList);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Contact", contactsJson);
        editor.putString("Hops", hopsJson);
        editor.commit();
    }
    static void Load(){
        possibleCovidContacts.clear();
        hopsList.clear();
        Gson gson = new Gson();
        String contactsJson = sharedPreferences.getString("Contact", "");
        String hopsJson = sharedPreferences.getString("Hops" ,"");
        Type type = new TypeToken<List<String>>() {
        }.getType();
        Type type1 = new TypeToken<List<Integer>>(){}.getType();
        List<String> package1 = gson.fromJson(contactsJson, type);
        List<Integer> package2 = gson.fromJson(hopsJson, type1);
        if(package1 == null || package2 == null){

        }
        else{
            possibleCovidContacts.addAll(package1);
            hopsList.addAll(package2);
        }
    }
    static List<String> getPossibleCovidContacts(){
        return possibleCovidContacts;
    }
    static List<Integer> getHopsList(){
        return hopsList;
    }
}
