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


public class WorkoutActivity extends AppCompatActivity implements SensorEventListener, StepListener, rotaionListener {

    SensorManager sm;
    Sensor accel;
    Sensor rotation;
    private StepDetector simpleStepDetector;
    private rotaionDetector turnDetect;
    TextView magnet, speed, stop, dist, right;
    Button profile, workouts;
    DelayUtil d = new DelayUtil();
    private int numSteps;
    private int leftTurn, rightTurn, fullLeftVolt, fullRightVolt, halfLeftVolt, halfRightVolt;
    private int degreeCount;
    private int oldStepCount;
    private int stopCount;
    private double meters;
    private double km;
    static Instant Start, startVoltLeft, startVoltRight;
    private float roofValue;
    private double kps;
   User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        magnet = findViewById(R.id.magneto);
        dist = findViewById(R.id.dist);
        speed = findViewById(R.id.speed);
        stop = findViewById(R.id.stop);
        right = findViewById(R.id.right);
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        rotation = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
        turnDetect = new rotaionDetector();
        turnDetect.registerListener(this);
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
        sm.registerListener(WorkoutActivity.this, rotation, SensorManager.SENSOR_DELAY_NORMAL);

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

        if(sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION){
            turnDetect.detectTurning(
                    sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]
            );
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void detectTurn(float currentDegree, float oldDegree) {

    String old = String.valueOf(oldDegree);
    String newV = String.valueOf(currentDegree);

    dist.setText(old);
    speed.setText(newV);

    if(oldDegree > currentDegree){
        startVoltLeft = Instant.now();
        leftTurn++;
    }
    else if(oldDegree < currentDegree){
        startVoltRight = Instant.now();
        rightTurn++;
    }

   switch (leftTurn){
        case 20:
            fullLeftVolt++;
            stop.setText(String.valueOf(fullLeftVolt));
            Instant leftVoltStop = Instant.now();
            Duration leftVolt = Duration.between(startVoltLeft, leftVoltStop);
            leftTurn = 0;
        break;

       default:
           leftTurn = 0;
           break;
    }

    switch (rightTurn){
        case 20:
            fullRightVolt++;
            right.setText(String.valueOf(fullRightVolt));
            Instant rightVoltStop = Instant.now();
            Duration rightVolt = Duration.between(startVoltRight, rightVoltStop);
            rightTurn = 0;
         break;

        default:
            rightTurn = 0;
        break;
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

       /* String Dist = String.valueOf(meters);
        String Speed = String.valueOf(kps);
        String Stop = String.valueOf(stopCount);*/

        //dist.setText(Dist);
        //speed.setText(Speed);
        //stop.setText(Stop);


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

