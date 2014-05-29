package org.cgiar.ilri.mistro.farmer.carrier;

import org.cgiar.ilri.mistro.farmer.ui.FarmerRegistrationScreen;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.utils.DataHandler;
import org.cgiar.ilri.mistro.farmer.utils.ResponseListener;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * This is a carrier class for information on a farmer.
 * 
 * @author Jason Rogena <j.rogena@cgiar.org>
 */
public class Farmer {
    public static final String MODE_INITIAL_REGISTRATION = "initialRegistration";
    public static final String MODE_NEW_COW_REGISTRATION = "newCowRegistration";
    private String fullName;
    private String extensionPersonnel;
    private String mobileNumber;
    private Cow[] cows;
    private String longitude;
    private String latitude;
    private String simCardSN;
    private String mode;
    private String preferreLocale;
    
    private EventConstraint[] eventConstraints;

    /**
     * This is a constructor for the Farmer class.
     * Use this constructor of you do not have data on the 
     * farmer encoded as a JSONObject.
     */
    public Farmer()
    {
        fullName="";
        extensionPersonnel="";
        mobileNumber="";
        longitude="";
        latitude="";
        simCardSN ="";
        mode = "";
        preferreLocale = "";
    }
    
    /**
     * This is a constructor for the Farmer class.
     * Use this constructor if you have data on the farmer
     * encoded as a JSONObject
     * 
     * @param farmerJSONObject JSON object containing data on the farmer
     */
    public Farmer(JSONObject farmerJSONObject){
        try {
            fullName = farmerJSONObject.getString("name");
            extensionPersonnel="";
            mobileNumber=farmerJSONObject.getString("mobile_no");
            longitude=farmerJSONObject.getString("gps_longitude");
            latitude=farmerJSONObject.getString("gps_latitude");
            simCardSN=farmerJSONObject.getString("sim_card_sn");
            preferreLocale = farmerJSONObject.getString("pref_locale");
            
            JSONArray cowsJSONArray = farmerJSONObject.getJSONArray("cows");
            cows = new Cow[cowsJSONArray.length()];
            for(int i=0; i < cows.length; i++){
                cows[i] = new Cow(cowsJSONArray.getJSONObject(i));
            }
            
            JSONArray constraintJSONArray = farmerJSONObject.getJSONArray("event_constraints");
            eventConstraints = new EventConstraint[constraintJSONArray.length()];
            for(int i = 0; i < eventConstraints.length; i++){
                eventConstraints[i] = new EventConstraint(constraintJSONArray.getJSONObject(i));
            }
            
            mode = "";
        } 
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Get updated data on the farmer from the server
     */
    public void update(){
        Thread thread = new Thread(new Updater());
        thread.run();
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setExtensionPersonnel(String extensionPersonnel) {
        this.extensionPersonnel = extensionPersonnel;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
    
    public void setPreferredLanguage(String language){
        this.preferreLocale = Locale.getLocaleString(language);
    }
    
    public String getPreferredLanguage(){
        return Locale.getLanguage(preferreLocale);
    }

    /**
     * Get the nuber of cows registered under this farmer
     * 
     * @return int The number of cows owned by the farmer.
     */
    public int getCowNumber() {
        if(cows!=null){
            return cows.length;
        }
        else {
            return 0;
        }
    }

    public String getMode() {
        return this.mode;
    }
    
    public EventConstraint[] getEventConstraints(){
        return this.eventConstraints;
    }

    public void setCowNumber(int number) {
        this.cows = new Cow[number];
        for (int i=0;i<number;i++) {
            cows[i] = new Cow(true);
        }
    }
    
    /**
     * Append a new cow at the end of the list of cows owned by this farmer
     * 
     * @param newCow The cow to be appended to the cow list
     */
    public void appendCow(Cow newCow){
        if(cows!=null){
            Cow[] newCowList = new Cow[cows.length+1];
            for(int i = 0; i < cows.length; i++){
                newCowList[i] = cows[i];
            }
            newCowList[newCowList.length-1] = newCow;
            
            cows = newCowList;
        }
        else{
            cows = new Cow[1];
            cows[0] = newCow;
        }
    }
    
    /**
     * Use this method to remove the last member of the cow list.
     */
    public void unAppendCow(){
        if(cows!=null){
            Cow[] newCowList = new Cow[cows.length-1];
            for(int i = 0; i < cows.length-1; i++){
                newCowList[i]=cows[i];
            }
            cows = newCowList;
        }
    }

    /**
     * Use this method to initialize the list of cows registered under the farmer.
     * 
     * @param cows Array of cows under the farmer
     */
    public void setCows(Cow[] cows) {
        this.cows = cows;
    }
    
    /**
     * Set the index in the cow list to the provided cow.
     * 
     * @param cow The new cow object to be placed in provided index
     * @param index Position on cow list to place the cow. If this index does not lie
     *              within the size of the cow list nothing will happen.
     */
    public void setCow(Cow cow, int index) {
        if(index<cows.length) {
            cows[index] = cow;
        }
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setSimCardSN(String simCardSN) {
        this.simCardSN = simCardSN;
    }

    public String getFullName() {
        return fullName;
    }

    public String getExtensionPersonnel() {
        return extensionPersonnel;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public Cow[] getCows() {
        return cows;
    }

    /**
     * Get cow object on the provided index in the farmer's cow list
     * 
     * @param index Index on the farmer's cow list
     * 
     * @return The cow object in the provided position on the cow list or <null> if there is no cow in specified position
     */
    public Cow getCow(int index)
    {
        if(index<cows.length)
            return cows[index];
        else
            return null;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getSimCardSN() {
        return simCardSN;
    }

    /**
     * Get the JSONObject with all data on the farmer carried by this object.
     * 
     * @return A JSON object with data on the farmer or a blank json object if something goes 
     *          wrong while parsing the data into the json object.
     */
    public JSONObject getJsonObject()
    {
        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("fullName",((fullName==null) ? "":fullName));
            jsonObject.put("extensionPersonnel",((extensionPersonnel==null) ? "":extensionPersonnel));
            jsonObject.put("mobileNumber",((mobileNumber==null) ? "":mobileNumber));
            JSONArray cowsJsonArray=new JSONArray();
            if(mode.equals(MODE_INITIAL_REGISTRATION)){
                for (int i=0;i<cows.length;i++) {
                    cowsJsonArray.put(i,cows[i].getJsonObject());
                }
            }
            else if(mode.equals(MODE_NEW_COW_REGISTRATION)){
                cowsJsonArray.put(0,cows[cows.length-1].getJsonObject());
            }
            jsonObject.put("cows",cowsJsonArray);
            jsonObject.put("longitude",((longitude==null) ? "":longitude));
            jsonObject.put("latitude",((latitude==null) ? "":latitude));
            jsonObject.put("simCardSN",((simCardSN ==null) ? "": simCardSN));
            jsonObject.put("mode",((mode ==null) ? "": mode));
            jsonObject.put("preferredLocale", preferreLocale);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return  jsonObject;
    }
    
    /**
     * This method sends updated data on the farmer to the server.
     * 
     * @param responseListener Listener that will be called when response is gotten from server
     */
    public void syncWithServer(ResponseListener responseListener){
        Thread thread = new Thread(new DataSender(getJsonObject(), responseListener));
        thread.run();
    }
    
    /**
     * This class initializes a thread that sends updated farmer data to server
     */
    private class DataSender implements Runnable{
        
        private ResponseListener responseListener;
        private JSONObject farmerJSONObject;
        
        public DataSender(JSONObject farmerJSONObject, ResponseListener responseListener) {
            this.responseListener = responseListener;
            this.farmerJSONObject = farmerJSONObject;
        }
        
        public void run() {
            String message = DataHandler.sendDataToServer(farmerJSONObject, DataHandler.FARMER_REGISTRATION_URL);
            responseListener.responseGotten(Farmer.this, message);
        }
        
    }
    
    /**
     * This method is called when response is gotten from server in the Update inner class
     * 
     * @param farmerJSONObject The json object containing data on the farmer
     */
    private void actOnServerResponse(JSONObject farmerJSONObject){
        try {
            fullName = farmerJSONObject.getString("name");
            extensionPersonnel="";
            mobileNumber=farmerJSONObject.getString("mobile_no");
            longitude=farmerJSONObject.getString("gps_longitude");
            latitude=farmerJSONObject.getString("gps_latitude");
            simCardSN=farmerJSONObject.getString("sim_card_sn");
            preferreLocale = farmerJSONObject.getString("pref_locale");
            
            JSONArray cowsJSONArray = farmerJSONObject.getJSONArray("cows");
            cows = new Cow[cowsJSONArray.length()];
            for(int i=0; i < cows.length; i++){
                cows[i] = new Cow(cowsJSONArray.getJSONObject(i));
            }
            
            mode = "";
        } 
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * This class creates a thread that fetches farmer data corresponding to the provided 
     * mobile phone number from the server
     */
    private class Updater implements Runnable{
        
        public void run() {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("mobileNumber", mobileNumber);
                String response = DataHandler.sendDataToServer(jSONObject, DataHandler.FARMER_AUTHENTICATION_URL);
                if(response == null){
                    System.err.println("no response from server");
                }
                else if(response.equals(DataHandler.CODE_USER_NOT_AUTHENTICATED)) {
                    System.err.println("user not authenticated");
                }
                else{
                    JSONObject farmerJSONObject = new JSONObject(response);
                    actOnServerResponse(farmerJSONObject);
                }
                
            } 
            catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        
    }
}