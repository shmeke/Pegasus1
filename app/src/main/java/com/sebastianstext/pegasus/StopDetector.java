package com.sebastianstext.pegasus;

public class StopDetector {
    private static final int ACCEL_RING_SIZE = 50;
    private static final int VEL_RING_SIZE = 10;

    // change this threshold according to your sensitivity preferences
    private static final float STOP_THRESHOLD = 10f;

    private static final int STOP_DELAY_NS = 1000000000;

    private int accelRingCounter = 0;
    private float[] accelRingX = new float[ACCEL_RING_SIZE];
    private float[] accelRingY = new float[ACCEL_RING_SIZE];
    private float[] accelRingZ = new float[ACCEL_RING_SIZE];
    private int velRingCounter = 0;
    private float[] velRing = new float[VEL_RING_SIZE];
    private float minDrift;
    private float maxDrift;
    private long lastStopTimeNs = 0;
    private float oldVelocityEstimate = 0;
    private float olderVelocityEstimate = 0;

    private StopListener listener;

    public void registerListener(StopListener listener) {
        this.listener = listener;

    }

    public void stepCountUpdate(long timeNs, float x, float y, float z) {
        float[] currentAccel = new float[3];
        currentAccel[0] = x;
        currentAccel[1] = y;
        currentAccel[2] = z;

        // First step is to update our guess of where the global z vector is.
        accelRingCounter++;
        accelRingX[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[0];
        accelRingY[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[1];
        accelRingZ[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[2];

        float[] worldZ = new float[3];
        worldZ[0] = SensorFilter.sum(accelRingX) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
        worldZ[1] = SensorFilter.sum(accelRingY) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
        worldZ[2] = SensorFilter.sum(accelRingZ) / Math.min(accelRingCounter, ACCEL_RING_SIZE);

        float normalization_factor = SensorFilter.norm(worldZ);

        worldZ[0] = worldZ[0] / normalization_factor;
        worldZ[1] = worldZ[1] / normalization_factor;
        worldZ[2] = worldZ[2] / normalization_factor;

        float currentM = (float) Math.sqrt(worldZ[0]*worldZ[0] + worldZ[1]*worldZ[1] + worldZ[2]*worldZ[2]);
        velRingCounter++;
        velRing[velRingCounter % VEL_RING_SIZE] = currentM;

        float velocityEstimate = SensorFilter.sum(velRing);
        minDrift = (float) (oldVelocityEstimate - 0.01);
        maxDrift = (float) (oldVelocityEstimate + 0.1);


        if(minDrift < velocityEstimate){
            if (velocityEstimate < STOP_THRESHOLD && timeNs - lastStopTimeNs > STOP_DELAY_NS){
                listener.onStopCount(currentM);
                lastStopTimeNs = timeNs;

            }
        }

        olderVelocityEstimate = oldVelocityEstimate;
        oldVelocityEstimate = velocityEstimate;
    }
}
