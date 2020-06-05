package com.csa.contactsafetyapp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NameNumber {
    static List<String> names = new ArrayList<>();
    static List<String> numbers = new ArrayList<>();
    static Context context;
    static SharedPreferences sharedPreferences;
    static void setContext(Context ctxt){
        context = ctxt;
        sharedPreferences = context.getSharedPreferences("NumberData", Context.MODE_PRIVATE);
    }
    static void save(){
        Gson gson = new Gson();
        String nameJson = gson.toJson(names);
        String numberJson = gson.toJson(numbers);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nameJson" , nameJson);
        editor.putString("numberJson", numberJson);
        editor.commit();
    }
    static void load(){
        names.clear();
        numbers.clear();
        Gson gson = new Gson();
        String nameJson = sharedPreferences.getString("nameJson", "");
        String numberJson = sharedPreferences.getString("numberJson", "");
        Type type = new TypeToken<List<String>>() {
        }.getType();
        List<String> package1 = gson.fromJson(nameJson, type);
        List<String> package2 = gson.fromJson(numberJson, type);
        if(package1 == null || package2 == null){

        }else{
            names.addAll(package1);
            numbers.addAll(package2);
        }
    }
    static boolean numberIsPresent(String s){
        for(int i = 0; i<numbers.size() ; i++){
            if(numbers.get(i).equals(s)){
                return true;
            }
        }
        return false;
    }
    static void Add(String name, String number){
        if(!numberIsPresent(number)) {
            names.add(name);
            numbers.add(number);
        }
    }

    static List<String> getNames(){
        return names;
    }
    static List<String> getNumbers(){
        return numbers;
    }

    static String getCalculatedDate(String dateFormat, int days) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat(dateFormat);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return s.format(new Date(cal.getTimeInMillis()));
    }

    /*public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }*/
}
