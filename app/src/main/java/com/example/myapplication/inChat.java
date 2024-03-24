package com.example.myapplication;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
public class inChat extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    TextView noExpense1;
    ImageView noExpense;
    TextView noExpense2;
    Button newExpense;
    ListView lv_desc;
    ListView lv_amt;
    ArrayList<String> allAmounts=new ArrayList<>();
    ArrayList<Expense> AllData=new ArrayList<Expense>();
    ArrayList<String> allDescriptions=new ArrayList<>();
    ExpenseAdapter<Expense> arrayAdapter;
    ArrayAdapter<String> aa_amt;
    public float billOnIdInChat;
    public double stringBillOnIdInChat;
    String idInChat;
    String name;

    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private String currentuser=FirebaseAuth.getInstance().getUid();
    private ArrayList<Float> CUBlist=new ArrayList<>();
    private ArrayList<Float> OCUBlist=new ArrayList<>();
    private ArrayList<String> list=new ArrayList<>();
    DocumentReference documentReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_chat);
        lv_desc = findViewById(R.id.lv_desc);
        name = getIntent().getStringExtra("name in chat");
        idInChat = getIntent().getStringExtra("idInChat");
        TextView nameInChat=findViewById(R.id.name_inChat);
        nameInChat.setText(name);
        noExpense = findViewById(R.id.noExpense);
        noExpense1 = findViewById(R.id.noExpense1);
        noExpense2 = findViewById(R.id.noExpense2);
        newExpense = findViewById(R.id.newExpense);

        lv_desc.setVisibility(View.VISIBLE);

        documentReference=firebaseFirestore.collection("finalSetteledAmount").document(currentuser).collection(idInChat).document(idInChat);
        firebaseFirestore.collection("finalSetteledAmount").document(currentuser).collection(idInChat).document(idInChat);
        Map<String, Double> finalSetteledAmount=new HashMap<>();
        finalSetteledAmount.put("diplayAmount", stringBillOnIdInChat);
        documentReference.set(finalSetteledAmount);

        billOnIdInChat=0;
        AllData.clear();
        retriveRecords();
        updateUI();


        arrayAdapter=new ExpenseAdapter<>(inChat.this,R.layout.item_expense, AllData);
        lv_desc.setAdapter(arrayAdapter);

        if(allAmounts!=null){
            noExpense.setVisibility(View.INVISIBLE);
            noExpense1.setVisibility(View.INVISIBLE);
            noExpense2.setVisibility(View.INVISIBLE);
        }

        newExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noExpense.setVisibility(View.INVISIBLE);
                noExpense1.setVisibility(View.INVISIBLE);
                noExpense2.setVisibility(View.INVISIBLE);
                Intent addExpense=new Intent(inChat.this, com.example.myapplication.addExpense.class);
                addExpense.putExtra("id",name);
                startActivityForResult(addExpense,REQUEST_CODE);
            }
        });


        lv_desc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showPopup(position);
            }
        });

        lv_desc.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                View popupView= LayoutInflater.from(inChat.this).inflate(R.layout.expensedeletepopup,null);
                PopupWindow popupWindow=new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
                popupWindow.showAtLocation(popupView,Gravity.CENTER,0,0);

                TextView que =popupView.findViewById(R.id.expenseque);
                TextView nameInDelete=popupView.findViewById(R.id.expenseNameInDelete);
                Button cancel=popupView.findViewById(R.id.expenseCancel);
                Button delete=popupView.findViewById(R.id.expenseRemove);

                String idAtPos = null;
                idAtPos=AllData.get(position).getId();
                String finalIdAtPos = idAtPos;
                String name1=AllData.get(position).getDescription();

                nameInDelete.setText(name1);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseFirestore.collection("records").document(finalIdAtPos).delete();
                        popupWindow.dismiss();
                        AllData.clear();
                        retriveRecords();
                        updateUI();

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                return true;
            }
        });

    }
    private void showPopup(int position) {
        // Inflate the pop-up layout
        View popupView = LayoutInflater.from(this).inflate(R.layout.in_expense, null);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);


        TextView nameInExpense=popupView.findViewById(R.id.nameInExpense);
        TextView amountInExpense=popupView.findViewById(R.id.amountInExpense);
        TextView paidBy=popupView.findViewById(R.id.paidBy);
        TextView rs=popupView.findViewById(R.id.rs);
        TextView billOnYou=popupView.findViewById(R.id.billOnYou);
        TextView billOnName=popupView.findViewById(R.id.billOnName);
        TextView nameInExpense2=popupView.findViewById(R.id.nameInExpense2);
        TextView amtOnName=popupView.findViewById(R.id.amtOnName);
        TextView amtOnYou=popupView.findViewById(R.id.amtOnYou);
        TextView delete=popupView.findViewById(R.id.delete);
        View view3=popupView.findViewById(R.id.view3);
        View view4=popupView.findViewById(R.id.view4);
        TextView splits=popupView.findViewById(R.id.splits);


        String id = null;
        id=AllData.get(position).getId();
        String billOnNonPayer;
        billOnNonPayer=AllData.get(position).getbillOnNonPayer();
        String amount = AllData.get(position).getAmount();


        nameInExpense.setVisibility(View.INVISIBLE);
        amountInExpense.setVisibility(View.INVISIBLE);
        nameInExpense2.setVisibility(View.INVISIBLE);
        paidBy.setVisibility(View.INVISIBLE);
        rs.setVisibility(View.INVISIBLE);
        billOnYou.setVisibility(View.INVISIBLE);
        billOnName.setVisibility(View.INVISIBLE);
        amtOnName.setVisibility(View.INVISIBLE);
        amtOnYou.setVisibility(View.INVISIBLE);
        delete.setVisibility(View.INVISIBLE);
        view3.setVisibility(View.INVISIBLE);
        view4.setVisibility(View.INVISIBLE);
        splits.setVisibility(View.INVISIBLE);

        String finalId = id;
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("records").document(finalId).delete();
                popupWindow.dismiss();
                AllData.clear();
                retriveRecords();
                updateUI();
            }
        });
        firebaseFirestore.collection("records").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot=task.getResult();
                            String idnew = documentSnapshot.get("Payer's id").toString();
                            String billOnPayer=documentSnapshot.get("Bill on Payer").toString();
                            String billOnNonPayer=documentSnapshot.get("Bill on Non-Payer").toString();
                            Toast.makeText(inChat.this, "bill on payer" +billOnPayer, Toast.LENGTH_SHORT).show();
                            Toast.makeText(inChat.this, "bill on non payer" +billOnNonPayer, Toast.LENGTH_SHORT).show();

                            firebaseFirestore.collection("users").document(idnew).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                DocumentSnapshot documentSnapshot1= task.getResult();
                                                String name1;
                                                name1=documentSnapshot1.get("Full Name").toString();
                                                if(idnew.equals(idInChat)){
                                                    amtOnName.setText(billOnPayer);
                                                    amtOnYou.setText(billOnNonPayer);
                                                    if(amtOnYou.getText()!="0"){
                                                        amtOnYou.setTextColor(Color.parseColor("#E18804"));
                                                    }
                                                }
                                                else{
                                                    amtOnName.setText(billOnNonPayer);
                                                    amtOnYou.setText(billOnPayer);
                                                    if(amtOnYou.getText()!="0"){
                                                        amtOnYou.setTextColor(Color.parseColor("#E18804"));
                                                    }
                                                }
                                                nameInExpense2.setText(name+":");
                                                nameInExpense.setText(name1);
                                                amountInExpense.setText(amount);
                                                billOnName.setVisibility(View.VISIBLE);
                                                nameInExpense.setVisibility(View.VISIBLE);
                                                nameInExpense2.setVisibility(View.VISIBLE);
                                                paidBy.setVisibility(View.VISIBLE);
                                                amountInExpense.setVisibility(View.VISIBLE);
                                                rs.setVisibility(View.VISIBLE);
                                                billOnYou.setVisibility(View.VISIBLE);
                                                amtOnName.setVisibility(View.VISIBLE);
                                                amtOnYou.setVisibility(View.VISIBLE);
                                                delete.setVisibility(View.VISIBLE);
                                                view3.setVisibility(View.VISIBLE);
                                                view4.setVisibility(View.VISIBLE);
                                                splits.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                        }
                    }
                });
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }
    private void updateUI() {
        firebaseFirestore.collection("records").whereEqualTo("NonPayer's id", idInChat).
                whereEqualTo("Payer's id", currentuser)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Expense newExpense = new Expense(document.get("Description").toString(),
                                        document.get("Total Amount").toString(),
                                        document.get("Bill on Non-Payer").toString(),
                                        "You lent",
                                        document.getId());

                                AllData.add(newExpense);
                                Collections.sort(AllData, new Comparator<Expense>() {
                                    @Override
                                    public int compare(Expense o1, Expense o2) {
                                        return o1.getDescription().compareToIgnoreCase(o2.getDescription());
                                    }
                                });
                            }
                        }
                    }
                });
        firebaseFirestore.collection("records").whereEqualTo("NonPayer's id", currentuser).
                whereEqualTo("Payer's id", idInChat)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Expense newExpense = new Expense(document.get("Description").toString(),
                                        document.get("Total Amount").toString(),
                                        document.get("Bill on Non-Payer").toString(),
                                        "You borrowed", document.getId());
                                        AllData.add(newExpense);
                                Collections.sort(AllData, new Comparator<Expense>() {
                                    @Override
                                    public int compare(Expense o1, Expense o2) {
                                        return o1.getDescription().compareToIgnoreCase(o2.getDescription());
                                    }
                                });
                            }
                           arrayAdapter.notifyDataSetChanged();
                            if(AllData.isEmpty()|AllData==null){
                                noExpense.setVisibility(View.VISIBLE);
                                noExpense1.setVisibility(View.VISIBLE);
                                noExpense2.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }
    private void retriveRecords() {
        firebaseFirestore.collection("records").whereEqualTo("NonPayer's id", idInChat).
                whereEqualTo("Payer's id", currentuser)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            billOnIdInChat=0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String P = document.get("Bill on Non-Payer").toString();
                                Float expense=Float.parseFloat(P);
                                billOnIdInChat=billOnIdInChat+expense;
                            }
                        }
                    }
                });
        firebaseFirestore.collection("records").whereEqualTo("NonPayer's id", currentuser).
                whereEqualTo("Payer's id", idInChat)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String B = document.get("Bill on Non-Payer").toString();
                                Float expense=Float.parseFloat(B);
                                billOnIdInChat=billOnIdInChat-expense;
                            }
                            stringBillOnIdInChat= Double.valueOf(billOnIdInChat);
                            firebaseFirestore.collection("finalSetteledAmount").document(currentuser).collection(idInChat).document(idInChat).update("diplayAmount",stringBillOnIdInChat);
                        }
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            float inChatAmount=getIntent().getFloatExtra("inChatAmount",0);
            billOnIdInChat=0;
            AllData.clear();
            retriveRecords();
            updateUI();
        }
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent home = new Intent(inChat.this, MainActivity.class);
        startActivity(home);
        overridePendingTransition(R.anim.slide_out_left,
                R.anim.side_in_right);
        finish();
    }
}