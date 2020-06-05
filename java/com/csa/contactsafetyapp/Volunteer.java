package com.csa.contactsafetyapp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Volunteer extends Fragment {
    List<String> number;
    View v;


    public Volunteer() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_volunteer, container, false);
        number = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Volunteer").child("CallingList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                number.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    number.add(postSnapshot.getKey());
                }
                VolunteerAdapter volunteerAdapter = new VolunteerAdapter(number, v.getContext());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(v.getContext());
                RecyclerView recyclerView = v.findViewById(R.id.recycler);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(volunteerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return v;
    }

}
