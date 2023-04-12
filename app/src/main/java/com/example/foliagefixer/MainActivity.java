package com.example.foliagefixer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private TextView create;
    private EditText email, pass;
    private Button login;

    private static final String FLASK_SERVER_URL = "https://foliagefixerbackend.herokuapp.com/adduser";
    private static final String TAG = "MainActivity";

//    @Override
//    public void onStart(){
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null){
//            Intent intent = new Intent(getApplicationContext(), Home.class);
//            startActivity(intent);
//            finish();
//        }
//    }

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



        final EditText passwordEditText = findViewById(R.id.password);
        passwordEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (passwordEditText.getTransformationMethod() instanceof PasswordTransformationMethod) {
                            passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.visibility_white_24dp), null);
                        } else {
                            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.visibility_off_white_24dp), null);
                        }
                        return true;
                    }
                }
                return false;
            }
        });

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
                sendFirebaseUidToFlaskServer();
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
//                    sendIdTokenToFlaskBackend();
                    startActivity(new Intent(getApplicationContext(), Home.class));
                }
                else{
                    Toast.makeText(MainActivity.this, "Failed to Login", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void sendIdTokenToFlaskBackend() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()) {
                        String idToken = task.getResult().getToken();
                        OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder()
                                .url("https://your_flask_app_url/protected")
                                .header("Authorization", idToken)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    Log.d("ID_TOKEN", "ID token sent to Flask backend successfully");
                                } else {
                                    Log.e("ID_TOKEN", "Failed to send ID token to Flask backend. Error: " + response.code() + ", " + response.message());
                                }
                            }
                        });
                    } else {
                        Log.e("ID_TOKEN", "Failed to get ID token", task.getException());
                    }
                }
            });
        } else {
            Log.e("ID_TOKEN", "No user is signed in");
        }
    }



    private void sendFirebaseUidToFlaskServer() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()) {
                        String idToken = task.getResult().getToken();
                        String uid = user.getUid();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    OkHttpClient client = new OkHttpClient();
                                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("uid", uid);
                                    RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

                                    Request request = new Request.Builder()
                                            .url(FLASK_SERVER_URL)
                                            .addHeader("authorization",  idToken)
                                            .post(requestBody)
                                            .build();

                                    Response response = client.newCall(request).execute();

                                    if (response.isSuccessful()) {
                                        Log.d(TAG, "Response: " + response.body().string());
                                    } else {
                                        Log.e(TAG, "Request failed: " + response.message());
                                    }
                                } catch (JSONException | IOException e) {
                                    Log.e(TAG, "Error sending Firebase UID to Flask server", e);
                                }
                            }
                        }).start();
                    } else {
                        Log.e(TAG, "Failed to get ID token: " + task.getException().getMessage());
                    }
                }
            });
        } else {
            Log.e(TAG, "User not signed in");
        }
    }

}