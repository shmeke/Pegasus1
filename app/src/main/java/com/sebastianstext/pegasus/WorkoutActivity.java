package com.sebastianstext.pegasus;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;


import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;


public class WorkoutActivity extends AppCompatActivity implements SensorEventListener, StepListener {

    SensorManager sm;
    Sensor accel;
    private StepDetector simpleStepDetector;

    Button profile, workouts;
    DelayUtil d = new DelayUtil();
    private int numSteps;
    private int oldStepCount;
    private int stopCount;
    private double meters;
    private double km;
    static Instant Start;
    private float roofValue;
    private double kps;
   User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
        profile = findViewById(R.id.buttonProfile);
        user = SharedPrefManager.getInstance(WorkoutActivity.this).getUser();
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateWorkoutSession();
                finish();
                Intent i = new Intent(WorkoutActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        sm.registerListener(WorkoutActivity.this, accel, SensorManager.SENSOR_DELAY_NORMAL);

        Instant start = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            start = Instant.now();
        }
        Start = start;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void tempWorkout(long timeNs, float velocityEstimate) {
        numSteps++;
        meters = (numSteps);
        km = (meters * 0.001);
        Instant still = Instant.now();
        Duration timeElapsed = Duration.between(Start, still);
        float sec = timeElapsed.toMillis();

        roofValue = 0.5f;
        if(velocityEstimate <= roofValue && oldStepCount < numSteps && velocityEstimate > 0){
            stopCount++;
        }
        oldStepCount = numSteps;

        kps = (km) / (sec * 3600);

    }

   private void updateWorkoutSession(){
        final String id = String.valueOf(user.getId());
        final String username = user.getUsername();
        final String metersTravelled = String.valueOf(meters);
        final String averageSpeed = String.valueOf(kps);
        final String Stops = String.valueOf(stopCount);

    @SuppressLint("StaticFieldLeak")
    class UpdateWorkoutSession extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            //creating request handler object
            RequestHandler requestHandler = new RequestHandler();

            //creating request parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("id", id);
            params.put("username", username);
            params.put("accelvalue", Stops);
            params.put("meters", metersTravelled);
            params.put("averagespeed", averageSpeed);

            //returning the response
            return requestHandler.sendPostRequest(URLs.URL_TEMPWORKOUT, params);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                //converting response to json object
                JSONObject obj = new JSONObject(s);

                //if no error in response
                if (!obj.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

        UpdateWorkoutSession uws = new UpdateWorkoutSession();
        uws.execute();
    }
}

