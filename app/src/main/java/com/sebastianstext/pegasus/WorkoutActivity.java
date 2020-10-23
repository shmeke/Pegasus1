package com.sebastianstext.pegasus;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


public class WorkoutActivity extends AppCompatActivity implements SensorEventListener, StepListener {

    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private SensorManager sensorMan;
    private Sensor motion;
    private Sensor accel;
    TextView Distance, Speed;
    Button menu;
    private StepDetector simpleStepDetector;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;
    static Instant Start;
    TextView NumberStops;
    private int stops;

    DelayUtil d = new DelayUtil();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_activity);
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        motion = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accel = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        NumberStops = findViewById(R.id.textViewStopp);
       // buttonProf = findViewById(R.id.buttonProfile);
        //BtnStart = (Button) findViewById(R.id.buttonStart);
        Distance = findViewById(R.id.textViewDist);
        Speed = findViewById(R.id.textViewSpeed);
        menu = findViewById(R.id.buttonMenu);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
        sensorManager.registerListener(WorkoutActivity.this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        Instant start = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            start = Instant.now();
        }
        Start = start;
        /*NumberStops = findViewById(R.id.textViewStopp);
        DistTrav = findViewById(R.id.textViewDistTrav);
        DistSkritt = findViewById(R.id.textViewDistSkritt);
        DistGallopp = findViewById(R.id.textViewDistGallop);
        VoltRight = findViewById(R.id.textViewNmbrVoltRight);
        VoltLeft = findViewById(R.id.textViewNmbrVoltLeft);
*/    // Get an instance of the SensorManager

        /*buttonProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i = new Intent(WorkoutActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });*/
        /*BtnStart.setOnClickListener(new View.OnClickListener() {
         @Override
            public void onClick(View view) {


              Intent a = new Intent(WorkoutActivity.this, DistSpeedActivity.class);
                startActivity(a);


            }
        });*/
    }
    @Override
    public void onResume() {
        super.onResume();
        sensorMan.registerListener(this, motion,
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorMan.unregisterListener(this);
    }
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);

            float[] mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt(x*x + y*y + z*z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            float minDrift = (float) (mAccelCurrent - 0.01);
            float maxDrift = (float) (mAccelCurrent + 0.01);
            if(mAccelLast <= maxDrift && mAccelLast >= minDrift){
                d.delay(10);
                StopActivity();
            }


        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void step(long timeNs) {
        numSteps++;
        double meters = (numSteps);
        Instant still = Instant.now();
        Duration timeElapsed = Duration.between(Start, still);

        float sec = timeElapsed.toMillis();

        double km = (meters * 0.001);

        if(meters > 1000)
        {  Distance.setText(km + " km"); }
        else{  Distance.setText(meters + " m"); }

        double kps = (km) / (sec *3600);
        Speed.setText(kps+ " km/h");
    }

    public void StopActivity(){
        stops++;
        String Stops = String.valueOf(stops);
        NumberStops.setText(Stops);

    }
}

