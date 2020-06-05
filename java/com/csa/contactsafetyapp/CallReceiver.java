package com.csa.contactsafetyapp;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class CallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NameNumber.setContext(context);
        NameNumber.load();
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String number = "";
        String name = "";
        if(state == null){
            Toast.makeText(context , intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER), Toast.LENGTH_SHORT).show();
            number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            name = getContactDisplayNameByNumber(number, context);
            number = number.replaceAll(" ", "");
            if(number.length()==13 || number.length() == 11){
                number = number.substring(3).trim();
                if(!NameNumber.numberIsPresent(number) && !name.equals("Unknown number") ) {
                    NameNumber.Add(name, number);
                    NameNumber.save();
                }
            }else if(number.length() == 10){
                if(!NameNumber.numberIsPresent(number) && !name.equals("Unknown number") ) {
                    NameNumber.Add(name, number);
                    NameNumber.save();
                }
            }

        }else if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            name = getContactDisplayNameByNumber(number, context);
            number = number.replaceAll(" ", "");
            Toast.makeText(context , name  + number, Toast.LENGTH_SHORT).show();
            if(number.length()==13 || number.length() == 11){
                number = number.substring(3).trim();
                if(!NameNumber.numberIsPresent(number) && !name.equals("Unknown number") ) {
                    NameNumber.Add(name, number);
                    NameNumber.save();
                }
            }else if(number.length() == 10){
                if(!NameNumber.numberIsPresent(number) && !name.equals("Unknown number") ) {
                    NameNumber.Add(name, number);
                    NameNumber.save();
                }
            }

        }
    }
    public String getContactDisplayNameByNumber(String number,Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "";

        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, null, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                // this.id =
                // contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                // String contactId =
                // contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            } else {
                name = "Unknown number";
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }
}
