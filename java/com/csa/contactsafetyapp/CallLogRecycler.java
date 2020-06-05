package com.csa.contactsafetyapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class CallLogRecycler extends RecyclerView.Adapter<CallLogRecycler.Holder> {
    List<String> name;
    List<String> number;
    Context context;
    String s;
    CallLogRecycler(List<String> name, List<String> number, Context context, String s){
        this.name = name;
        this.number = number;
        this.context = context;
        this.s = s;
    }
    @NonNull
    @Override
    public CallLogRecycler.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.number_item, viewGroup, false);
        CallLogRecycler.Holder holder = new CallLogRecycler.Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CallLogRecycler.Holder holder, final int i) {
        NameNumber.setContext(context);
        NameNumber.load();
        holder.name.setText(name.get(i));
        holder.number.setText(number.get(i));
        holder.number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        holder.mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.extendingLayout.setVisibility(View.VISIBLE);
            }
        });
        holder.safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Thanks for your input, please wait while we add it in our servers...");
                progressDialog.setIndeterminate(true);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            if(dataSnapshot1.getKey().equals(number.get(i))){
                                HashMap<String, Long> hashMap= (HashMap<String, Long>)dataSnapshot1.getValue();
                                FirebaseDatabase.getInstance().getReference("Users").child(number.get(i)).child("Safe").setValue(hashMap.get("Safe")+(long)1);
                                FirebaseDatabase.getInstance().getReference("Volunteer").child("CallingList").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(3).trim()).setValue("");
                                progressDialog.dismiss();
                                if (s.equals("Log")){
                                    NameNumber.names.remove(i);
                                    NameNumber.numbers.remove(i);
                                }
                                name.remove(i);
                                number.remove(i);
                                NameNumber.save();
                                notifyItemChanged(i);
                                notifyItemRangeChanged(i, name.size());
                                Toast.makeText(context, "Marked Safe", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        holder.unsafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Thanks for your input, please wait while we add it to our servers...");
                progressDialog.setIndeterminate(true);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            if(dataSnapshot1.getKey().equals(number.get(i))){
                                HashMap<String, Long> hashMap= (HashMap<String, Long>)dataSnapshot1.getValue();
                                FirebaseDatabase.getInstance().getReference("Users").child(number.get(i)).child("Unsafe").setValue(hashMap.get("Unsafe")+(long)1);
                                progressDialog.dismiss();
                                if (s.equals("Log")){
                                    NameNumber.names.remove(i);
                                    NameNumber.numbers.remove(i);
                                }
                                name.remove(i);
                                number.remove(i);
                                NameNumber.save();
                                notifyItemChanged(i);
                                notifyItemRangeChanged(i, name.size());
                                Toast.makeText(context, "Marked Unsafe", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView number;
        TextView name;
        Button mark;
        LinearLayout extendingLayout;
        TextView unsafe;
        TextView safe;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);
            mark = itemView.findViewById(R.id.mark);
            extendingLayout = itemView.findViewById(R.id.extendingLayout);
            unsafe = itemView.findViewById(R.id.unsafe);
            safe = itemView.findViewById(R.id.safe);
        }
    }
}
