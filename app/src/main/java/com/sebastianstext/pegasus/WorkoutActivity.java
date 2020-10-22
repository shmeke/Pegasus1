package com.sebastianstext.pegasus;


import android.content.Intent;
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


public class WorkoutActivity extends AppCompatActivity {

    //NumberStops, DistTrav, DistSkritt, DistGallopp, VoltRight, VoltLeft;
    Button buttonProf;
    Button BtnStart;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        buttonProf = findViewById(R.id.buttonProfile);
        BtnStart = (Button) findViewById(R.id.buttonStart);

        /*NumberStops = findViewById(R.id.textViewStopp);
        DistTrav = findViewById(R.id.textViewDistTrav);
        DistSkritt = findViewById(R.id.textViewDistSkritt);
        DistGallopp = findViewById(R.id.textViewDistGallop);
        VoltRight = findViewById(R.id.textViewNmbrVoltRight);
        VoltLeft = findViewById(R.id.textViewNmbrVoltLeft);
*/    // Get an instance of the SensorManager

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
            public void onClick(View view) {
              Intent a = new Intent(WorkoutActivity.this, DistSpeedActivity.class);
                startActivity(a);

                /*Intent b = new Intent(WorkoutActivity.this, MovementDetector.class);
                startActivity(b);*/
            }
        });
    }


}

