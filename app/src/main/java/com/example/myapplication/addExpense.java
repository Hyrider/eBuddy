package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class addExpense extends AppCompatActivity {
    EditText IPamount;
    EditText IPdescription;
    Float amount;
    String stringAmount;
    int amt;
    String description;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String currentuser = FirebaseAuth.getInstance().getUid();
    private String idP;
    private String idNP;
    private String pos;
    private int position;
    ConstraintLayout layout;
    private String name;
    TextView name_add_expense;
    TextView you_n_equal;
    TextView you_owed_full_payment;
    TextView he_owed_full_payment;
    TextView he_n_equal;
    public static final ArrayList<Expense> expense = new ArrayList<>();
    TextView split_options;
     float amtPerHead;
    private String payer_name;
    private String splitEqual;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        IPamount = findViewById(R.id.amount);
        IPdescription = findViewById(R.id.description);
        layout = findViewById(R.id.addExpense);
        name_add_expense = findViewById(R.id.name_add_expense);
        Toolbar toolbar_add_expense = findViewById(R.id.toolbar_add_expense);
        setSupportActionBar(toolbar_add_expense);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        idP = currentuser;
        name = getIntent().getStringExtra("id");
        name_add_expense.setText(name);
        firestore.collection("users").whereEqualTo("Full Name", name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                idNP = document.getId();

                            }
                        }
                    }
                });

        split_options = findViewById(R.id.split_options);
        split_options.setText("YOU PAID AND SPLIT EQUALLY");
        split_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                boolean focusable = true;
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popUpView = inflater.inflate(R.layout.split_options, null);
                PopupWindow popupWindow = new PopupWindow(popUpView, width, height, focusable);

                you_n_equal = popUpView.findViewById(R.id.you_n_equal);

                you_owed_full_payment = popUpView.findViewById(R.id.you_owed_full_payment);

                he_n_equal = popUpView.findViewById(R.id.he_n_equal);

                he_owed_full_payment = popUpView.findViewById(R.id.he_owed_full_payment);

                you_n_equal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        split_options.setText("YOU PAID AND SPLIT EQUALLY");
                        split_options.setTextSize(18);
                        idP=idP;
                        idNP=idNP;

                    }
                });
                you_owed_full_payment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        popupWindow.dismiss();
                        split_options.setText("You Owed the Full Payment");
                        split_options.setTextSize(18);
                        idP=idP;
                        idNP=idNP;
                    }
                });
                he_n_equal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        popupWindow.dismiss();
                        splitEqual=name + "Paid and Split Equally";
                        split_options.setText(splitEqual);
                        split_options.setTextSize(18);
                        String temp = idP;
                        idP=idNP;
                        idNP=temp;
                    }
                });
                he_owed_full_payment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        popupWindow.dismiss();
                        payer_name=name + "Owed the Full Payment";
                        split_options.setText(payer_name);
                        split_options.setTextSize(18);
                        String temp = idP;
                        idP=idNP;
                        idNP=temp;
                    }
                });

                layout.post(new Runnable() {
                    @Override
                    public void run() {
                        popupWindow.showAsDropDown(layout,Gravity.BOTTOM, 0, 0);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_expense, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        stringAmount = IPamount.getText().toString();
        amount=Float.parseFloat(stringAmount);
        description = IPdescription.getText().toString();
        if (item.getItemId() == R.id.done) {
            if (TextUtils.isEmpty(stringAmount)) {
                Toast.makeText(this, "Enter Amount", Toast.LENGTH_SHORT).show();
                IPamount.requestFocus();
                IPamount.setError("Enter Amount");
            } else if (TextUtils.isEmpty(description)) {
                Toast.makeText(this, "Add Description", Toast.LENGTH_SHORT).show();
                IPdescription.requestFocus();
                IPdescription.setError("Add Description");
            }

            if (!TextUtils.isEmpty(stringAmount) && !TextUtils.isEmpty(description)) {
                if (split_options.getText() == "YOU PAID AND SPLIT EQUALLY") {
                    DocumentReference documentReference=firestore.collection("records").document();
                    Map<String,String> newRecord=new HashMap<>();
                    amtPerHead=amount/2;
                    newRecord.put("Payer's id",idP);
                    newRecord.put("NonPayer's id",idNP);
                    newRecord.put("Total Amount",String.valueOf(amount));
                    newRecord.put("Bill on Payer", String.valueOf(amtPerHead));
                    newRecord.put("Bill on Non-Payer", String.valueOf(amtPerHead));
                    newRecord.put("Description",description);
                    documentReference.set(newRecord);
                    Intent back = new Intent();
                    back.putExtra("inChatAmount",amtPerHead);
                    back.putExtra("Payer's id",idP);
                    setResult(RESULT_OK, back);
                    finish();
                } else if (split_options.getText()=="You Owed the Full Payment") {
                    DocumentReference documentReference=firestore.collection("records").document();
                    Map<String,String> newRecord=new HashMap<>();
                    newRecord.put("Payer's id",idP);
                    newRecord.put("NonPayer's id",idNP);
                    newRecord.put("Total Amount",String.valueOf(amount));
                    newRecord.put("Bill on Payer", "0");
                    newRecord.put("Bill on Non-Payer",String.valueOf(amount));
                    newRecord.put("Description",description);
                    documentReference.set(newRecord);
                    Intent back = new Intent();
                    back.putExtra("inChatAmount",amount);
                    back.putExtra("Payer's id",idP);
                    setResult(RESULT_OK, back);
                    finish();
                } else if (split_options.getText()==splitEqual) {
                    DocumentReference documentReference=firestore.collection("records").document();
                    Map<String,String> newRecord=new HashMap<>();
                    amtPerHead=amount/2;
                    Toast.makeText(this, "."+amt, Toast.LENGTH_SHORT).show();
                    newRecord.put("Payer's id",idP);
                    newRecord.put("NonPayer's id",idNP);
                    newRecord.put("Total Amount",String.valueOf(amount));
                    newRecord.put("Bill on Payer", String.valueOf(amtPerHead));
                    newRecord.put("Bill on Non-Payer", String.valueOf(amtPerHead));
                    newRecord.put("Description",description);
                    documentReference.set(newRecord);
                    Intent back = new Intent();
                    back.putExtra("inChatAmount",amtPerHead);
                    back.putExtra("Payer's id",idP);
                    setResult(RESULT_OK, back);
                    finish();
                }
                else if(split_options.getText()==payer_name){
                    DocumentReference documentReference=firestore.collection("records").document();
                    Map<String,String> newRecord=new HashMap<>();
                    newRecord.put("Payer's id",idP);
                    newRecord.put("NonPayer's id",idNP);
                    newRecord.put("Total Amount",String.valueOf(amount));
                    newRecord.put("Bill on Payer", "0");
                    newRecord.put("Bill on Non-Payer",String.valueOf(amount));
                    newRecord.put("Description",description);
                    documentReference.set(newRecord);
                    Intent back = new Intent();
                    back.putExtra("inChatAmount",amount);
                    back.putExtra("Payer's id",idP);
                    setResult(RESULT_OK, back);
                    finish();
                }
            }
        }
        else {
            onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent inChat=new Intent(this, inChat.class);
        startActivity(inChat);
        overridePendingTransition(R.anim.slide_out_right,
                R.anim.slide_in_left);
    }
}
