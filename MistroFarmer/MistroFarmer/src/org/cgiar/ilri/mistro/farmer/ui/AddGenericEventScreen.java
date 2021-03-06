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
 *
 * @author jason
 */
public class AddGenericEventScreen extends Form implements Screen, ActionListener, DateDialog.OnDateEnteredListener{

    private final Midlet midlet;
    private final int locale;
    private final Farmer farmer;
    private final String eventType;
    
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
    private Label remarksL;
    private TextArea remarksTA;
    
    public AddGenericEventScreen(Midlet midlet, int locale, Farmer farmer, String eventType) {
        super(Locale.getStringInLocale(locale, StringResources.add_an_event));
        
        this.midlet = midlet;
        this.locale = locale;
        this.farmer = farmer;
        this.eventType = eventType;
        
        String[] eventTypes = Locale.getStringArrayInLocale(locale, ArrayResources.cow_event_types);
        String[] eventTypesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.cow_event_types);
        for(int i = 0; i < eventTypes.length; i++){
            if(eventTypesInEN[i].equals(eventType)){
                this.setTitle(eventTypes[i]);
            }
        }
        
        parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        
        backCommand = new Command(Locale.getStringInLocale(locale, StringResources.back));
        this.addCommand(backCommand);
        
        okayCommand = new Command(Locale.getStringInLocale(locale, StringResources.okay));
        this.addCommand(okayCommand);
        
        this.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(backCommand)){
                    EventsScreen eventsScreen = new EventsScreen(AddGenericEventScreen.this.midlet, AddGenericEventScreen.this.locale, AddGenericEventScreen.this.farmer);
                    eventsScreen.start();
                }
                else if(evt.getCommand().equals(okayCommand)){
                    if(validateInput()){
                        Cow selectedCow = (Cow)validCows.elementAt(cowCB.getSelectedIndex());
                        Date date = (Date) dateS.getValue();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        String dateString = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.MONTH)+1)+"/"+String.valueOf(calendar.get(Calendar.YEAR));
                        JSONObject jSONObject = new JSONObject();
                        try {
                            jSONObject.put("mobileNo", AddGenericEventScreen.this.farmer.getMobileNumber());
                            jSONObject.put("cowEarTagNumber", selectedCow.getEarTagNumber());
                            jSONObject.put("cowName", selectedCow.getName());
                            jSONObject.put("date", dateString);
                            jSONObject.put("eventType",AddGenericEventScreen.this.eventType);
                            jSONObject.put("remarks", remarksTA.getText());
                            Thread thread = new Thread(new EventHandler(jSONObject));
                            thread.run();
                        } 
                        catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                    
                }
            }
        });
        
        cowL = new Label(Locale.getStringInLocale(locale, StringResources.cow));
        setLabelStyle(cowL);
        this.addComponent(cowL);
        
        String[] cowNames = getValidCows();
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
        if(evt.getComponent().equals(dateB)){
            if(dateDialog == null){
                dateDialog = new DateDialog(locale, dateS, this);
                dateDialog.setText(Locale.getStringInLocale(locale, StringResources.enter_date));
            }
            
            dateDialog.show();
        }
    }
    
    private String[] getValidCows(){
        Cow[] allCows = farmer.getCows();
        validCows = new Vector(allCows.length);
        for(int i = 0; i < allCows.length; i++){
            //if(allCows[i].getEarTagNumber()!=null && allCows[i].getEarTagNumber().trim().length()>0){
            validCows.addElement(allCows[i]);
            //}
        }
        
        String[] cowNames = new String[validCows.size()];
        for(int i =0; i < validCows.size(); i++){
            Cow currentCow = (Cow) validCows.elementAt(i);
            if(currentCow.getName()!= null && currentCow.getName().trim().length() > 0)
                cowNames[i]=currentCow.getEarTagNumber()+" ("+currentCow.getName()+")";
            else
                cowNames[i]=currentCow.getEarTagNumber();
        }
        
        return cowNames;
    }
    
    private boolean validateInput(){
        
        final InformationDialog infoDialog = new InformationDialog(locale, false);
        
        if(validateDate()!=null){
            infoDialog.setText(validateDate());
            dateS.requestFocus();
            infoDialog.show();
            return false;
        }
        
        else if(eventType.equals("Start of Lactation")){
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
        }
        
        else if(eventType.equals("Dry Off")){
            Cow selectedCow = (Cow)validCows.elementAt(cowCB.getSelectedIndex());
            EventConstraint[] constraints = farmer.getEventConstraints();
            for(int i = 0; i < constraints.length; i++){
                EventConstraint currConstraint = constraints[i];

                if(currConstraint.getEvent().equals(EventConstraint.CONSTRAINT_MILKING)){
                    if(selectedCow.getAgeMilliseconds()<currConstraint.getTimeMilliseconds()){
                        infoDialog.setText(Locale.getStringInLocale(locale, StringResources.cow_too_young));
                        cowCB.requestFocus();
                        infoDialog.show();
                        return false;
                    }
                }
            }
        }
        
        else if(eventType.equals("Signs of Heat")){
            Cow selectedCow = (Cow)validCows.elementAt(cowCB.getSelectedIndex());
            EventConstraint[] constraints = farmer.getEventConstraints();
            for(int i = 0; i < constraints.length; i++){
                EventConstraint currConstraint = constraints[i];

                if(currConstraint.getEvent().equals(EventConstraint.CONSTRAINT_MATURITY)){
                    if(selectedCow.getAgeMilliseconds()<currConstraint.getTimeMilliseconds()){
                        infoDialog.setText(Locale.getStringInLocale(locale, StringResources.cow_too_young));
                        cowCB.requestFocus();
                        infoDialog.show();
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private String validateDate(){
        Date dateSelected  = (Date) dateS.getValue();
        long currentTime = System.currentTimeMillis();
        
        long timeDiffDays = (currentTime - dateSelected.getTime())/86400000;
        
        if(timeDiffDays > Event.MAX_EVENT_DAYS){
            return Locale.getStringInLocale(locale, StringResources.event_too_old);
        }
        else if(timeDiffDays < 0){
            return Locale.getStringInLocale(locale, StringResources.date_in_future);
        }
        
        return null;
    }
    
    private void reactToServerResponse(String response){
        final InformationDialog infoDialog = new InformationDialog(locale, false);
        
        if(response == null){
            infoDialog.setText(Locale.getStringInLocale(locale, StringResources.problem_connecting_to_server));
            infoDialog.show();
        }
        else if(response.equals(DataHandler.ACKNOWLEDGE_OK)){
            farmer.update();
            infoDialog.setText(Locale.getStringInLocale(locale, StringResources.information_successfully_sent_to_server));
            infoDialog.show();
            infoDialog.addCommandListener(new ActionListener() {

                public void actionPerformed(ActionEvent ae) {
                    if(ae.getCommand().equals(infoDialog.getBackCommand())){
                        EventsScreen eventsScreen = new EventsScreen(midlet, locale, farmer);
                        eventsScreen.start();
                    }
                }
            });
        }
        else{
            infoDialog.setText(Locale.getStringInLocale(locale, StringResources.problem_in_data_sent_en));
            infoDialog.show();
        }
    }

    public void dateSelected(Spinner spinner, Date date) {
        dateB.setText(dateDialog.dateToString((Date)dateS.getValue()));
    }
    
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
