package org.cgiar.ilri.mistro.farmer.carrier;

import java.util.Calendar;
import java.util.Date;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * This is the carrier class for Cow data.
 * @author Jason Rogena <j.rogena@cgiar.org>
 */
public class Cow {
    public static final String SEX_MALE = "Male";
    public static final String SEX_FEMALE = "Female";
    public static final String AGE_TYPE_DAY = "Days";
    public static final String AGE_TYPE_WEEK = "Weeks";
    public static final String AGE_TYPE_MONTH = "Months";
    public static final String AGE_TYPE_YEAR = "Years";
    public static final String MODE_ADULT_COW_REGISTRATION = "adultCowRegistration";
    public static final String MODE_BORN_CALF_REGISTRATION = "bornCalfRegistration";
    public static final String SERVICE_TYPE_BULL = "Bull";
    public static final String SERVICE_TYPE_AI = "Artificial Insemination";
    public static final String SERVICE_TYPE_ET = "Embryo Transfer";
    
    private final String DEFAULT_DOB = "0000-00-00 00:00:00";
    
    private String name;
    private String earTagNumber;
    private String dateOfBirth;
    private int age;
    private String ageType;
    private String[] breeds;
    private String sex;
    private String[] deformities;
    private Sire sire;
    private Dam dam;
    private String countryOfOrigin;
    private boolean isNotDamOrSire;
    private String mode;
    private String serviceType;
    private String otherDeformity;
    private String piggyBack;
    private Date dateOfBirthDate;
    private MilkProduction[] milkProduction;
    private Event[] events;
    private String dateAdded;

    /**
     * Constructor for the Cow class.
     * Use this constructor if you don't have a json object that can be used to
     * initialize the object. Otherwise, use the Cow(JSONObject) constructor.
     * 
     * @param isNotDamOrSire Set to <true> if the cow is not a sire or dam. Be careful how 
     *                          you initialize this variable or you a stackOverflow error might
     *                          be thrown at you.
     */
    public Cow(boolean isNotDamOrSire) {
        name = "";
        earTagNumber = "";
        dateOfBirth = "";
        age = -1;
        ageType = "";
        breeds = new String[0];
        sex = "";
        deformities = new String[0];
        this.isNotDamOrSire = isNotDamOrSire;
        if (isNotDamOrSire)//LOL, brings StackOverflowError if you init sire object inside sire object
        {
            sire = new Sire();
            dam = new Dam();
        }
        mode = "";
        countryOfOrigin = "";
        serviceType = "";
        otherDeformity = "";
        piggyBack = "";
        dateAdded = "";
    }
    
    /**
     * Constructor for the Cow class.
     * Use this constructor if you have data for the cow encoded in a JSONObject object.
     * Otherwise, use the Cow(boolean isNotDamOrSire) constructor
     * 
     * @param cowJSONObject The cow data encoded as a json object
     */
    public Cow(JSONObject cowJSONObject){
        try {
            name = cowJSONObject.getString("name");
            earTagNumber = cowJSONObject.getString("ear_tag_number");
            dateOfBirth = cowJSONObject.getString("date_of_birth");
            if(cowJSONObject.getString("age")!=null && cowJSONObject.getString("age").trim().length() > 0)
                System.out.println(cowJSONObject.getString("age"));
                age = Integer.parseInt(cowJSONObject.getString("age"));
            ageType = cowJSONObject.getString("age_type");
            breeds = new String[0];
            sex = cowJSONObject.getString("sex");
            deformities = new String[0];
            this.isNotDamOrSire = true;
            if (isNotDamOrSire)//LOL, brings StackOverflowError if you init sire object inside sire object
            {
                sire = new Sire();
                dam = new Dam();
            }
            mode = "";
            countryOfOrigin = "";
            serviceType = cowJSONObject.getString("service_type");
            otherDeformity = "";
            piggyBack = "";
            
            JSONArray milkProductionArray = cowJSONObject.getJSONArray("milk_production");
            if(milkProductionArray!=null && sex.equals(SEX_FEMALE)){
                milkProduction = new MilkProduction[milkProductionArray.length()];
                for(int i = 0; i < milkProductionArray.length(); i++){
                    milkProduction[i] = new MilkProduction(milkProductionArray.getJSONObject(i));
                }
            }
            else{
                milkProduction = new MilkProduction[0];
            }
            
            JSONArray eventsArray = cowJSONObject.getJSONArray("cow_events");
            if(eventsArray!=null){
                events = new Event[eventsArray.length()];
                for(int i = 0; i< eventsArray.length(); i++ ){
                    events[i] = new Event(eventsArray.getJSONObject(i));
                }
            }
            else{
                events = new Event[0];
            }
            
            dateAdded = cowJSONObject.getString("date_added");
        } 
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEarTagNumber(String earTagNumber) {
        this.earTagNumber = earTagNumber;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirthDate = dateOfBirth;
        Calendar calendar  = Calendar.getInstance();
        calendar.setTime(dateOfBirth);
        StringBuffer dateStringBuffer = new StringBuffer();
        dateStringBuffer.append(calendar.get(Calendar.DATE)).append("/");
        dateStringBuffer.append(calendar.get(Calendar.MONTH)+1).append("/");
        dateStringBuffer.append(calendar.get(Calendar.YEAR));
        this.dateOfBirth = dateStringBuffer.toString();
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAgeType(String ageType) {
        this.ageType = ageType;
    }

    public void setBreeds(String[] breeds) {
        this.breeds = breeds;
    }

    public void setPiggyBack(String piggyBack) {
        this.piggyBack = piggyBack;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setOtherDeformity(String otherDeformity) {
        this.otherDeformity = otherDeformity;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setDeformities(String[] deformities) {
        this.deformities = deformities;
    }

    public void setSire(Sire sire) {
        this.sire = sire;
    }

    public void setDam(Dam dam) {
        this.dam = dam;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getName() {
        return name;
    }

    public String getMode() {
        return mode;
    }

    public String getEarTagNumber() {
        return earTagNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
    
    public long getDateOfBirthMilliseconds() {
        if(dateOfBirthDate != null)
            return dateOfBirthDate.getTime();
        else return -1;
    }
    
    public long getAgeMilliseconds(){
        Date today = new Date();
        
        long ageFromDOB = 0;
        if(this.dateOfBirth.length()>0 && !this.dateOfBirth.equals(DEFAULT_DOB)){
            ageFromDOB = today.getTime() - convertStringToDate(this.dateOfBirth).getTime();
        }
        else{
            System.out.println("Date of birth is either null or default");
        }
            
        
        long ageFromAge = 0;
        
        long ageUnits = 0;
        
        if(ageType.equals(AGE_TYPE_DAY)) ageUnits = 86400000l;
        else if(ageType.equals(AGE_TYPE_MONTH)) ageUnits = 86400000l * 30;
        else if(ageType.equals(AGE_TYPE_YEAR)) ageUnits = 86400000l * 365;
        
        ageFromAge = age * ageUnits;
        
        long addToAge = today.getTime() - convertStringToDate(this.dateAdded).getTime();
        
        ageFromAge = ageFromAge + addToAge;
        
        System.out.println("Age of cow is "+String.valueOf(this.age)+" "+this.ageType);
        System.out.println("DOB of cow is "+this.dateOfBirth);
        
        if(ageFromAge > ageFromDOB){
            System.out.println("Age more feasibly than date of birth. Using age");
            System.out.println("Age in milliseconds = "+String.valueOf(ageFromAge));
            System.out.println("Alternate age in milliseconds = "+String.valueOf(ageFromDOB));
            return ageFromAge;
        }
        else{
            System.out.println("DOB more feasibly than age. Using DOB");
            System.out.println("Age in milliseconds = "+String.valueOf(ageFromDOB));
            System.out.println("Alternate age in milliseconds = "+String.valueOf(ageFromAge));
            return ageFromDOB;
        }
    }
    
    private Date convertStringToDate(String date){
        //takes date of type yyyy-MM-dd hh:mm:ss
        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
        c.set(Calendar.MONTH, Integer.parseInt(date.substring(5, 7)) - 1);
        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(8, 10)));
        
        return c.getTime();
    }

    public int getAge() {
        return age;
    }
    
    public String getDateAdded(){
        return dateAdded;
    }
    
    public void setDateAdded(String dateAdded){
        this.dateAdded = dateAdded;
    }

    public String getPiggyBack() {
        return piggyBack;
    }

    public String getAgeType() {
        return ageType;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getOtherDeformity() {
        return otherDeformity;
    }

    public String[] getBreeds() {
        return breeds;
    }

    public String getSex() {
        return sex;
    }

    public String[] getDeformities() {
        return deformities;
    }

    public Sire getSire() {
        return sire;//TODO: handle nullpointerexception
    }

    public Dam getDam() {
        return dam;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }
    
    public MilkProduction[] getMilkProduction(){
        return milkProduction;
    }
    
    public Event[] getEvents(){
        return events;
    }

    /**
     * Returns all the data stored in this carrier object as a json object.
     * May include piggyback data used during calf and acquired cow registration.
     * 
     * @return JSONObject
     */
    public JSONObject getJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", ((name == null) ? "" : name));
            jsonObject.put("earTagNumber", ((earTagNumber == null) ? "" : earTagNumber));
            jsonObject.put("dateOfBirth", ((dateOfBirth == null) ? "" : dateOfBirth));
            jsonObject.put("age", age);
            jsonObject.put("ageType", ageType);
            JSONArray breedJsonArray = new JSONArray();
            if(breeds!=null){
                for (int i = 0; i < breeds.length; i++) {
                    breedJsonArray.put(i, breeds[i]);
                }
                jsonObject.put("breeds", breedJsonArray);
            }
            else{
                jsonObject.put("breeds", new JSONArray());
            }
            
            jsonObject.put("sex", sex);
            JSONArray deformityJsonArray = new JSONArray();
            if(deformities!=null){
                for (int i = 0; i < deformities.length; i++) {
                    deformityJsonArray.put(i, deformities[i]);
                }
                jsonObject.put("deformities", deformityJsonArray);
            }
            else{
                jsonObject.put("deformities", new JSONArray());
            }
            
            jsonObject.put("mode", ((mode == null) ? "" : mode));
            jsonObject.put("serviceType", ((serviceType == null) ? "" : serviceType));
            jsonObject.put("otherDeformity", ((otherDeformity == null) ? "" : otherDeformity));
            jsonObject.put("countryOfOrigin", ((countryOfOrigin == null) ? "" : countryOfOrigin));
            if (isNotDamOrSire) {
                jsonObject.put("type", "cow");
                if(sire == null) jsonObject.put("sire",  "");
                else jsonObject.put("sire",sire.getJsonObject());
                
                if(dam == null )jsonObject.put("dam", "");
                else jsonObject.put("dam", dam.getJsonObject());
            }
            if(piggyBack!=null && piggyBack.length()>0) {
                jsonObject.put("piggyBack",piggyBack);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
