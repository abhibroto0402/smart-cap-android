package com.home.smartcap;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by amukherjee on 11/15/16.
 */

public class DrugSchedule {

    public String drug_name;
    public String dosage;
    public String expdate;
    public String smartcap_id;
    public String meal;
    private static final HashMap<String, String[]> time_dose_map;
    static{
        time_dose_map = new HashMap<>();
        time_dose_map.put("1X", new String[]{"11","13"});
        time_dose_map.put("2X", new String[]{"11", "13", "18", "20"});
        time_dose_map.put("3X", new String[]{"7", "9", "13", "15", "18", "20"});
    }


    public void setListData(JSONArray tempArr){
        try {
                for(int x=0; x<5;x++) {
                    switch (x) {
                        case 0:
                            this.smartcap_id = tempArr.getString(x);
                            break;
                        case 1:
                            this.drug_name = tempArr.getString(x);
                            break;
                        case 2:
                            this.dosage = tempArr.getString(x);
                            break;
                        case 3:
                            this.meal = tempArr.getString(x);
                            break;
                        case 4:
                            this.expdate = tempArr.getString(x).replace("/","-");
                            break;
                    }
                }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getListData(String params){
        switch (params){
            case "smartcapid":
                return this.smartcap_id;
            case "drug_name":
                return this.drug_name;
            case "expdate":
                return "Expiry Date: " + this.expdate;
            case "dosage":
                return "Daily Dosage: " + this.dosage;
            case "nextIntake":
                String [] temp = time_dose_map.get(dosage);
                DateFormat sdf = new SimpleDateFormat("HH");
                String hour = sdf.format(new Date());
                for(String e: temp){
                    if(Integer.parseInt(e)>= Integer.parseInt(hour)){
                        return "Next Dosage between: " + String.valueOf(Integer.parseInt(e)-2)+ ":00 -"+ e + ":00" + " ("+this.meal+" meal)";

                    }
                }
                return "Next dosage between 18:00 - 20:00";
            default:
                return "";
        }

    }
}
