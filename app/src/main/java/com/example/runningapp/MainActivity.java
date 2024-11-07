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

    // Displays the time and run
    private TextView tvSteps, tvTime;

    // Controls the run with these buttons
    private Button btnStart, btnStop, btnReset, btnShowRun;

    // Manages the step counter and if the run is being tracked. False by default lest we have any undue accidents
    private int steps = 0;
    private long startTime = 0;
    private boolean running = false;

    // Accelerometer Functionality
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    // Updating Time
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    // Step Threshold consideration.....value fresh from my arse.
    private final float STEP_THRESHOLD = 17.5f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // UI Elements ---> Local Use
        tvSteps = findViewById(R.id.tvSteps);
        tvTime = findViewById(R.id.tvTimer);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnReset = findViewById(R.id.btnReset);
        btnShowRun = findViewById(R.id.btnshowrun);

        // Local use of SensorManager & the Accelerometer Sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Start Button ---> Starts the Step Counter and Timer
        btnStart.setOnClickListener(v -> startRun());

        // Stop Button ---> Stops the Step Counter and Timer
        btnStop.setOnClickListener(v -> stopRun());

        // Reset Button ---> Resets the Total Steps and Timer to Zero
        btnReset.setOnClickListener(v -> resetRun());

        // Show Run Button ---> Opens the 2nd Page with the Run Details (Will only Function if the Run is Stopped)
        btnShowRun.setOnClickListener(v -> showRunDetails());
    }
    // "Press START to begin" Even after pressing top and pressing start again, it will reset the UI
    // I know its a consequence of how the code is written, but......I wish I could change that.
    private void startRun()
    {
        if (!running)
        {
            // variables in local use
            running = true;
            steps = 0;  //Also used in the UI reset
            startTime = System.currentTimeMillis();

            // Reset the textviews upon beginning this function
            tvSteps.setText(String.valueOf(steps));
            tvTime.setText("0");

            // accelerometer starts listening
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);

            // Start the Timer
            startTimer();
        }
    }

    // "Press Pause"
    private void stopRun()
    {
        if (running)
        {
            running = false;
            sensorManager.unregisterListener(this);
            stopTimer();
        }
    }

    // "Hold L+R, then Press START to reset" ----> Simply refreshes the UI. Nothing Else.
    // Does not work if the program is tracking steps. Want to change that.....but no.
    private void resetRun()
    {
        if (!running)
        {
            steps = 0;
            tvSteps.setText(String.valueOf(steps));
            tvTime.setText("0");
        }
    }

    // "Post Score to the Leaderboard?  Y/N ?" ----> Displays the run on a seperate page.
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

    //not sure why this is here. It does nothing. Except be a class from the SensorListener.
    // Oh well. may as well keep it here.
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // ...
    }

    // Starting the Timer. Not much else to say.
    // It displays it in seconds
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
    // Don't want any errant.....data harvesting and getting sued
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
    // I mean it works, but in the sense that it will work ONLY AFTER the data has been wiped.
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