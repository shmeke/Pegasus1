package com.sebastianstext.pegasus;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.time.Instant;

public class MovementDetector extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorMan;
    private Sensor motion;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private int stops;
    private int starts;
    private float minDrift;
    private float maxDrift;
    TextView NumberStops, Start;
    static Instant StartTime;
    DelayUtil d = new DelayUtil();

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_activity);
        Start = findViewById(R.id.textViewDistTrav);
        NumberStops = findViewById(R.id.textViewStopp);

        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        motion = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        StartTime = Instant.now();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt(x*x + y*y + z*z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            minDrift = (float) (mAccelCurrent - 0.01);
            maxDrift = (float) (mAccelCurrent + 0.01);
            // Make this higher or lower according to how much
            // motion you want to detect
                if(mAccelLast <= maxDrift && mAccelLast >= minDrift){
                    StopActivity();
                }
        }



            }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void StopActivity(){
        stops++;
        String Stops = String.valueOf(stops);
        NumberStops.setText(Stops);
        finish();
        Intent i = new Intent(MovementDetector.this, WorkoutActivity.class);
        startActivity(i);
    }
}