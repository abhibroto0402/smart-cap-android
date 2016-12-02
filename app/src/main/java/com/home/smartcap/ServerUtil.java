package com.home.smartcap;

/**
 * Created by amukherjee on 11/8/16.
 */

public class ServerUtil {

    public static final String PATIENT_ENDPOINT ="https://smartcap-cloned-abhibroto0402.c9users.io/patient/";

    public static final String USERS_ENDPOINT = "https://smartcap-cloned-abhibroto0402.c9users.io/user/";

    private static final String BASE_ENDPOINT = "https://smartcap-cloned-abhibroto0402.c9users.io/";

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
