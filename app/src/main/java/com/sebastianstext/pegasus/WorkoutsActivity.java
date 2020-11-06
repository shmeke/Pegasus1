package com.sebastianstext.pegasus;


import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;


import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;



public class WorkoutsActivity extends AppCompatActivity {

    TextView distance, speed, stops, velEst;
    Button menu, search;
    Spinner spinnerDate;
    WorkoutsList workoutsList;
    User user;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.workouts_activity);
        distance = findViewById(R.id.distTot);
        speed = findViewById(R.id.snittSpeed);
        stops = findViewById(R.id.nmrStopp);
        menu = findViewById(R.id.backToMenu);
        spinnerDate = findViewById(R.id.spinnerDate);
        workoutsList = SharedPrefManager.getInstance(WorkoutsActivity.this).getWorkout();
        user = SharedPrefManager.getInstance(WorkoutsActivity.this).getUser();
        search = findViewById(R.id.buttonGo);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i = new Intent(WorkoutsActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWorkout();
                String Speed = String.valueOf(workoutsList.getAvrgspeed());
                String Dist = String.valueOf(workoutsList.getMeters());
                String Stops = String.valueOf(workoutsList.getNmbrstops());
                distance.setText(Dist);
                speed.setText(Speed);
                stops.setText(Stops);
            }
        });


    }



    public void getWorkout(){
        final String username = user.getUsername();
        final String date = String.valueOf(spinnerDate.getSelectedItem());

        class GetWorkout extends AsyncTask<Void, Void, String> {

            ProgressBar progressBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);


                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject workoutJson = obj.getJSONObject("workout");

                        //creating a new user object
                        WorkoutsList workoutsList = new WorkoutsList(
                                workoutJson.getString("user"),
                                workoutJson.getInt("meters"),
                                workoutJson.getInt("nmbrstops"),
                                workoutJson.getString("avrgspeed")
                        );

                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).workouts(workoutsList);

                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("date", date);

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_GETWORKOUTS, params);
            }
        }

        GetWorkout gw = new GetWorkout();
        gw.execute();
    }

}











