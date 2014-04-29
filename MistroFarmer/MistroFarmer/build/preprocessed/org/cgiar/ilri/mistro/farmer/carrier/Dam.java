package org.cgiar.ilri.mistro.farmer.carrier;

import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * This is a carrier Class for dam information.
 * This class is a sub class of the Cow class.
 * 
 * @author Jason Rogena <j.rogena@cgiar.org>
 */
public class Dam extends Cow {
    private String embryoNumber;

    public Dam() {
        //set super to false since this is a dam
        super(false);
        setSex(SEX_FEMALE);
        embryoNumber="";
    }

    public void setEmbryoNumber(String embryoNumber) {
        this.embryoNumber = embryoNumber;
    }

    public String getEmbryoNumber() {
        return embryoNumber;
    }
    
    /**
     * This method returns data stored in this object as a json object
     * @return 
     */
    public JSONObject getJsonObject() {
        // 1. Get data in form of json object from parent object (Cow)
        JSONObject jsonObject=super.getJsonObject();
        try
        {
            // 2. Append data stored in this object to the json object obtained from parent object
            jsonObject.put("type","dam");
            jsonObject.put("embryoNumber",((embryoNumber==null) ? "":embryoNumber));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jsonObject;
    }
}