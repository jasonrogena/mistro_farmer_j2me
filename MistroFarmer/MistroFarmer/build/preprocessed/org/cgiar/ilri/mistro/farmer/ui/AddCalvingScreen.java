/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.Button;
import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.spinner.Spinner;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import org.cgiar.ilri.mistro.farmer.Midlet;
import org.cgiar.ilri.mistro.farmer.carrier.Cow;
import org.cgiar.ilri.mistro.farmer.carrier.Dam;
import org.cgiar.ilri.mistro.farmer.carrier.Event;
import org.cgiar.ilri.mistro.farmer.carrier.EventConstraint;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.cgiar.ilri.mistro.farmer.ui.localization.ArrayResources;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;
import org.cgiar.ilri.mistro.farmer.utils.DataHandler;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * This class creates the Add Calving Screen in the application
 * 
 * @author Jason Rogena <j.rogena@cgiar.org>
 */
public class AddCalvingScreen extends Form implements Screen, DateDialog.OnDateEnteredListener, ActionListener{

    private final Midlet midlet;
    private final Farmer farmer;
    private final int locale;
    
    private Vector validCows;
    
    private BoxLayout parentBoxLayout;
    private Command backCommand;
    private Command okayCommand;
    
    private Label cowL;
    private ComboBox cowCB;
    private Label dateL;
    private Spinner dateS;
    private Button dateB;
    private DateDialog dateDialog = null;
    private Label typeL;
    private ComboBox typeCB;
    /*private Label birthsL;
    private TextField birthsTF;*/
    
    
    public AddCalvingScreen(Midlet midlet, int locale, Farmer farmer) {
        super(Locale.getStringInLocale(locale, StringResources.calving));
        this.midlet = midlet;
        this.farmer = farmer;
        this.locale = locale;
        
        parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        
        backCommand = new Command(Locale.getStringInLocale(locale, StringResources.back));
        this.addCommand(backCommand);
        
        okayCommand = new Command(Locale.getStringInLocale(locale, StringResources.next));
        this.addCommand(okayCommand);
        
        this.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(backCommand)){
                    FertilityScreen fertilityScreen = new FertilityScreen(AddCalvingScreen.this.midlet, AddCalvingScreen.this.locale, AddCalvingScreen.this.farmer);
                    fertilityScreen.start();
                }
                else if(evt.getCommand().equals(okayCommand)){
                    if(validateInput()){
                        String[] birthTypesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.birth_types);
                        JSONObject jSONObject = new JSONObject();
                        Cow selectedCow = (Cow)validCows.elementAt(cowCB.getSelectedIndex());
                        try {
                            jSONObject.put("motherETN", selectedCow.getEarTagNumber());
                            jSONObject.put("motherName", selectedCow.getName());
                            jSONObject.put("birthType",birthTypesInEN[typeCB.getSelectedIndex()]);
                            jSONObject.put("eventType","Birth");
                            //jSONObject.put("liveBirths",birthsTF.getText());
                        } 
                        catch (JSONException ex) {
                            ex.printStackTrace();
                        }

                        if(birthTypesInEN[typeCB.getSelectedIndex()].equals("Normal")){
                            AddCalvingScreen.this.farmer.setMode(Farmer.MODE_NEW_COW_REGISTRATION);
                            Cow calf = new Cow(true);
                            calf.setMode(Cow.MODE_BORN_CALF_REGISTRATION);
                            calf.setPiggyBack(jSONObject.toString());
                            long currTime = System.currentTimeMillis();
                            long birthDate = ((Date)dateS.getValue()).getTime();
                            int days =(int)((currTime - birthDate)/86400000);
                            calf.setAge(days);
                            calf.setAgeType(Cow.AGE_TYPE_DAY);
                            calf.setDateOfBirth((Date)dateS.getValue());

                            Dam dam = new Dam();
                            dam.setEarTagNumber(selectedCow.getEarTagNumber());
                            dam.setName(selectedCow.getName());

                            calf.setDam(dam);
                            System.out.println("Size of cows before calf is appended = "+String.valueOf(AddCalvingScreen.this.farmer.getCows().length));
                            AddCalvingScreen.this.farmer.appendCow(calf);
                            System.out.println("Size of cows after calf is appended = "+String.valueOf(AddCalvingScreen.this.farmer.getCows().length));
                            CowRegistrationScreen cowRegistrationScreen = new CowRegistrationScreen(AddCalvingScreen.this.midlet, AddCalvingScreen.this.locale, AddCalvingScreen.this.farmer, AddCalvingScreen.this.farmer.getCows().length-1, AddCalvingScreen.this.farmer.getCows().length);
                            cowRegistrationScreen.show();
                        }
                        else if(birthTypesInEN[typeCB.getSelectedIndex()].equals("Still")){
                            Date date = (Date) dateS.getValue();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            String dateString = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.MONTH)+1)+"/"+String.valueOf(calendar.get(Calendar.YEAR));
                            try {
                                jSONObject.put("date", dateString);//TODO:implement date spinner
                                jSONObject.put("birth_type","Still");
                                jSONObject.put("mobileNo",AddCalvingScreen.this.farmer.getMobileNumber());
                                jSONObject.put("cowName", selectedCow.getName());
                                jSONObject.put("cowEarTagNumber", selectedCow.getEarTagNumber());
                                
                                Thread thread = new Thread(new EventHandler(jSONObject));
                                thread.run();
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
        
        cowL = new Label(Locale.getStringInLocale(locale, StringResources.cow));
        setLabelStyle(cowL);
        this.addComponent(cowL);
        
        String [] cowNames = getValidCows();
        cowCB = new ComboBox(cowNames);
        setComponentStyle(cowCB, true);
        cowCB.setRenderer(new MistroListCellRenderer(cowNames));
        this.addComponent(cowCB);
        
        dateL = new Label(Locale.getStringInLocale(locale, StringResources.date));
        setLabelStyle(dateL);
        this.addComponent(dateL);
        
        dateS = Spinner.createDate(System.currentTimeMillis() - (86400000l*15), System.currentTimeMillis() + 86400000l, System.currentTimeMillis() + 86400000l, '/', Spinner.DATE_FORMAT_DD_MM_YYYY);
        //setComponentStyle(dateS, true);
        //dateS.getSelectedStyle().setFgColor(0x2ecc71);
        //this.addComponent(dateS);
        dateB = new Button(Locale.getStringInLocale(locale, StringResources.click_to_set_date));
        setComponentStyle(dateB, true);
        dateB.addActionListener(this);
        this.addComponent(dateB);
        
        typeL = new Label(Locale.getStringInLocale(locale, StringResources.type_of_birth));
        setLabelStyle(typeL);
        this.addComponent(typeL);
        
        typeCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.birth_types));
        setComponentStyle(typeCB, true);
        typeCB.setRenderer(new MistroListCellRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.birth_types)));
        this.addComponent(typeCB);
        
        /*birthsL = new Label(Locale.getStringInLocale(locale, StringResources.no_f_cow_births));
        setLabelStyle(birthsL);
        this.addComponent(birthsL);
        
        birthsTF = new TextField();
        setComponentStyle(birthsTF, false);
        birthsTF.setConstraint(TextField.NUMERIC);
        birthsTF.setInputModeOrder(new String[] {"123"});
        this.addComponent(birthsTF);*/
    }
    
    private void setLabelStyle(Label label){
        label.getStyle().setMargin(10, 0, 10, 0);
        label.getSelectedStyle().setMargin(10, 0, 10,0);
    }
    
    private void setComponentStyle(Component component, boolean isFocusable){
        component.getStyle().setMargin(5, 0, 0, 0);
        component.getSelectedStyle().setMargin(5, 0, 0, 0);
        if(isFocusable){
            component.getSelectedStyle().setBgColor(0x2ecc71);
        }
    }
    
    private String[] getValidCows(){
        Cow[] allCows = farmer.getCows();
        validCows = new Vector(allCows.length);
        if(allCows!=null){
            for(int i = 0; i < allCows.length; i++){
                if(allCows[i].getSex().equals(Cow.SEX_FEMALE)){// && allCows[i].getEarTagNumber()!=null && allCows[i].getEarTagNumber().trim().length()>0){
                    validCows.addElement(allCows[i]);
                }
            }
            
            String[] cowNames = new String[validCows.size()];
            for(int i = 0 ; i < validCows.size(); i++){
                Cow currentCow = (Cow) validCows.elementAt(i);
                if(currentCow.getName()!=null && currentCow.getName().trim().length() > 0){
                    cowNames[i] = currentCow.getEarTagNumber()+" ("+currentCow.getName()+")";
                }
                else{
                    cowNames[i] = currentCow.getEarTagNumber();
                }
            }
            return cowNames;
        }
        String[] placibo = {" "};
        return placibo;
    }
    
    /**
     * This method validates the input in the screen
     * 
     * @return <true> if all input is valid. Otherwise <false> is returned
     */
    private boolean validateInput(){
        final InformationDialog infoDialog = new InformationDialog(locale, false);
        
        /*if(birthsTF.getText() == null || birthsTF.getText().trim().length()==0){
            infoDialog.setText(Locale.getStringInLocale(locale, StringResources.enter_no_f_cow_births));
            birthsTF.requestFocus();
            infoDialog.show();
            return false;
        }*/
        
        if(validateDate()!=null){
            infoDialog.setText(validateDate());
            dateS.requestFocus();
            infoDialog.show();
            return false;
        }
        
        Cow selectedCow = (Cow)validCows.elementAt(cowCB.getSelectedIndex());
        EventConstraint[] constraints = farmer.getEventConstraints();
        for(int i = 0; i < constraints.length; i++){
            EventConstraint currConstraint = constraints[i];
            
            if(currConstraint.getEvent().equals(EventConstraint.CONSTRAINT_CALVING)){
                if(selectedCow.getAgeMilliseconds()<currConstraint.getTimeMilliseconds()){
                    infoDialog.setText(Locale.getStringInLocale(locale, StringResources.cow_too_young));
                    cowCB.requestFocus();
                    infoDialog.show();
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * This method checks if the date input in screen is valid.
     * Date should not be older than 30 days old or in the future.
     * 
     * @return <true> is returned if date is valid. Otherwise, <false> is returned.
     */
    private String validateDate(){
        Date dateSelected  = (Date) dateS.getValue();
        long currentTime = System.currentTimeMillis();
        
        long timeDiffDays = (currentTime - dateSelected.getTime())/86400000;
        
        if(timeDiffDays > Event.MAX_EVENT_DAYS){
            return Locale.getStringInLocale(locale, StringResources.milk_data_too_old);
        }
        else if(timeDiffDays < 0){
            return Locale.getStringInLocale(locale, StringResources.date_in_future);
        }
        
        return null;
    }
    
    /**
     * Call this method when you want this screen to show for the first time.
     */
    public void start() {
        this.show();
    }

    public void destroy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void pause() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * This method is called by the EventHandler class when a response is gotten from the server.
     * 
     * @param message Response message from the server
     */
    private void reactToServerResponse(String message){
        
       System.out.println("response gotten for calf");
       if(message == null){
           final InformationDialog infoDialog = new InformationDialog(Locale.getStringInLocale(locale, StringResources.error), locale, false);
           infoDialog.setText(Locale.getStringInLocale(locale, StringResources.problem_connecting_to_server));
           infoDialog.show();
       }
       else if(message.equals(DataHandler.ACKNOWLEDGE_OK)){
            final InformationDialog infoDialog = new InformationDialog(Locale.getStringInLocale(locale, StringResources.success), locale, false);
            infoDialog.setText(Locale.getStringInLocale(locale, StringResources.information_successfully_sent_to_server));
            infoDialog.show();
       }
       else{
           final InformationDialog infoDialog = new InformationDialog(Locale.getStringInLocale(locale, StringResources.error), locale, false);
           infoDialog.setDialogType(Dialog.TYPE_ERROR);//TODO: check if this works, InformationDialog automatically set to TYPE_INFO
           infoDialog.setText(Locale.getStringInLocale(locale, StringResources.something_went_wrong_try_again));
           infoDialog.show();
       }
    }

    public void dateSelected(Spinner spinner, Date date) {
        dateB.setText(DateDialog.dateToString((Date)dateS.getValue()));
    }

    public void actionPerformed(ActionEvent ae) {
        if(ae.getComponent().equals(dateB)){
            if(dateDialog == null){
                dateDialog = new DateDialog(locale, dateS, this);
                dateDialog.setText(Locale.getStringInLocale(locale, StringResources.enter_date));
            }
            
            dateDialog.show();
        }
    }
    
    /**
     * This method creates a thread that sends the calving data obtained from this screen to the server.
     */
    private class EventHandler implements Runnable{
        
        private JSONObject jSONObject;

        public EventHandler(JSONObject jSONObject) {
            this.jSONObject = jSONObject;
        }

        public void run() {
            String response = DataHandler.sendDataToServer(jSONObject, DataHandler.FARMER_ADD_COW_EVENT_URL);
            reactToServerResponse(response);
        }
        
    }

}
