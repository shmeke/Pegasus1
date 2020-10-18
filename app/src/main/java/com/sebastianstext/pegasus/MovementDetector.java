package com.sebastianstext.pegasus;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MovementDetector extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    double[] maxAccelerations = new double[3];
    double[] position = new double[3];
    long[] times = new long[3];
    // time combined with maxAcceleration can approximate the change in position,
// with the formula Δpos = (maxAcceleration * time ^ 2) / 6
    long currentTime;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            sensorManager.registerListener(this, accelerometer, sensorManager.SENSOR_DELAY_FASTEST);
        }
        currentTime = System.currentTimeMillis();
        for (int i = 0; i < 3; i++) {
            times[i] = currentTime;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor ignore, int thisFunction) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            for (int i = 0; i < 3; i++) {
                if (Math.abs(event.values[i]) < 0.01) {
                    // Note: this is to try to prevent accelerating time from being counted when the phone is stationary. 0.01 should be
                    // changed to an appropriate sensitivity level that can be calculated by finding an average noise level when the phone is stationary.
                    times[i] = System.currentTimeMillis();
                }
                if (event.values[i] > maxAccelerations[i] && maxAccelerations[i] >= 0) {
                    maxAccelerations[i] = event.values[i];
                } else if (event.values[i] < maxAccelerations[i] && maxAccelerations[i] <= 0) {
                    maxAccelerations[i] = event.values[i];
                } else if (event.values[i] > 0 && maxAccelerations[i] < 0) {
                    currentTime = System.currentTimeMillis();
                    position[i] += maxAccelerations[i] * (times[i] - currentTime) * (times[i] - currentTime) / 6;
                    times[i] = currentTime;
                    maxAccelerations[i] = event.values[i];
                } else if (event.values[i] < 0 && maxAccelerations[i] > 0) {
                    currentTime = System.currentTimeMillis();
                    position[i] += maxAccelerations[i] * (times[i] - currentTime) * (times[i] - currentTime) / 6;
                    times[i] = currentTime;
                    maxAccelerations[i] = event.values[i];
                }
            }
        }
    }
}