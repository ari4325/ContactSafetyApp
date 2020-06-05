package com.csa.contactsafetyapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class StartActivity extends AppCompatActivity {
    String number;
    TextView name;
    TextView button;
    FirebaseAuth firebaseAuth;
    String mVerificationId;
    ProgressDialog progressDialog;
    boolean aSwitchSelected;
    Switch aSwitch;
    boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        checkCallPermissions();
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null){
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("Tag", "");
            startActivity(intent);
        }
        aSwitch = findViewById(R.id.switch1);
        aSwitchSelected = aSwitch.isSelected();
        number = "";
        check = false;
        name = findViewById(R.id.phnOtp);
        button = findViewById(R.id.nextBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!check) {
                    aSwitch.setVisibility(View.GONE);
                    number = name.getText().toString();
                    name.setText("");
                    button.setText("Enter OTP");
                    name.setHint("Enter the OTP sent");
                    check = true;
                    findViewById(R.id.displayText).setVisibility(View.VISIBLE);
                    sendVerificationCode(number);
                }else{
                    if(name.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(), "Enter a code first", Toast.LENGTH_SHORT).show();
                    }else {
                        progressDialog = new ProgressDialog(StartActivity.this);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Verifying Code...");
                        progressDialog.show();
                        verifyVerificationCode(name.getText().toString());
                    }
                }
            }
        });


    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                name.setText(code);
                progressDialog = new ProgressDialog(StartActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Verifying Code...");
                progressDialog.show();
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(StartActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(StartActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(aSwitchSelected) {
                                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("Tag", "Volunteer");
                                startActivity(intent);
                            }
                            Intent intent = new Intent(StartActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("Tag", "Normal");
                            startActivity(intent);

                        } else {
                            progressDialog.dismiss();

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Toast.makeText(StartActivity.this, message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    void checkCallPermissions(){
        int permission = checkSelfPermission("Manifest.permission.READ_PHONE_STATE");
        permission+= checkSelfPermission("Manifest.permission.READ_CONTACTS");
        permission+= checkSelfPermission("Manifest.permission.PROCESS_OUTGOING_CALLS");
        permission+= checkSelfPermission("Manifest.permission.READ_PHONE_NUMBERS");

        if(permission!=0) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.READ_PHONE_NUMBERS}, 1001);
        }else {
        }
    }
}
