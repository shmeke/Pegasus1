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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


@RequiresApi(api = Build.VERSION_CODES.O)
public class WorkoutActivity extends AppCompatActivity implements SensorEventListener, StepListener {

    TextView Distance;//, Speed, NumberStops, DistTrav, DistSkritt, DistGallopp, VoltRight, VoltLeft;
    Button buttonProf;
    private Intent intent;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;
    private Button BtnStart;
    private int meters;
    private int speed;



    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
    LocalDateTime now = LocalDateTime.now();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_activity);
        buttonProf = findViewById(R.id.buttonProfile);
        Distance = findViewById(R.id.textViewDist);
        /*Speed = findViewById(R.id.textViewSpeed);
        NumberStops = findViewById(R.id.textViewStopp);
        DistTrav = findViewById(R.id.textViewDistTrav);
        DistSkritt = findViewById(R.id.textViewDistSkritt);
        DistGallopp = findViewById(R.id.textViewDistGallop);
        VoltRight = findViewById(R.id.textViewNmbrVoltRight);
        VoltLeft = findViewById(R.id.textViewNmbrVoltLeft);
*/    // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
        BtnStart = (Button) findViewById(R.id.buttonStart);
        buttonProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i = new Intent(WorkoutActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });
        BtnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                numSteps = 0;

                sensorManager.registerListener(WorkoutActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);

            }
        });



    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        meters = (int) (numSteps*0.65);

        Distance.setText(numSteps+ " m");
    }
}

