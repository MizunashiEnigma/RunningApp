package com.example.runningapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.runningapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RunDetailsActivity extends AppCompatActivity
{
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_details);

        // Grabbing Data from MainActivity
        Intent intent = getIntent();
        int steps = intent.getIntExtra("steps", 0);
        long timeInSeconds = intent.getLongExtra("time", 0);

        // Calculating the Metres Ran (Formula: Steps * 0.8)
        double metersRan = steps * 0.8;

        // And the Calories Burned (Formula: Steps * 0.04)
        double caloriesBurned = steps * 0.04;

        // The Current Date
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        // My References to the TextViews
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvMeters = findViewById(R.id.tvMeters);
        TextView tvCalories = findViewById(R.id.tvCalories);
        TextView tvTimeTaken = findViewById(R.id.tvTimeTaken);

        // Apply the changes to the TextViews
        tvDate.setText("Date: " + currentDate);
        tvMeters.setText("Meters Ran: " + String.format(Locale.getDefault(), "%.2f", metersRan));
        tvCalories.setText("Calories Burned: " + String.format(Locale.getDefault(), "%.2f", caloriesBurned));
        tvTimeTaken.setText("Time Taken: " + timeInSeconds + " seconds");

        // The Back Button (Returns to 1st Page
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
}