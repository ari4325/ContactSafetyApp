package com.csa.contactsafetyapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class VolunteerAdapter extends RecyclerView.Adapter<VolunteerAdapter.Holder> {
    List<String> number;
    Context context;
    VolunteerAdapter(List<String> number, Context context){
        this.number = number;
        this.context = context;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.volunteer_call, viewGroup, false);
        VolunteerAdapter.Holder holder = new VolunteerAdapter.Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int i) {
        holder.numberText.setText(number.get(i));
        holder.itemView.findViewById(R.id.callBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Volunteer").child("CallingList").child(number.get(i)).removeValue();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+number.get(i)));
                context.startActivity(intent);
                number.remove(i);
                notifyItemChanged(i);
                notifyItemRangeChanged(i, number.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return number.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView numberText;
        public Holder(@NonNull View itemView) {
            super(itemView);
            numberText = itemView.findViewById(R.id.numText);
        }
    }
}
