package com.csa.contactsafetyapp;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> numbers;
    List<String> name;
    CallLogRecycler callLogRecycler;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    List<String> safety;
    List<String> number;
    ProgressBar progressBar;
    int Total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();

        Intent receivedIntent = getIntent();
        String tag  = receivedIntent.getStringExtra("Tag");
        Toast.makeText(getApplicationContext(), tag, Toast.LENGTH_SHORT).show();
        if(tag.equals("Volunteer")){
            FirebaseDatabase.getInstance().getReference("Volunteer").child("Volunteers").child(firebaseAuth.getCurrentUser().getPhoneNumber().substring(3)).setValue("");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        Intent intent = new Intent(getApplicationContext(), WarningService.class);
        startService(intent);

        /*safety = new ArrayList<>();
        number = new ArrayList<>();

        progressBar = findViewById(R.id.progressBar);
        Total = 0;


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

        FirebaseDatabase.getInstance().getReference("Users");*/

        checkCallPermissions();

        /*numbers = new ArrayList<>();
        name = new ArrayList<>();

        NameNumber.setContext(getApplicationContext());
        NameNumber.load();
        numbers = NameNumber.getNumbers();
        name = NameNumber.getNames();

        callLogRecycler = new CallLogRecycler(name, numbers, getApplicationContext());
        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(callLogRecycler);

        getPhoneNumbers();*/

        SpaceNavigationView spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem(null, R.drawable.user));
        spaceNavigationView.addSpaceItem(new SpaceItem(null, R.drawable.add_data));
        spaceNavigationView.setCentreButtonIcon(R.drawable.ic_home_black_24dp);
        spaceNavigationView.setSpaceBackgroundColor(getResources().getColor(R.color.white));
        spaceNavigationView.setCentreButtonColor(getResources().getColor(R.color.colorAccent));
        spaceNavigationView.setActiveSpaceItemColor(getResources().getColor(R.color.Active));
        spaceNavigationView.setActiveCentreButtonIconColor(getResources().getColor(R.color.white));

        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Home()).commit();

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Home()).commit();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex){
                    case 0:
                        FirebaseDatabase.getInstance().getReference("Volunteer").child("Volunteers").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                    if(postSnapshot.getKey().equals(firebaseAuth.getCurrentUser().getPhoneNumber().substring(3).trim()) && postSnapshot.getValue(String.class).equals("True")){
                                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Volunteer()).commit();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new EnterData()).commit();
                        break;
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(getApplicationContext(), WarningService.class);
        stopService(intent);
    }

    void checkCallPermissions(){
        int permission = checkSelfPermission("Manifest.permission.READ_PHONE_STATE");
        permission+= checkSelfPermission("Manifest.permission.READ_CONTACTS");
        permission+= checkSelfPermission("Manifest.permission.PROCESS_OUTGOING_CALLS");
        permission+= checkSelfPermission("Manifest.permission.READ_PHONE_NUMBERS");
        permission+= checkSelfPermission("Manifest.permission.CALL_PHONE");

        if(permission!=0) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.CALL_PHONE}, 1001);
        }else {
        }
    }



}

