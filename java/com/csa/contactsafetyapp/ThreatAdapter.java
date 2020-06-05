package com.csa.contactsafetyapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ThreatAdapter extends RecyclerView.Adapter<ThreatAdapter.Holder> {
    List<String> name;
    List<String> number;
    Context context;
    ThreatAdapter(List<String> name, List<String> number, Context context){
        this.name = name;
        this.number = number;
        this.context = context;
    }
    @NonNull
    @Override
    public ThreatAdapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.new_layout, viewGroup, false);
        ThreatAdapter.Holder holder = new ThreatAdapter.Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ThreatAdapter.Holder holder, int i) {
        holder.name.setText(name.get(i));
        holder.number.setText(number.get(i));
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView number;
        TextView name;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);
        }
    }
}
