package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthMultiFactorException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore store;
    String email, password,mob,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        TextView tagLine = findViewById(R.id.textView);
        EditText IPname = findViewById(R.id.IPname);
        EditText IPmobile = findViewById(R.id.IPmobile);
        EditText IPemail = findViewById(R.id.IPemail);
        EditText IPpass = findViewById(R.id.IPpass);
        Button SignUp = findViewById(R.id.signup);
        TextView titleName=findViewById(R.id.titleName);
        TextView titleMob=findViewById(R.id.titleMob);
        TextView titleEmail=findViewById(R.id.titleEmail);
        TextView titlePass=findViewById(R.id.titlePass);
        TextView login_here=findViewById(R.id.login_here);



        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth=FirebaseAuth.getInstance();
                store=FirebaseFirestore.getInstance();

                name=IPname.getText().toString();
                mob=IPmobile.getText().toString();
                email = IPemail.getText().toString();
                password = IPpass.getText().toString();
                if(mob.startsWith("+91")){
                    mob=mob;
                }
                else {
                    mob="+91"+mob;
                }

                if(TextUtils.isEmpty(name)){
                    Toast.makeText(signup.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                    IPname.setError("Name is required");
                    IPname.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(mob)) {
                    Toast.makeText(signup.this, "Please enter your Mobile No.", Toast.LENGTH_SHORT).show();
                    IPmobile.setError("Mob No. is required");
                    IPmobile.requestFocus();
                    return;
                }
                if(!TextUtils.isEmpty(email))
                {
                    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    {
                        Toast.makeText(signup.this, "Please Enter valid email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please enter email!!", Toast.LENGTH_LONG).show();
                    IPemail.setError("Email is required");
                    IPemail.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(),"Please enter password!!",Toast.LENGTH_LONG).show();
                    IPpass.requestFocus();
                    IPpass.setError("Password is required");
                    return;
                }
                else if(password.length()<8){
                    Toast.makeText(signup.this, "Password must be of 8 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser current_user = mAuth.getCurrentUser();
                                    current_user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(signup.this, "Verification Email Has Been Sent", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure" + e.getMessage());
                                        }
                                    });
                                    Toast.makeText(getApplicationContext(),
                                                    "Registration successful!",
                                                    Toast.LENGTH_LONG)
                                            .show();
                                    String userID=mAuth.getCurrentUser().getUid();
                                    DocumentReference doucumentRef = store.collection("users").document(userID);
                                    Map<String,Object> user = new HashMap<>();
                                    user.put("Full Name",name);
                                    user.put("Mobile",mob);
                                    user.put("Email ID",email);
                                    user.put("usersWithTransaction",null);
                                    doucumentRef.set(user);
                                    Intent login =new Intent(signup.this, com.example.myapplication.login.class);
                                    startActivity(login);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Registration failed!!" + " Please try again later",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        }) ;
        SpannableString spannableString= new SpannableString("Login here");
        ClickableSpan login= new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                    Intent login = new Intent(signup.this, com.example.myapplication.login.class);
                    startActivity(login);
                    finish();
            }
        };

        spannableString.setSpan(login,0,10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        login_here.setText(spannableString);
        login_here.setMovementMethod(LinkMovementMethod.getInstance());
    }
}