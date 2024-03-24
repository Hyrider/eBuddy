package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class NameAdapter<N> extends ArrayAdapter<NameModel> {
    Context context;
    List<NameModel> list;

    public NameAdapter(@NonNull Context context, int resource, @NonNull List<NameModel> listOfAllNames) {
        super(context, resource, listOfAllNames);
        this.context = context;
        this.list = listOfAllNames;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        NameModel name=list.get(position);
        convertView= LayoutInflater.from(getContext()).inflate(R.layout.item_contact,parent,false);

        TextView nameitem=convertView.findViewById(R.id.tv_name);
        TextView sum=convertView.findViewById(R.id.sum);
        TextView textToShow=convertView.findViewById(R.id.textToShow);

        nameitem.setText(name.getName());

        sum.setText(name.getTotal_sum().toString());

        if(name.gettextToShow()=="You borrowed"){
            textToShow.setTextColor(Color.parseColor("#E18804"));
        }
        textToShow.setText(name.gettextToShow());

        return convertView;
    }
}
