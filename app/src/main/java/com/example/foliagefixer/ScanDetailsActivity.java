package com.example.foliagefixer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class ScanDetailsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_details);

        TextView logoutTextView = findViewById(R.id.logoutTextView);
        mAuth = FirebaseAuth.getInstance();
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the user from Firebase Auth
                mAuth.signOut();

                // Navigate back to the login screen or any other appropriate activity
                Intent intent = new Intent(ScanDetailsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        TextView appTitle = findViewById(R.id.Title1);
        appTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScanDetailsActivity.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        Scan scan = getIntent().getParcelableExtra("selected_scan");

        ImageView detailsImageView = findViewById(R.id.detailsImageView);
        TextView classificationTextView = findViewById(R.id.classificationTextView);
        TextView severityTextView = findViewById(R.id.severityTextView);
        TextView solutionsTextView = findViewById(R.id.solutionsTextView);

        if (scan != null) {
            Glide.with(this).load(scan.getImage()).into(detailsImageView);

            classificationTextView.setText(scan.getClassification().toString());
            severityTextView.setText(String.format("Severity: %.2f", scan.getSeverity()));

            String solutions = scan.getSolutions();
            if (solutions != null && !solutions.isEmpty()) {
                String[] solutionsArray = solutions.split(";");
                StringBuilder solutionsText = new StringBuilder("Solutions:\n");
                for (int i = 0; i < solutionsArray.length; i++) {
                    solutionsText.append("\u2022 ").append(solutionsArray[i].trim());
                    if (i < solutionsArray.length - 1) {
                        solutionsText.append("\n");
                    }
                }
                solutionsTextView.setText(solutionsText.toString());
            } else {
                solutionsTextView.setText("Solutions: Not available");
            }
        }
    }
}

