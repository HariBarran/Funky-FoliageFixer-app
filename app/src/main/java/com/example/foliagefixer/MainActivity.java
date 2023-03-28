package com.example.foliagefixer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private TextView create;
    private EditText email, pass;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(this, Home.class));
//            finish();
        }
        email = (EditText) findViewById(R.id.mail);
        pass = (EditText) findViewById(R.id.password);

        login = (MaterialButton) findViewById(R.id.loginbtn);
        login.setOnClickListener(this);
        create = (TextView) findViewById(R.id.createaccount);
        create.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.createaccount:
                startActivity(new Intent(this, CreateUser.class));
                break;
            case R.id.loginbtn:
                userLogin();
                startActivity(new Intent(this, Home.class));
                break;

        }
    }

    private void userLogin() {
        String mail = email.getText().toString().trim();
        String password = pass.getText().toString().trim();

        if(mail.isEmpty()){
            email.setError("Email is required!");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            email.setError("Valid email is required!");
            email.requestFocus();
            return;
        }
        if (password.isEmpty()){
            pass.setError("Password is required!");
            pass.requestFocus();
            return;
        }
        if (password.length() < 6){
            pass.setError("Password too short");
            pass.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this, Home.class));
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "Failed to Login", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}