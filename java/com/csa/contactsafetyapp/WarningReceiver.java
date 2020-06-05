package com.csa.contactsafetyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WarningReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, WarningService.class);
        context.startService(intent1);
    }
}
