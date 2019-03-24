package com.avneetksethi.womentor;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.DatabaseMetaData;

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText emailET;
    private EditText passwordET;
    private EditText nameET;
    private RadioGroup mradioGroup;
    private TextView registerTV;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerTV = (TextView) findViewById(R.id.registertv);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Shink.ttf");
        registerTV.setTypeface(typeface);


        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //user logged in
                if(user != null) {
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };



        registerButton = (Button) findViewById(R.id.registerButton);
        emailET = (EditText) findViewById(R.id.email);
        passwordET = (EditText) findViewById(R.id.password);
        nameET = (EditText) findViewById(R.id.name);

        mradioGroup = (RadioGroup) findViewById(R.id.radioGroup);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedOptionID = mradioGroup.getCheckedRadioButtonId();

                final RadioButton radioButton = (RadioButton) findViewById(selectedOptionID);

                if(radioButton.getText() == null) {
                    return;
                }

                final String email = emailET.getText().toString();
                final String password = passwordET.getText().toString();
                final String name = nameET.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //if registration fails
                        if(!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration Error", Toast.LENGTH_SHORT).show();

                        }else {
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(radioButton.getText().toString()).child(userId).child("name");
                            currentUserDb.setValue(name);
                        }

                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
       super.onStart();
       mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
