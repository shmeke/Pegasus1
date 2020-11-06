package com.sebastianstext.pegasus;

public class URLs {
    private static final String ROOT_URL = "http://188.149.0.124/mustang/pegasus/api.php?apicall=";

    public static final String URL_REGISTER = ROOT_URL + "signup";
    public static final String URL_LOGIN= ROOT_URL + "login";
    public static final String URL_TEMPWORKOUT = ROOT_URL + "tempworkout";
    public static final String URL_DATESPINNER = ROOT_URL + "getspinnerdates";
    public static final String URL_GETWORKOUTS = ROOT_URL + "getWorkouts";

    public static final String KEY_NMBRSTOPS = "nmbrstops";
    public static final String KEY_METERS = "meters";
    public static final String KEY_AVERAGESPEED = "averagespeed";
    public static final String JSON_ARRAY = "result";

}
