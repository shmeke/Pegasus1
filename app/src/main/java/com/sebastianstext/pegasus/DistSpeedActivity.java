package com.sebastianstext.pegasus;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.time.Duration;
import java.time.Instant;

public class DistSpeedActivity extends AppCompatActivity implements SensorEventListener, StepListener {

    TextView Distance, Speed;
    Button menu;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;
    private double meters;
    private double km;
    static Instant Start;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_activity);
        Distance = findViewById(R.id.textViewDist);
        Speed = findViewById(R.id.textViewSpeed);
        menu = findViewById(R.id.buttonMenu);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
        sensorManager.registerListener(DistSpeedActivity.this, accel, SensorManager.SENSOR_DELAY_NORMAL);

        Instant start = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            start = Instant.now();
        }
        Start = start;

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(DistSpeedActivity.this, WorkoutActivity.class);
                startActivity(a);
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void step(long timeNs) {
        numSteps++;
        meters = (numSteps);
        Instant still = Instant.now();
        Duration timeElapsed = Duration.between(Start, still);

        float sec = timeElapsed.toMillis();
        float timeSeconds = sec;

        km = (meters*0.001);

        if(meters > 1000)
        {  Distance.setText(km+ " km"); }
        else{  Distance.setText(meters+ " m"); }

        double kps = (km) / (timeSeconds*3600);
        Speed.setText(kps+ " km/h");
    }
}
