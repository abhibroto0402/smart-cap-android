package com.home.smartcap;

/**
 * Created by amukherjee on 11/8/16.
 */

public class ServerUtil {

    private static final String PATIENT_ENDPOINT ="https://smartcap-abhibroto0402.c9users.io/patient/";

    private static final String USERS_ENDPOINT = "https://smartcap-abhibroto0402.c9users.io/user/";


    public static String getPatientEndpoint(String email){
        return PATIENT_ENDPOINT+email;
    }

    public static String getUsersEndpoint(String email, String password){
        return USERS_ENDPOINT+ email + "/" + password;
    }
}
