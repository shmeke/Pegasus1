package com.sebastianstext.pegasus;

public class WorkoutsList {

    private String user;
    private int nmbrstops;
    private String avrgspeed;
    private int meters;

    public WorkoutsList(String user, int nmbrstops, int meters, String avrgspeed) {
        this.user = user;
        this.avrgspeed = avrgspeed;
        this.meters = meters;
        this.nmbrstops = nmbrstops;
    }

    public String getUser(){return user;}

    public int getNmbrstops(){return nmbrstops;}

    public String getAvrgspeed(){return avrgspeed;}

    public int getMeters(){return meters;}
}
