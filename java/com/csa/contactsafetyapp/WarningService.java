package com.csa.contactsafetyapp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
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

public class WarningService extends Service {
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    int hops = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(getApplicationContext(), "Service is Running", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_SHORT).show();
        CheckDatabase checkDatabase = new CheckDatabase(getApplicationContext());
        checkDatabase.execute();
        return START_STICKY;
    }



    @Override
    public void onDestroy(){
        super.onDestroy();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, WarningReceiver.class);
        this.sendBroadcast(broadcastIntent);
    }

}
