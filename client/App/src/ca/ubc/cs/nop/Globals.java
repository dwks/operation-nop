package ca.ubc.cs.nop;

public class Globals {
    public static final String SERVER = "http://sirius.nss.cs.ubc.ca:8080";
    public static final String SESSION_ID
        = "f55c5204-2980-4f3c-ba2e-8a0bbc340d3c";

    /* data that is update periodically */
    public static double status;
    public static String city = "Unknown";
    public static String country = "Unknown";
    public static String street = "Unknown";
    public static String number = "Unknown";
    public static double airQuality;
    public static double[] fluPeople = new double[3];
    public static double[] fluHospitals = new double[3];
    public static double[] fluWorkPlaces = new double[3];
    public static double longitude;
    public static double latitude;
}
