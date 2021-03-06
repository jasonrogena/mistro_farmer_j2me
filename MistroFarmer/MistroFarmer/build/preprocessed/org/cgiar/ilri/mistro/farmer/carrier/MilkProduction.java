/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.carrier;

import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * This a carrier class for data on milk production.
 * 
 * @author Jason Rogena <j.rogena@cgiar.org>
 */
public class MilkProduction {
   
    public static final String QUANTITY_TYPE_KGS = "KGs";
    public static final String QUANTITY_TYPE_LITRES = "Litres";
    public static final int MAX_MILK_PRODUCTION_DAYS = 15;
    
    private String time;
    private int quantity;
    private String date;
    private String quantityType;

    public MilkProduction(JSONObject jSONObject) {
        try {
            time = jSONObject.getString("time");
            if(jSONObject.getString("quantity")!=null && jSONObject.getString("quantity").trim().length() > 0){
                quantity = Integer.parseInt(jSONObject.getString("quantity"));
            }
            date = jSONObject.getString("date");
            quantityType = jSONObject.getString("quantity_type");
        } 
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    public String getDate(){
        return date;
    }
    
    public String getTime(){
        return time;
    }
    
    public int getQuantity(){
        return quantity;
    }
    
    public String getQuantityType(){
        return quantityType;
    }
    
}
