package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class login extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore store;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView welcome_back=findViewById(R.id.welcome_back);
        TextView msg=findViewById(R.id.msg);
        EditText login_email=findViewById(R.id.login_IPemail);
        EditText login_pass=findViewById(R.id.login_IPpass);
        TextView tx=findViewById(R.id.signuptxt);
        Button login=findViewById(R.id.login);

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Intent home=new Intent(this,MainActivity.class);
            startActivity(home);
            finish();
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String email = login_email.getText().toString();
                String password = login_pass.getText().toString();
                mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    try{
                                    if (task.isSuccessful()) {
                                        if (currentUser.isEmailVerified()) {
                                            Toast.makeText(login.this, "Verified", Toast.LENGTH_SHORT).show();
                                            SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putBoolean("isLoggedIn", true);
                                            editor.apply();
                                            Intent home = new Intent(login.this, MainActivity.class);
                                            startActivity(home);
                                            finish();
                                        } else {
                                            Toast.makeText(login.this, "not verified", Toast.LENGTH_SHORT).show();
                                            store.collection("users").document(currentUser.getUid()).delete();

                                        }
                                    }
                                    }catch (Exception e){
                                        Toast.makeText(login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }


//
        });
        SpannableString spannableString=new SpannableString("Sign up");
        ClickableSpan signup=new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent signup1 = new Intent(login.this, signup.class);
                startActivity(signup1);
                finish();
            }
        };
        spannableString.setSpan(signup,0,7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tx.setText(spannableString);
        tx.setMovementMethod(LinkMovementMethod.getInstance());

    }
}