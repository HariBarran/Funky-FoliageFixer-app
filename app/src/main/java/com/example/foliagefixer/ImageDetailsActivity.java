package com.example.foliagefixer;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.google.firebase.auth.FirebaseAuth;

public class ImageDetailsActivity extends AppCompatActivity {

    private OkHttpClient httpClient;
    private Handler mainHandler;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private class SendImageToServerTask extends AsyncTask<Uri, Void, Void> {
        @Override
        protected Void doInBackground(Uri... imageUris) {
            Uri imageUri = imageUris[0];
            sendImageToServer(imageUri);
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);
        TextView classificationTextView = findViewById(R.id.classificationTextView);
        TextView severityTextView = findViewById(R.id.severityTextView);
        TextView solutionsTextView = findViewById(R.id.solutionsTextView);

        ImageView imageView = findViewById(R.id.detailsImageView);
        TextView textView = findViewById(R.id.detailsTextView);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        progressBar = findViewById(R.id.progressBar);
        classificationTextView.setText("Classification: Loading...");
        severityTextView.setText("Severity: Loading...");
        solutionsTextView.setText("Solutions: Loading...");

        TextView logoutTextView = findViewById(R.id.logoutTextView);
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the user from Firebase Auth
                mAuth.signOut();

                // Navigate back to the login screen or any other appropriate activity
                Intent intent = new Intent(ImageDetailsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        TextView appTitle = findViewById(R.id.Title1);
        appTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImageDetailsActivity.this, Home.class);
                startActivity(intent);
            }
        });

        // Get the image Uri from the Intent and display the image
        String imageUriString = getIntent().getStringExtra("image_uri");
        Uri imageUri = imageUriString != null ? Uri.parse(imageUriString) : null;
        String title = getIntent().getStringExtra("title");

        if (imageUri != null) {
            Log.d("IMAGE_DETAILS", "Image Uri: " + imageUri.toString());
            new SendImageToServerTask().execute(imageUri);
            imageView.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Image Uri is null", Toast.LENGTH_SHORT).show();
        }
        textView.setText(title);

        httpClient = new OkHttpClient();
        mainHandler = new Handler(Looper.getMainLooper());



        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d("Firebase UID", "User ID: " + userId);
        } else {
            Log.d("Firebase UID", "User not signed in.");
        }
    }

    private String formatSolutions(String solutionsString) {
        StringBuilder formattedSolutions = new StringBuilder();
        String[] solutions = solutionsString.split(";");

        for (int i = 0; i < solutions.length; i++) {
            if (i == 0) {
                formattedSolutions.append("\n"); // Add a newline character for the first solution
            }
            formattedSolutions.append("\u2022 ").append(solutions[i].trim());

            if (i < solutions.length - 1) {
                formattedSolutions.append("\n\n");
            }
        }

        return formattedSolutions.toString();
    }


    private void sendImageToServer(Uri imageUri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()) {
                        String idToken = task.getResult().getToken();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.VISIBLE);
                                    }
                                });

                                String userId = "1";

                                OkHttpClient client = new OkHttpClient.Builder()
                                        .connectTimeout(120, TimeUnit.SECONDS)
                                        .writeTimeout(120, TimeUnit.SECONDS)
                                        .readTimeout(120, TimeUnit.SECONDS)
                                        .build();

                                InputStream inputStream;
                                byte[] byteArray;
                                try {
                                    inputStream = getContentResolver().openInputStream(imageUri);

                                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                                    int bytesRead;
                                    byte[] data = new byte[1024];
                                    while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                                        buffer.write(data, 0, bytesRead);
                                    }
                                    buffer.flush();
                                    byteArray = buffer.toByteArray();
                                    inputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    byteArray = null;
                                }

                                if (byteArray != null) {
                                    MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
                                    RequestBody requestBody = new MultipartBody.Builder()
                                            .setType(MultipartBody.FORM)
                                            .addFormDataPart("image", "uploaded_image.jpg", RequestBody.create(MEDIA_TYPE_JPEG, byteArray))
                                            .addFormDataPart("user_id", userId)
                                            .build();

                                    Request request = new Request.Builder()
                                            .url("https://foliagefixerbackend.herokuapp.com/classify")
                                            .addHeader("authorization", idToken)
                                            .post(requestBody)
                                            .build();

                                    Log.d("SEND_IMAGE", "Request sent. Waiting for response...");

                                    try (Response response = client.newCall(request).execute()) {
                                        if (response.isSuccessful()) {
                                            String jsonResponse = response.body().string();
                                            if (!jsonResponse.isEmpty()) {
                                                Gson gson = new Gson();
                                                MyResponseObject responseObject = gson.fromJson(jsonResponse, MyResponseObject.class);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        TextView classificationTextView = findViewById(R.id.classificationTextView);
                                                        classificationTextView.setText("Classification: " + responseObject.getClassification());

                                                        TextView severityTextView = findViewById(R.id.severityTextView);
                                                        severityTextView.setText("Severity: " + responseObject.getSeverity());

                                                        TextView solutionsTextView = findViewById(R.id.solutionsTextView);
                                                        solutionsTextView.setText("Solutions: " + formatSolutions(responseObject.getSolutions()));
                                                    }
                                                });
                                            }

                                            mainHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setVisibility(View.GONE);
                                                    Log.d("JSON_RESPONSE", jsonResponse);
                                                }
                                            });
                                        } else {
                                            mainHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(ImageDetailsActivity.this, "Request failed: " + response.message(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        Log.d("SEND_IMAGE", "Response received: " + response);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        mainHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(ImageDetailsActivity.this, "Request failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        Log.e("SEND_IMAGE", "Exception while sending the image: ", e);
                                    }
                                } else {
                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(ImageDetailsActivity.this, "Failed to open image InputStream.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).start();
                    } else {
// Handle error
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(ImageDetailsActivity.this, "Failed to retrieve ID token: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ImageDetailsActivity.this, "User is not logged in.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



                                                private File createTemporaryImageFile(InputStream inputStream) throws IOException {
        File outputDir = getCacheDir();
        File outputFile = File.createTempFile("tempImage", ".jpg", outputDir);

        OutputStream outputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        inputStream.close();

        return outputFile;
    }
}

