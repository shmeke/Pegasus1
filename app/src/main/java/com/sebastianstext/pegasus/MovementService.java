package com.sebastianstext.pegasus;



public class MovementService  {

    private static final int STOP_DELAY_NS = 250000000;
    float mAccel, mAccelCurrent, mAccelLast, minDrift, maxDrift;
    private long lastStopTimeNs = 0;


    private StopListener listener;
    public void registerListener(StopListener listener) { this.listener = listener; }


    public void updateStopCount(long timeNS, float x, float y, float z){


        mAccel = 0.0f;

        float[] currentAccel = new float[3];
        currentAccel[0] = x;
        currentAccel[1] = y;
        currentAccel[2] = z;

        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt(x*x + y*y + z*z);
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta;
        minDrift = (float) (mAccelCurrent - 0.01);
        maxDrift = (float) (mAccelCurrent + 0.01);

        if(mAccel <= minDrift && mAccel >= maxDrift && timeNS - lastStopTimeNs > STOP_DELAY_NS) {
            listener.onStopCount(timeNS);
            lastStopTimeNs = timeNS;
        }

    }



}


