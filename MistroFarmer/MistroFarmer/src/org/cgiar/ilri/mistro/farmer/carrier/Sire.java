package org.cgiar.ilri.mistro.farmer.carrier;

import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * This is a carrier class for data on a sire.
 * This is a subclass of the Cow class.
 * 
 * @author Jason Rogena <j.rogena@cgiar.org>
 */
public class Sire extends Cow {
    private String strawNumber;

    public Sire()
    {
        super(false);
        setSex(SEX_MALE);
        strawNumber="";
    }

    public void setStrawNumber(String strawNumber) {
        this.strawNumber = strawNumber;
    }

    public String getStrawNumber() {
        return strawNumber;
    }
    
    /**
     * This method returns a JSON object containing data on this sire.
     * 
     * @return A JSON object containing data on the sire or an empty JSON object.
     */
    public JSONObject getJsonObject() {
        JSONObject jsonObject=super.getJsonObject();
        try
        {
            jsonObject.put("type","sire");
            jsonObject.put("strawNumber",((strawNumber==null) ? "":strawNumber));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jsonObject;
    }
}