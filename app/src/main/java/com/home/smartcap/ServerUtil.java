package com.home.smartcap;

/**
 * Created by amukherjee on 11/8/16.
 */

public class ServerUtil {

    public static final String PATIENT_ENDPOINT ="http://ec2-52-33-82-105.us-west-2.compute.amazonaws.com/patient/";

    public static final String USERS_ENDPOINT = "http://ec2-52-33-82-105.us-west-2.compute.amazonaws.com/user/";

    private static final String BASE_ENDPOINT = "http://ec2-52-33-82-105.us-west-2.compute.amazonaws.com/";

    public static String getPatientEndpoint(String email){
        return PATIENT_ENDPOINT+email;
    }

    public static String getUsersEndpoint(String email, String password){
        return USERS_ENDPOINT+ email + "/" + password;
    }

    public static String getBaseEndpoint(){
        return BASE_ENDPOINT;
    }
}
