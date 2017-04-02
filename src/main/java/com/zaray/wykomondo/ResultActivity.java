package com.zaray.wykomondo;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();


        String totalTime = intent.getStringExtra("EXTRA_TOTAL_TIME");
        double maxSpeed = intent.getDoubleExtra("MAX_SPEED", 0);
        double totalDistance = intent.getDoubleExtra("DISTANCE", 0);
        double distanceForAverage = intent.getDoubleExtra("DISTANCE", 0);
        int secondsForAverage = intent.getIntExtra("SECONDS", 0);
        double averageSpeed = (distanceForAverage * 1000 / secondsForAverage);


        Resources res = getResources();
        String result = res.getString(R.string.result, totalDistance, totalTime, averageSpeed, maxSpeed);
        TextView textView = (TextView) findViewById(R.id.resultText);
        textView.setText(result);
    }
    public void onClickBackToMenu (View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}