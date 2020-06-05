package com.csa.contactsafetyapp;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {
    List<String> numbers;
    List<String> name;
    CallLogRecycler callLogRecycler;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    List<String> safety;
    List<String> number;
    ProgressBar progressBar;
    View v;
    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        safety = new ArrayList<>();

        name = new ArrayList<>();
        numbers = new ArrayList<>();

        progressBar = v.findViewById(R.id.progressBar);

        List<String> threatNumbers = new ArrayList<>();
        WarningData.setContext(getActivity().getApplicationContext());
        WarningData.Load();
        for(int i = 0; i<WarningData.possibleCovidContacts.size(); i++){
            threatNumbers.add(i, getContactName(WarningData.possibleCovidContacts.get(i), v.getContext()));
        }
        ThreatAdapter threatAdapter = new ThreatAdapter(threatNumbers, WarningData.getPossibleCovidContacts(), v.getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = v.findViewById(R.id.threatRecycler);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(threatAdapter);

        FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getCurrentUser().getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    HashMap<String, Integer> hashMap = new HashMap<>();
                    hashMap.put("Positive", 0);
                    hashMap.put("Negative", 0);
                    FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getCurrentUser().getPhoneNumber().replaceAll("91", "").replace('+', ' ').trim()).setValue(hashMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        WarningData.setContext(getActivity().getApplicationContext());
        WarningData.Load();
        int contacts = getPhoneNumbers();
        int unsafe = WarningData.getPossibleCovidContacts().size();
        progressBar.setProgress(contacts - unsafe);
        progressBar.setMax(contacts);
        TextView safeCount = v.findViewById(R.id.safeCount);
        TextView unsafeCount = v.findViewById(R.id.unsafeCount);
        ImageView safetyImage = v.findViewById(R.id.safetyImage);
        TextView safetyText = v.findViewById(R.id.safetyText);

        safeCount.setText(contacts - unsafe+"");
        unsafeCount.setText(unsafe+"");

        if(unsafe!=0){
            v.findViewById(R.id.safetyBadge).setBackgroundColor(v.getResources().getColor(R.color.Active));
            safetyImage.setImageResource(R.drawable.danger);
            safetyText.setText("You have threat from "+unsafe+" contact(s)");
        }

        NameNumber.setContext(getActivity().getApplicationContext());
        NameNumber.load();
        List<String> name1 = NameNumber.getNames();
        List<String> number1 = NameNumber.getNumbers();

        RecyclerView Recycler=v.findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(v.getContext());
        CallLogRecycler callLogRecycler= new CallLogRecycler(name1, number1, v.getContext(), "Log");
        Recycler.setLayoutManager(linearLayoutManager1);
        Recycler.setAdapter(callLogRecycler);




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


    private int getPhoneNumbers() {
        int count = 0;
        ContentResolver contentResolver = getActivity().getContentResolver();
        String contactId = null;
        String displayName = null;
        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
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

        Toast.makeText(getContext(), numbers.size()+"", Toast.LENGTH_SHORT).show();

        for(int i = 0; i<numbers.size(); i++){
            updateDatabase(numbers.get(i));
            FirebaseDatabase.getInstance().getReference("ContactList").child(firebaseAuth.getCurrentUser().getPhoneNumber().substring(3).trim()).child(numbers.get(i)).child("Temp").setValue("");
        }

        return numbers.size();

    }

    void updateDatabase(final String number){
        databaseReference.child(number).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int total;
                int negative;
                int positive;
                if(dataSnapshot.exists()) {
                    /*HashMap<String, Long> hashMap = (HashMap<String, Long>) dataSnapshot.getValue();
                    negative = Integer.parseInt(hashMap.get("Negative")+"");
                    positive = Integer.parseInt(hashMap.get("Positive")+"");
                    total = positive+negative;
                    if(total>0) {
                        safety.add(((positive / total) > (negative / total)) ? "Safe" : "Unsafe");
                    }else{
                        safety.add("Safe");
                    }*/
                }else{
                    HashMap<String, Integer> hashMap = new HashMap<>();
                    hashMap.put("Safe", 0);
                    hashMap.put("Unsafe", 0);
                    FirebaseDatabase.getInstance().getReference("Users").child(number).setValue(hashMap);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
