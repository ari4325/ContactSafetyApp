package com.csa.contactsafetyapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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

public class CheckDatabase extends AsyncTask<Context, Integer, String> {
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    Context context;
    int hops = 0;
    CheckDatabase(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(Context... contexts) {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseDatabase.getInstance().getReference("ContactList").child(firebaseAuth.getCurrentUser().getPhoneNumber().substring(3).trim()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    List<String> currentContacts = new ArrayList<>();
                    Toast.makeText(context, dataSnapshot.getChildrenCount() + "", Toast.LENGTH_SHORT).show();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        currentContacts.add(dataSnapshot1.getKey());
                    }

                    for (int i = 0; i < currentContacts.size(); i++) {
                        recursiveSearch(currentContacts.get(i));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return null;
    }


    void recursiveSearch(final String phoneNumber){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    if(postSnapshot.getKey().equals(phoneNumber)) {
                        Toast.makeText(context, postSnapshot.getValue()+"", Toast.LENGTH_SHORT).show();
                        hops++;
                        int safe;
                        int unsafe;
                        int total;
                        HashMap<String, Long> hashMap = (HashMap<String, Long>)postSnapshot.getValue();
                        Toast.makeText(context, hashMap.get("Safe")+"", Toast.LENGTH_SHORT).show();
                        String s = hashMap.get("Safe") + "";
                        String u = hashMap.get("Unsafe") + "";
                        Toast.makeText(context, s+u, Toast.LENGTH_SHORT).show();
                        try {
                            safe = Integer.parseInt(s);
                            unsafe = Integer.parseInt(u);
                            total = safe + unsafe;
                        }catch (Exception e){
                            safe = 0;
                            unsafe = 0;
                            total = 0;
                        }
                        if (total > 0) {
                            if (safe / total >= unsafe / total) {
                                FirebaseDatabase.getInstance().getReference("ContactList").child(postSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            List<String> currentContacts = new ArrayList<>();
                                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                currentContacts.add(dataSnapshot1.getKey());
                                            }
                                            for (int i = 0; i < currentContacts.size(); i++) {
                                                recursiveSearch(currentContacts.get(i));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                WarningData.setContext(context);
                                WarningData.Load();
                                WarningData.add(postSnapshot.getKey(), hops);
                                WarningData.save();
                                Toast.makeText(context, postSnapshot.getKey()+"This was added"+WarningData.possibleCovidContacts.size(), Toast.LENGTH_SHORT).show();
                                hops = 0;
                            }
                        }else{
                            FirebaseDatabase.getInstance().getReference("ContactList").child(postSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Toast.makeText(context, dataSnapshot.getKey()+" covered", Toast.LENGTH_SHORT).show();
                                        List<String> currentContacts = new ArrayList<>();
                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            currentContacts.add(dataSnapshot1.getKey());
                                        }
                                        for (int i = 0; i < currentContacts.size(); i++) {
                                            recursiveSearch(currentContacts.get(i));
                                        }
                                    }else{
                                        Toast.makeText(context, dataSnapshot.getKey()+" no more", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        if (hops != 0)
                            hops--;
                    }
                    else{}

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
