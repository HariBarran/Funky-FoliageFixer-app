package com.example.foliagefixer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateUser extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private static final String TAG = "CreateUser";
    private static final String FLASK_SERVER_URL = "https://foliagefixerbackend.herokuapp.com/adduser";

    private EditText username, password, email;
    private TextView create_user, title;

    // ...
    // Initialize Firebase Auth
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), Home.class);
            startActivity(intent);

        }
    }

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

        mFirebaseDatabase = FirebaseDatabase.getInstance("https://foliagefixer-default-rtdb.firebaseio.com/");
        mDatabaseReference = mFirebaseDatabase.getReference();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.createbtn:
                createUser();
//                sendFirebaseUidToFlaskServer();
                break;
        }
    }

    private void createUser() {

        String name = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String mail = email.getText().toString().trim();

        if (name.isEmpty()) {
            username.setError("Username is required!");
            username.requestFocus();
            return;
        }
        if (pass.isEmpty()) {
            password.setError("Password is required!");
            password.requestFocus();
            return;
        }
        if (mail.isEmpty()) {
            email.setError("mail is required!");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            email.setError("Valid email is required!");
            email.requestFocus();
            return;
        }
        if (pass.length() < 6) {
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

                                            if (task.isSuccessful()) {
                                                Toast.makeText(CreateUser.this, "User has been created", Toast.LENGTH_LONG).show();

                                                FirebaseAuth.getInstance().getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                        if (task.isSuccessful()) {
                                                            String idToken = task.getResult().getToken();
                                                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                            sendUserIdToServer(userId, idToken);
                                                        }
                                                    }
                                                });

                                                finish();
                                            } else {
                                                Toast.makeText(CreateUser.this, "User failed to register", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(CreateUser.this, "User failed to register", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }
    private void sendUserIdToServer (String userId, String idToken){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("user_id", userId)
                .build();

        Request request = new Request.Builder()
                .url("https://foliagefixerbackend.herokuapp.com/adduser")
                .addHeader("Authorization",  idToken)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle the error
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CreateUser.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Handle the response
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CreateUser.this, "User ID sent to the server", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CreateUser.this, "Error: " + response.code() + ", " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}