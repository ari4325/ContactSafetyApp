package com.csa.contactsafetyapp;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EnterData extends Fragment {
    View v;
    List<String> name = new ArrayList<>();
    List<String> numbers = new ArrayList<>();


    public EnterData() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_enter_data, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.Recycler);
        LoadContacts loadContacts = new LoadContacts(v.getContext(), recyclerView, getActivity());
        try {
            loadContacts.execute();
        }catch (Exception e){
            Toast.makeText(v.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        return v;
    }

    public String getContactName(final String phoneNumber, Context context)
    {
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName="";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }


    /*private void getPhoneNumbers() {

        ContentResolver contentResolver = getActivity().getContentResolver();
        String contactId = null;
        String displayName = null;
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {

                    contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


                    Cursor phoneCursor = getActivity().getContentResolver().query(
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

    }*/


}
