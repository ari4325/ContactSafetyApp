package com.csa.contactsafetyapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LoadContacts extends AsyncTask<RecyclerView, Integer, List<String>> {
    Context context;
    RecyclerView recyclerView;
    Activity activity;
    List<String> name = new ArrayList<>();
    List<String> numbers = new ArrayList<>();
    LoadContacts(Context context, RecyclerView recyclerView, Activity activity){
        this.context = context;
        this.recyclerView = recyclerView;
        this.activity = activity;
    }

    private void getPhoneNumbers() {

        ContentResolver contentResolver = context.getContentResolver();
        String contactId = null;
        String displayName = null;
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {

                    contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


                    Cursor phoneCursor = context.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null);

                    if (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumber = phoneNumber.replaceAll(" ", "");
                        if(phoneNumber.length()==13 || phoneNumber.length() == 11) {
                            name.add(displayName);
                            numbers.add(phoneNumber.substring(3).trim());
                        }else if(phoneNumber.length() == 10){
                            name.add(displayName);
                            numbers.add(phoneNumber);
                        }
                    }

                    phoneCursor.close();

                }
            }
        }
        cursor.close();
        setRecyclerView();

    }

    @Override
    protected List<String> doInBackground(RecyclerView... recyclerViews) {
        getPhoneNumbers();

        return null;
    }

    private void setRecyclerView(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
                CallLogRecycler callLogRecycler= new CallLogRecycler(name, numbers, context, "");
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(callLogRecycler);
            }
        });
    }
}
