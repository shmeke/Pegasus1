package com.sebastianstext.pegasus;

public class rotaionDetector {

private rotaionListener listener;

    float currentDegree;
    float oldDegree;
    float minTurnDrift;
    float maxTurnDrift;
    final static float TURN_DELAY_NS = 100000000;


public void registerListener(rotaionListener listener) { this.listener = listener; }

    public void detectTurning(long timeNs, float x, float y, float z){

    float degree = Math.round(x);

    currentDegree = -degree;

    maxTurnDrift = currentDegree + 5;
    minTurnDrift = currentDegree - 5;


    if(oldDegree > minTurnDrift && oldDegree < maxTurnDrift) {

    }
    else {
        listener.detectTurn(currentDegree, oldDegree);
    }

    oldDegree = currentDegree;


       // listener.detectTurn(currentDegree);

    }
}
