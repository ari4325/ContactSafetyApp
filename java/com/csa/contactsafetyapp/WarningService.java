package com.csa.contactsafetyapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
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
    List<String> currentUserContacts = new ArrayList<>();
    int hops = 0;
    int low = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    boolean isPresent(String s){
        return currentUserContacts.contains(s);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseDatabase.getInstance().getReference("ContactList").child(firebaseAuth.getCurrentUser().getPhoneNumber().substring(3).trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                for (final DataSnapshot post1 : dataSnapshot.getChildren()) {
                                    databaseReference.child(post1.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot post : dataSnapshot.getChildren()) {
                                                if (post.getKey().equals("Hops")) {
                                                    //HashMap<String, Long> hashMap = (HashMap<String, Long>) post.getValue();
                                                    int i = Integer.parseInt(post.getValue(Long.class)+"");
                                                    if (i == -1) {
                                                        low = -1;
                                                        WarningData.setContext(getApplicationContext());
                                                        WarningData.Load();
                                                        WarningData.add(post1.getKey(), 0);
                                                        if(!WarningData.isPresent(post.getKey())){
                                                            createNotification(post.getKey());
                                                        }
                                                        WarningData.save();
                                                        FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getCurrentUser().getPhoneNumber().substring(3).trim()).child("Hops").setValue(1);
                                                    } else if (i != 0) {
                                                        if(low != -1){
                                                            if(low >= i) {
                                                                low = i;
                                                            }else{

                                                            }
                                                            WarningData.setContext(getApplicationContext());
                                                            WarningData.Load();
                                                            WarningData.add(post1.getKey(), i);
                                                            WarningData.save();
                                                            int temp = low + 1;
                                                            FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getCurrentUser().getPhoneNumber().substring(3).trim()).child("Hops").setValue(temp);
                                                        }else{
                                                            /*WarningData.setContext(getApplicationContext());
                                                            WarningData.Load();
                                                            WarningData.add(post1.getKey(), i);
                                                            WarningData.save();
                                                            int temp = low + 1;
                                                            FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getCurrentUser().getPhoneNumber().substring(3).trim()).child("Hops").setValue(1);*/
                                                        }
                                                    }
                                                    Toast.makeText(getApplicationContext(), low + "" , Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }finally {
                    handler.postDelayed(this, 3600000);
                }

                for(int i = 0; i<WarningData.possibleCovidContacts.size(); i++) {
                    FirebaseDatabase.getInstance().getReference("Users").child(WarningData.possibleCovidContacts.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot post : dataSnapshot.getChildren()){
                                if(post.getKey().equals("Hops")){
                                    int i = Integer.parseInt(post.getValue(Long.class)+"");
                                    if(i == 0){
                                        WarningData.setContext(getApplicationContext());
                                        WarningData.Load();
                                        WarningData.possibleCovidContacts.remove(i);
                                        WarningData.hopsList.remove(i);
                                        WarningData.save();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
        handler.post(runnable);

        /*Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_SHORT).show();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                FirebaseDatabase.getInstance().getReference("ContactList").child(firebaseAuth.getCurrentUser().getPhoneNumber().substring(3).trim()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            List<String> currentContacts = new ArrayList<>();
                            Toast.makeText(getApplicationContext(), dataSnapshot.getChildrenCount() + "", Toast.LENGTH_SHORT).show();
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
                });*/

        return START_STICKY;
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

    void createNotification(String s){
        Intent resultIntent = new Intent(getApplicationContext() , MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Contact Alert!!!")
                .setContentText("One of your contact"+getContactName(s, getApplicationContext())+" has been marked unsafe and might possess risk of Covid-19")
                .setContentIntent(resultPendingIntent)
                .setPriority(2);

        Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarm);
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel("CHANNEL_1", "Personal Notification channel", NotificationManager.IMPORTANCE_HIGH);
            builder.setChannelId("CHANNEL_1");
        }

        final Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void makeNotificationChannel(String id, String name, int importance)
    {
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setShowBadge(true); // set false to disable badges, Oreo exclusive

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }

}