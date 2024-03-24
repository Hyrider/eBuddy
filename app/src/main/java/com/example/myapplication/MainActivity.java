package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class MainActivity extends AppCompatActivity {
    private static final int PICK_CONTACT_REQUEST = 1;
    ArrayList<String> NPlist = new ArrayList<>();

    public ArrayList<NameModel> NAMES = new ArrayList<NameModel>();
    public ArrayList<String> IDs = new ArrayList<>();
    public  ArrayList<String> idList1 = new ArrayList<>();
    NameAdapter<NameModel> nameAdapter;
    ListView listView;
    ImageView noFriends;
    FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    String currentuser = FirebaseAuth.getInstance().getUid();
    boolean userFound=false;
    String no;
    String idInChat = null;
    String name = null;
    TextView addNewFriends;
    TextView addNewFriends2;

    private double stringBillOnIdInChat=0.0;
    private ArrayList<String> idList;
    private ArrayList<String> billList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.listview);
        toolbar.setTitle("");
        noFriends = findViewById(R.id.noFriends);
        addNewFriends = findViewById(R.id.addNewFriends);
        addNewFriends2 = findViewById(R.id.addNewFriends2);
        addNewFriends2.setVisibility(View.INVISIBLE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        updateData();

        nameAdapter=new NameAdapter<>(this,R.layout.item_contact,NAMES);
        listView.setAdapter(nameAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < NAMES.size()) {
                    String passPosition= String.valueOf(position);
                    NameModel clickedItem = NAMES.get(position);
                    String clickedName = clickedItem.getName();
                    idInChat=clickedItem.getIdInChat();
                    Intent inChat = new Intent(MainActivity.this, inChat.class);
                    inChat.putExtra("name in chat", clickedName);
                    inChat.putExtra("id",passPosition);
                    inChat.putExtra("idInChat",idInChat);
                    startActivity(inChat);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Invalid item clicked", Toast.LENGTH_SHORT).show();
                }

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                View popupView= LayoutInflater.from(MainActivity.this).inflate(R.layout.deletepopup,null);
                PopupWindow popupWindow=new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);

                TextView que =popupView.findViewById(R.id.que);
                TextView nameInDelete=popupView.findViewById(R.id.nameInDelete);
                Button cancel=popupView.findViewById(R.id.cancel);
                Button remove=popupView.findViewById(R.id.remove);

                String name1 = NAMES.get(position).getName();
                nameInDelete.setText(name1);
                String id1 = NAMES.get(position).getIdInChat();
                Toast.makeText(MainActivity.this, ""+id1, Toast.LENGTH_SHORT).show();
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                Toast.makeText(MainActivity.this, ""+id1, Toast.LENGTH_SHORT).show();


                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        idList1.remove(id1);
                        documentReference=firestore.collection("users").document(currentuser);
                        documentReference.update("usersWithTransaction",idList1);
                        Toast.makeText(MainActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                        NAMES.clear();
                        updateData();
                        popupWindow.dismiss();
                    }
                });
                return true;
            }
        });

        addNewFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contacts = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contacts, PICK_CONTACT_REQUEST);
            }
        });
        addNewFriends2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contacts = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contacts, PICK_CONTACT_REQUEST);
            }
        });
    }


    private void updateData() {
        firestore.collection("users").document(currentuser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                final Toast toast = Toast.makeText(MainActivity.this, "Loading...", Toast.LENGTH_SHORT);
                toast.show();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 1000);
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        idList1 = (ArrayList<String>) document.get("usersWithTransaction");
                        if(idList1==null||idList1.isEmpty()){
                            listView.setVisibility(View.INVISIBLE);
                            noFriends.setVisibility(View.VISIBLE);
                            addNewFriends.setVisibility(View.VISIBLE);
                            addNewFriends2.setVisibility(View.INVISIBLE);
                        }
                        if (idList1 != null) {
                            if (!idList1.isEmpty()) {

                                for (int i = 0; i < idList1.size(); i++) {
                                    int finalI = i;
                                    firestore.collection("finalSetteledAmount").document(currentuser).collection(idList1.get(i)).document(idList1.get(i)).
                                            get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    String billOnIdInChat = null;
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            billOnIdInChat = document.get("diplayAmount").toString();
//                                                            Toast.makeText(MainActivity.this, "" + billOnIdInChat, Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(MainActivity.this, "no", Toast.LENGTH_SHORT).show();
                                                        }

                                                        String finalBillOnIdInChat = billOnIdInChat;
                                                        firestore.collection("users").document(idList1.get(finalI)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    DocumentSnapshot document = task.getResult();
                                                                    NameModel newName = null;
                                                                    if (document.exists()) {
                                                                        String name = document.getString("Full Name");
                                                                        idInChat = document.getId();
                                                                        Float floatFinalBillOnIdInChat = Float.parseFloat(finalBillOnIdInChat);
                                                                        String text="You lent";
                                                                        if(floatFinalBillOnIdInChat<0){
                                                                            floatFinalBillOnIdInChat=floatFinalBillOnIdInChat-(2*floatFinalBillOnIdInChat);
                                                                            text="You borrowed";
                                                                        }
                                                                        newName = new NameModel(name,floatFinalBillOnIdInChat,idInChat , text);
                                                                        NAMES.add(newName);
                                                                        Collections.sort(NAMES, new Comparator<NameModel>() {
                                                                            @Override
                                                                            public int compare(NameModel nameModel1, NameModel nameModel2) {
                                                                                return nameModel1.getName().compareToIgnoreCase(nameModel2.getName());
                                                                            }
                                                                        });
                                                                    }
//
                                                                    nameAdapter.notifyDataSetChanged();
                                                                    runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            if (!NAMES.isEmpty()) {
                                                                                noFriends.setVisibility(View.INVISIBLE);
                                                                                addNewFriends.setVisibility(View.INVISIBLE);
                                                                                addNewFriends2.setVisibility(View.VISIBLE);
                                                                                listView.setVisibility(View.VISIBLE);
                                                                            }
                                                                        }
                                                                    });
                                                                } else {
                                                                    Toast.makeText(MainActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_person) {
            Intent contacts = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(contacts, PICK_CONTACT_REQUEST);
        } else if (item.getItemId() == R.id.profile) {
            Intent profile=new Intent(this,profile.class);
            startActivity(profile);
        } else{
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Cursor cursor = null;
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            assert uri != null;
            cursor = getContentResolver().query(uri, null, null, null, null);
            if  (cursor != null && cursor.moveToFirst()){
                no = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
                firestore.collection("users").whereEqualTo("Mobile", no).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                     userFound = false;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        userFound = true;
                                        idInChat = document.getId();
                                        name = document.getString("Full Name");
                                        try {
                                            if(idList1==null){
                                                idList1=new ArrayList<>();
                                            }
                                        if (idList1.contains(idInChat)) {
                                            Toast.makeText(MainActivity.this, "Person Already Exists", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (!currentuser.equals(idInChat)) {
                                                    idList1.add(idInChat);
                                                    documentReference = firestore.collection("finalSetteledAmount").document(currentuser).collection(idInChat).document(idInChat);
                                                    firestore.collection("finalSetteledAmount").document(currentuser).collection(idInChat).document(idInChat);
                                                    Map<String, Double> finalSetteledAmount = new HashMap<>();
                                                    finalSetteledAmount.put("diplayAmount", stringBillOnIdInChat);
                                                    documentReference.set(finalSetteledAmount);

                                            } else {
                                                Toast.makeText(MainActivity.this, "You are trying to add yourself", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        }catch (Exception e){
                                            Log.d("TAG", "onComplete: "+e);
                                        }
                                        documentReference = firestore.collection("users").document(currentuser);
                                        documentReference.update("usersWithTransaction", idList1);
                                        NAMES.clear();
                                        updateData();
                                    }
                                    if (!userFound) {
                                        Toast.makeText(MainActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        }
        cursor.close();
    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}