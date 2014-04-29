/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.carrier;

import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * This class is a carrier for data on an event.
 * 
 * @author Jason Rogena <j.rogena@cgiar.org>
 */
public class Event {
    public static final String BULL_OWNER_OWN = "Own bull";
    public static final String BULL_OWNER_OTHER_FARMER = "Another farmer";
    public static final String BULL_OWNER_GROUP = "A group";
    public static final int MAX_EVENT_DAYS = 15;//the maximum number of days allowed for an old event
    
    private String type;
    private String date;

    public Event(JSONObject jSONObject) {
        try {
            date = jSONObject.getString("event_date");
            type = jSONObject.getString("event_type");
        } 
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    public String getType(){
        return type;
    }
    
    public String getDate(){
        return date;
    }
}
