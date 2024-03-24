package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ExpenseAdapter<E> extends ArrayAdapter<Expense> {
    private Context context;
    private List<Expense> expenseList;
    public ExpenseAdapter(@NonNull Context context, int resource, @NonNull List<Expense> expenseList) {
        super(context, resource, expenseList);
        this.context = context;
        this.expenseList = expenseList;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        }

        Expense expense = expenseList.get(position);

        TextView tvDescription = convertView.findViewById(R.id.item_description);
        TextView tvAmount = convertView.findViewById(R.id.total_spent);
        TextView status = convertView.findViewById(R.id.status);
        TextView billOnNonPayer = convertView.findViewById(R.id.amtperhead);

        String string=expense.getDescription();

        if(string.length()>25){
            string=string.trim().substring(0,20);
            tvDescription.setText(string+"...");
        }else{
            tvDescription.setText(expense.getDescription());
        }
        tvAmount.setText(expense.getAmount());
        if ("You borrowed".equals(expense.getStatus())) {
            status.setTextColor(Color.parseColor("#E18804"));
        } else {
            status.setTextColor(Color.parseColor("#DDFFFFFF"));
        }
        status.setText(expense.getStatus());
        billOnNonPayer.setText(expense.getbillOnNonPayer());
        return convertView;
    }
}
