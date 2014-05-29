/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.carrier;

import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 *
 * @author jason
 */
public class EventConstraint {
    
    private final String UNIT_DAY = "Days";
    private final String UNIT_MONTH = "Months";
    private final String UNIT_YEAR = "Years";
    
    public static final String CONSTRAINT_MATURITY = "Maturity";
    public static final String CONSTRAINT_BIRTH_TO_LACTATION = "MaxTimeBirthLactation";
    public static final String CONSTRAINT_MILKING = "Milking";
    public static final String CONSTRAINT_CALVING = "Calving";
    
    private int id;
    private String event;
    private int time;
    private String timeUnits;
    
    public EventConstraint(JSONObject jsonObject){
        System.out.println(jsonObject.toString());
        try {
            id = Integer.parseInt(jsonObject.getString("id"));
            event = jsonObject.getString("event");
            time = Integer.parseInt(jsonObject.getString("time"));
            timeUnits = jsonObject.getString("time_units");
        } 
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    public long getTimeMilliseconds(){
        long result = 0;
        
        long timeUnits = 0;
        if(this.timeUnits.equals(UNIT_DAY)) timeUnits = 86400000l;
        else if(this.timeUnits.equals(UNIT_MONTH)) timeUnits = 86400000l * 30;
        else if(this.timeUnits.equals(UNIT_YEAR)) timeUnits = 86400000l * 365;
        
        result = timeUnits * this.time;
        
        return result;
    }
    
    public String getEvent(){
        return this.event;
    }
}
