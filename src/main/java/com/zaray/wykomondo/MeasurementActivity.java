package com.zaray.wykomondo;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MeasurementActivity extends AppCompatActivity {
    private int counterSeconds = 0;
    private double distance = 0.0;
    private double maxSpeed = 0;
    private boolean isRunning;
    private boolean wasRunning;
    private boolean isBounded = false;
    private MeasurementService measurementService;
    private String time;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            MeasurementService.MeasurementBinder measurementBinder =
                    (MeasurementService.MeasurementBinder) binder;
            measurementService = measurementBinder.getMeasurement();
            isBounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBounded = false;
        }
    };


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("seconds", counterSeconds);
        savedInstanceState.putBoolean("isRunning", isRunning);
        savedInstanceState.putBoolean("wasRunning", wasRunning);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (savedInstanceState != null) {
            counterSeconds = savedInstanceState.getInt("seconds");
            isRunning = savedInstanceState.getBoolean("isRunning");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }
        measurementStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        wasRunning = isRunning;
        isRunning = false;
        if (isBounded) {
            unbindService(connection);
            isBounded = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MeasurementService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        if (wasRunning) {
            isRunning = true;
        }
    }

    public void onClickStartCounter(View view) {
        isRunning = true;
    }


    public void onClickPauseCounter(View view) {
        isRunning = false;
    }

    public void onClickStopCounter(View view) {
        isRunning = false;
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("EXTRA_TOTAL_TIME", time);
        intent.putExtra("MAX_SPEED", maxSpeed);
        intent.putExtra("DISTANCE", distance);
        intent.putExtra("SECONDS", counterSeconds);
        startActivity(intent);
    }


    public void measurementStart() {
        final TextView timeCounter = (TextView) findViewById(R.id.timeCounter);
        final TextView totalDistance = (TextView) findViewById(R.id.totalDistance);
        final TextView currentSeedText = (TextView) findViewById(R.id.currentSpeed);
        final TextView maxSeedText = (TextView) findViewById(R.id.maxSpeed);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double currentSpeed = 0.0;

                if (measurementService != null && isRunning)  {
                    distance = measurementService.getDistance();
                    currentSpeed = measurementService.getSpeed();
                                 }

                String distanceStr = String.format("%1$,.2f km", distance);
                totalDistance.setText(distanceStr);
                String currentSpeedString = String.format("%1$,.2f m/s", currentSpeed);
                currentSeedText.setText(currentSpeedString);

                if (currentSpeed >= maxSpeed) {
                    maxSpeed = currentSpeed;
                    String maxSpeedString = String.format("%1$,.2f m/s", maxSpeed);
                    maxSeedText.setText(maxSpeedString);
                }

                int hours = counterSeconds / 3600;
                int minutes = (counterSeconds % 3600) / 60;
                int secs = counterSeconds % 60;
                time = String.format("%d:%02d:%02d", hours, minutes, secs);
                timeCounter.setText(time);


                if (isRunning) {
                    counterSeconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }


}
