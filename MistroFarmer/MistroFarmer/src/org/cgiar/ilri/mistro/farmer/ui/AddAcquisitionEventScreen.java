/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.ui;

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
import org.cgiar.ilri.mistro.farmer.Midlet;
import org.cgiar.ilri.mistro.farmer.carrier.Cow;
import org.cgiar.ilri.mistro.farmer.carrier.Event;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.cgiar.ilri.mistro.farmer.ui.localization.ArrayResources;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * This Class creates the Add Cow Acquisition Screen
 * 
 * @author Jason Rogena <j.rogena@cgair.org>
 */
public class AddAcquisitionEventScreen extends Form implements Screen, ActionListener{

    private final Midlet midlet;
    private final int locale;
    private final Farmer farmer;
    
    private BoxLayout parentBoxLayout;
    private Command backCommand;
    private Command okayCommand;
    
    private Label dateL;
    private Spinner dateS;
    private Label remarksL;
    private TextArea remarksTA;
    
    public AddAcquisitionEventScreen(Midlet midlet, int locale, Farmer farmer) {
        super(Locale.getStringInLocale(locale, StringResources.acquisition));
        
        this.midlet = midlet;
        this.locale = locale;
        this.farmer = farmer;
        
        parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        
        backCommand = new Command(Locale.getStringInLocale(locale, StringResources.back));
        this.addCommand(backCommand);
        
        okayCommand = new Command(Locale.getStringInLocale(locale, StringResources.next));
        this.addCommand(okayCommand);
        
        this.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(backCommand)){
                    EventsScreen eventsScreen = new EventsScreen(AddAcquisitionEventScreen.this.midlet, AddAcquisitionEventScreen.this.locale, AddAcquisitionEventScreen.this.farmer);
                    eventsScreen.start();
                }
                else if(evt.getCommand().equals(okayCommand)){
                    if(validateInput()){
                        Date date = (Date) dateS.getValue();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        String dateString = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.MONTH)+1)+"/"+String.valueOf(calendar.get(Calendar.YEAR));
                        JSONObject jSONObject = new JSONObject();
                        try {
                            jSONObject.put("mobileNo", AddAcquisitionEventScreen.this.farmer.getMobileNumber());
                            jSONObject.put("date", dateString);
                            jSONObject.put("eventType","Acquisition");
                            jSONObject.put("remarks", remarksTA.getText());
                            
                            AddAcquisitionEventScreen.this.farmer.setMode(Farmer.MODE_NEW_COW_REGISTRATION);
                            Cow newCow = new Cow(true);
                            newCow.setMode(Cow.MODE_ADULT_COW_REGISTRATION);
                            newCow.setPiggyBack(jSONObject.toString());
                            
                            AddAcquisitionEventScreen.this.farmer.appendCow(newCow);
                            CowRegistrationScreen cowRegistrationScreen = new CowRegistrationScreen(AddAcquisitionEventScreen.this.midlet, AddAcquisitionEventScreen.this.locale, AddAcquisitionEventScreen.this.farmer, AddAcquisitionEventScreen.this.farmer.getCows().length-1, AddAcquisitionEventScreen.this.farmer.getCows().length);
                            cowRegistrationScreen.start();
                        } 
                        catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
        
        dateL = new Label(Locale.getStringInLocale(locale, StringResources.date));
        setLabelStyle(dateL);
        this.addComponent(dateL);
        
        dateS = Spinner.createDate(System.currentTimeMillis() - (31536000730l*50), System.currentTimeMillis(), System.currentTimeMillis(), '/', Spinner.DATE_FORMAT_DD_MM_YYYY);
        setComponentStyle(dateS, true);
        dateS.getSelectedStyle().setFgColor(0x2ecc71);
        this.addComponent(dateS);
        
        remarksL = new Label(Locale.getStringInLocale(locale, StringResources.remarks));
        setLabelStyle(remarksL);
        this.addComponent(remarksL);
        
        remarksTA = new TextArea();
        setComponentStyle(remarksTA, true);
        remarksTA.setRows(4);
        this.addComponent(remarksTA);
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

    public void actionPerformed(ActionEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * This method validates the input in the screen
     * 
     * @return <true> if all input is valid. Otherwise <false> is returned
     */
    private boolean validateInput(){
        final InformationDialog infoDialog = new InformationDialog(locale, false);
        
        if(validateDate()!=null){
            infoDialog.setText(validateDate());
            dateS.requestFocus();
            infoDialog.show();
            return false;
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
    
}
