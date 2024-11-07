package com.example.runningapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    // My UI for Displaying the Steps and the Time
    private TextView tvSteps, tvTime;

    // My Buttons for Controlling the Run
    private Button btnStart, btnStop, btnReset, btnShowRun;

    // Variables for Managing the Step Counter and Timer
    private int steps = 0;
    private long startTime = 0;
    private boolean running = false;

    // Functionality for the Accelerometer
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    // Timer Handler for Updating Time
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    // What is considered a Step Threshold
    private final float STEP_THRESHOLD = 17.5f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // My UI Elements
        tvSteps = findViewById(R.id.tvSteps);
        tvTime = findViewById(R.id.tvTimer);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnReset = findViewById(R.id.btnReset);
        btnShowRun = findViewById(R.id.btnshowrun);

        // My SensorManager & the Accelerometer Sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Start Button: Starts the Step Counter and Timer
        btnStart.setOnClickListener(v -> startRun());

        // Stop Button: Stops the Step Counter and Timer
        btnStop.setOnClickListener(v -> stopRun());

        // Reset Button: Resets the Total Steps and Timer to Zero
        btnReset.setOnClickListener(v -> resetRun());

        // Show Run Button: Opens the 2nd Page with the Run Details (Will only Function if the Run is Stopped)
        btnShowRun.setOnClickListener(v -> showRunDetails());
    }
    // When the Start Button is Pressed...
    private void startRun()
    {
        if (!running)
        {
            // My Variables
            running = true;
            steps = 0;
            startTime = System.currentTimeMillis();

            // Reset the UI when the Run Starts
            tvSteps.setText(String.valueOf(steps));
            tvTime.setText("0");

            // Make the accelerometer start Listening
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);

            // Start the Timer
            startTimer();
        }
    }

    // When the Stop Button is Pressed...
    private void stopRun()
    {
        if (running)
        {
            running = false;
            sensorManager.unregisterListener(this);
            stopTimer();
        }
    }

    // When the Reset Button is Pressed...
    private void resetRun()
    {
        if (!running)
        {
            steps = 0;
            tvSteps.setText(String.valueOf(steps));
            tvTime.setText("0");
        }
    }

    // When the Show Run Button is Pressed...
    private void showRunDetails()
    {
        if (!running)
        {
            Intent intent = new Intent(this, RunDetailsActivity.class);
            intent.putExtra("steps", steps);
            intent.putExtra("time", (System.currentTimeMillis() - startTime) / 1000);
            startActivity(intent);
        }
    }

    // The Accelerometer (important)
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (running && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            // The magnitude of the accelerometer
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float magnitude = (float) Math.sqrt(x * x + y * y + z * z);

            // High Magnitude = A Step
            if (magnitude > STEP_THRESHOLD)
            {
                steps++;
                // Step Count Goes Up
                tvSteps.setText(String.valueOf(steps));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // ...
    }

    // Starting the Timer
    private void startTimer()
    {
        timerRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                // Calculate the Time in Seconds
                long timeElapsed = (System.currentTimeMillis() - startTime) / 1000;
                tvTime.setText(String.valueOf(timeElapsed));
                // Update Every Second
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    // Stop the Timer
    private void stopTimer()
    {
        timerHandler.removeCallbacks(timerRunnable);
    }

    // Stopping the Sensor when Paused
    @Override
    protected void onPause()
    {
        super.onPause();
        if (running)
        {
            sensorManager.unregisterListener(this);
            stopTimer();
        }
    }

    // Resume the Sensor when Unpaused
    @Override
    protected void onResume()
    {
        super.onResume();
        if (running)
        {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            startTimer();
        }
    }
}