package com.example.runningapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.runningapp.R;

// used for the dates
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RunDetailsActivity extends AppCompatActivity
{
    @SuppressLint("SetTextI18n") //unconcerned about Multi-Language Support. HardCoded string ftw.
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_details);

        // Data form MainActivity
        Intent intent = getIntent();
        int steps = intent.getIntExtra("steps", 0);
        long timeInSeconds = intent.getLongExtra("time", 0);

        // Calculating the Metres Ran (Formula: Steps * 0.8)
        double metersRan = steps * 0.8;

        // And the Calories Burned (Formula: Steps * 0.04)
        double caloriesBurned = steps * 0.04;

        // Grabs the current data :  EU date format is the default. But it will grap the user's locale
        // for those weirdo that use mmddyyyy. clearly the best is yyyymmdd
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        // TextViews
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvMeters = findViewById(R.id.tvMeters);
        TextView tvCalories = findViewById(R.id.tvCalories);
        TextView tvTimeTaken = findViewById(R.id.tvTimeTaken);

        // Apply changes to TextViews
        tvDate.setText("Date: " + currentDate);
        tvMeters.setText("Meters Ran: " + String.format(Locale.getDefault(), "%.2f", metersRan));
        tvCalories.setText("Calories Burned: " + String.format(Locale.getDefault(), "%.2f", caloriesBurned));
        tvTimeTaken.setText("Time Taken: " + timeInSeconds + " seconds");

        // The Back Button (Returns to 1st Page ---> initially crashed until i double checked the manifest. ......i feel so~ stupid
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
}