package com.example.foliagefixer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Recent extends AppCompatActivity{

    private RecyclerView recyclerView;
    private RecentAdapter recentAdapter;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        userId = GlobalVariables.user_id; // Replace this with the actual user ID

        recyclerView = findViewById(R.id.recent_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recentAdapter = new RecentAdapter(this, new ArrayList<Scan>());
        recyclerView.setAdapter(recentAdapter);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        TextView logoutTextView = findViewById(R.id.logoutTextView);
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the user from Firebase Auth
                mAuth.signOut();

                // Navigate back to the login screen or any other appropriate activity
                Intent intent = new Intent(Recent.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d("Firebase UID", "User ID: " + userId);
        } else {
            Log.d("Firebase UID", "User not signed in.");
        }

        TextView appTitle = findViewById(R.id.Title1);
        appTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Recent.this, Home.class);
                startActivity(intent);
            }
        });

        loadRecentScans(recentAdapter);
    }



    private void loadRecentScans(final RecentAdapter recentAdapter) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()) {
                        String idToken = task.getResult().getToken();


                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();

                        Request request = new Request.Builder()
                                .url("https://foliagefixerbackend.herokuapp.com/recent?user_id=" + userId)
                                .header("authorization",  idToken)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                            }
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    String jsonResponse = response.body().string();
                                    Gson gson = new Gson();
                                    Type scanListType = new TypeToken<List<Scan>>(){}.getType();
                                    List<Scan> scans = gson.fromJson(jsonResponse, scanListType);

                                    // Get the 10 most recent responses
                                    int listSize = scans.size();
                                    int start = Math.max(listSize - 10, 0);
                                    List<Scan> lastTenScans = scans.subList(start, listSize);

                                    // Reverse the list to show the most recent scans first
                                    Collections.reverse(lastTenScans);

                                    final List<Scan> finalLastTenScans = lastTenScans;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            recentAdapter.setRecentScans(new ArrayList<>(finalLastTenScans));
                                        }
                                    });
                                } else {
                                    Log.e("loadRecentScans", "Error: " + response.code() + ", " + response.message());
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


}





