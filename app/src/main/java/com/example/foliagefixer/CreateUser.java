package com.example.foliagefixer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class CreateUser extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText username, password, email;
    private TextView create_user, title;
    // ...
    // Initialize Firebase Auth


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        mAuth = FirebaseAuth.getInstance();
        create_user = (Button) findViewById(R.id.createbtn);
        create_user.setOnClickListener(this);

        title = (TextView) findViewById(R.id.title);
        title.setOnClickListener(this);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.createbtn:
                createUser();
                finish();
                break;
        }
    }

    private void createUser() {

        String name = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String mail = email.getText().toString().trim();

        if (name.isEmpty()){
            username.setError("Username is required!");
            username.requestFocus();
            return;
        }
        if (pass.isEmpty()){
            password.setError("Password is required!");
            password.requestFocus();
            return;
        }
        if (mail.isEmpty()){
            email.setError("mail is required!");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            email.setError("Valid email is required!");
            email.requestFocus();
            return;
        }
        if (pass.length() < 6){
            password.setError("Create Longer Password");
            password.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(name, mail);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                Toast.makeText(CreateUser.this, "User has been created", Toast.LENGTH_LONG).show();
                                            }
                                            else{
                                                Toast.makeText(CreateUser.this, "User failed to register", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(CreateUser.this, "User failed to register", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


}