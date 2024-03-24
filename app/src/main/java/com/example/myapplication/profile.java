package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class profile extends AppCompatActivity {
TextView profileTitle;
    TextView nameInProfileTitle;
    TextView nameInProfile;
    TextView emailInProfileTitle;
    TextView emailInProfile;
    TextView numberInProfileTitle;
    TextView numberInProfile;
    Button logout;

    String currentUser=FirebaseAuth.getInstance().getUid();
    FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        nameInProfileTitle =findViewById(R.id.nameInProfileTitle);
        nameInProfile =findViewById(R.id.nameInProfile);
        emailInProfileTitle=findViewById(R.id.emailInProfileTitle);
        emailInProfile =findViewById(R.id.emailInProfile);
        numberInProfileTitle=findViewById(R.id.numberInProfileTitle);
        numberInProfile=findViewById(R.id.numberInProfile);
        logout=findViewById(R.id.logoutInProfile);

        firestore.collection("users").document(currentUser).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                String nameToShow = null;
                                String numberToShow = null;
                                String emailToShow = null;
                                if(task.isSuccessful()){
                                    DocumentSnapshot documentSnapshot=task.getResult();
                                    nameToShow = documentSnapshot.get("Full Name").toString();
                                    numberToShow = documentSnapshot.get("Mobile").toString();
                                    emailToShow = documentSnapshot.get("Email ID").toString();

                                }
                                nameInProfile.setText(nameToShow);
                                numberInProfile.setText(numberToShow);
                                emailInProfile.setText(emailToShow);
                            }
                        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences=getSharedPreferences("MyPrefs",MODE_PRIVATE);
                SharedPreferences.Editor   editor=preferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();
                Intent home = new Intent(profile.this, login.class);
                startActivity(home);
            }
        });



    }
}