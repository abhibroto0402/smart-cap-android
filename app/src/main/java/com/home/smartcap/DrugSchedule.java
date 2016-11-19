package com.home.smartcap;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by amukherjee on 11/15/16.
 */

public class DrugSchedule {

    public String drug_name;
    public String dosage;
    public String expdate;
    public String smartcap_id;


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
            default:
                return "";
        }

    }
}
