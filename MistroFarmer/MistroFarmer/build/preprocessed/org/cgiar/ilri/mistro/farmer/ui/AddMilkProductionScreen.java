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
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.cgiar.ilri.mistro.farmer.carrier.MilkProduction;
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
public class AddMilkProductionScreen extends Form implements Screen, ActionListener, DateDialog.OnDateEnteredListener{
    
    private final Midlet midlet;
    private final int locale;
    private final Farmer farmer;
    private Vector validCows;
    
    private BoxLayout parentBoxLayout;
    private Command backCommand;
    private Command addCommand;
    
    private Label cowL;
    private ComboBox cowCB;
    private Label dateL;
    private Spinner dateS;
    DateDialog dateDialog = null;
    private Button dateB;
    private Label timeL;
    private ComboBox timeCB;
    private Label quantityL;
    private TextField quantityTF;
    private Label quantityTypeL;
    private ComboBox quantityTypeCB;
    
    //private Date date;
    /*private Label noTimesMilkedL;
    private TextField noTimesMilkedTF;
    private Label calfSucklingL;
    private ComboBox calfSucklingCB;*/
    
    
    public AddMilkProductionScreen(Midlet midlet, int locale, Farmer farmer) {
        super(Locale.getStringInLocale(locale, StringResources.add_production));
        
        this.locale = locale;
        this.midlet = midlet;
        this.farmer = farmer;
        
        this.parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        
        backCommand = new Command(Locale.getStringInLocale(locale, StringResources.back));
        this.addCommand(backCommand);
        
        addCommand = new Command(Locale.getStringInLocale(locale, StringResources.add));
        this.addCommand(addCommand);
        
        this.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(backCommand)) {
                    MilkProductionScreen milkProductionScreen = new MilkProductionScreen(AddMilkProductionScreen.this.midlet, AddMilkProductionScreen.this.locale, AddMilkProductionScreen.this.farmer);
                    milkProductionScreen.start();
                }
                else if(evt.getCommand().equals(addCommand)){
                    if(validateInput()){
                        JSONObject jsonObject  = new JSONObject();
                        try {
                            jsonObject.put("mobile_no",AddMilkProductionScreen.this.farmer.getMobileNumber());
                            Cow selectedCow = (Cow) validCows.elementAt(cowCB.getSelectedIndex());
                            jsonObject.put("cowName",selectedCow.getName());
                            jsonObject.put("cowEarTagNumber",selectedCow.getEarTagNumber());

                            String[] timesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.milking_times);
                            jsonObject.put("time",timesInEN[timeCB.getSelectedIndex()]);
                            jsonObject.put("quantity",quantityTF.getText());

                            String[] quantityTypesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.quantity_types);
                            jsonObject.put("quantityType",quantityTypesInEN[quantityTypeCB.getSelectedIndex()]);

                            Date date = (Date) dateS.getValue();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            String dateString = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.MONTH)+1)+"/"+String.valueOf(calendar.get(Calendar.YEAR));
                            jsonObject.put("date", dateString);
                            
                            /*jsonObject.put("noMilkingTimes",noTimesMilkedTF.getText());
                            
                            String[] yesNoInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.yes_no);
                            jsonObject.put("calfSuckling", yesNoInEN[calfSucklingCB.getSelectedIndex()]);*/
                            
                            Thread thread = new Thread(new MilkProductionHandler(jsonObject));
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
        
        dateB = new Button(Locale.getStringInLocale(locale, StringResources.click_to_set_date));
        setComponentStyle(dateB, true);
        dateB.getSelectedStyle().setBgColor(0x2ecc71);
        dateB.addActionListener(this);
        this.addComponent(dateB);
        
        
        dateS = Spinner.createDate(System.currentTimeMillis() - (86400000l*15), System.currentTimeMillis(), System.currentTimeMillis(), '/', Spinner.DATE_FORMAT_DD_MM_YYYY);
        //setComponentStyle(dateS, true);
        //this.addComponent(dateS);
        
        timeL = new Label(Locale.getStringInLocale(locale, StringResources.time));
        setLabelStyle(timeL);
        this.addComponent(timeL);
        
        timeCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.milking_times));
        setComponentStyle(timeCB, true);
        timeCB.setRenderer(new MistroListCellRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.milking_times)));
        this.addComponent(timeCB);
        
        quantityL = new Label(Locale.getStringInLocale(locale, StringResources.quantity));
        setLabelStyle(quantityL);
        this.addComponent(quantityL);
        
        quantityTF = new TextField();
        setComponentStyle(quantityTF, false);
        quantityTF.setConstraint(TextField.NUMERIC);
        quantityTF.setInputModeOrder(new String[] {"123"});
        this.addComponent(quantityTF);
        
        quantityTypeL = new Label(Locale.getStringInLocale(locale, StringResources.quantity_type));
        setLabelStyle(quantityTypeL);
        this.addComponent(quantityTypeL);
        
        quantityTypeCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.quantity_types));
        setComponentStyle(quantityTypeCB, true);
        quantityTypeCB.setRenderer(new MistroListCellRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.quantity_types)));
        this.addComponent(quantityTypeCB);
        
        /*noTimesMilkedL = new Label(Locale.getStringInLocale(locale, StringResources.no_tms_mlkd));
        setLabelStyle(noTimesMilkedL);
        this.addComponent(noTimesMilkedL);
        
        noTimesMilkedTF = new TextField();
        setComponentStyle(noTimesMilkedTF, false);
        noTimesMilkedTF.setConstraint(TextField.NUMERIC);
        noTimesMilkedTF.setInputModeOrder(new String[]{"123"});
        this.addComponent(noTimesMilkedTF);
        
        calfSucklingL = new Label(Locale.getStringInLocale(locale, StringResources.calf_suckling));
        setLabelStyle(calfSucklingL);
        this.addComponent(calfSucklingL);
        
        calfSucklingCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.yes_no));
        setComponentStyle(calfSucklingCB, true);
        calfSucklingCB.setRenderer(new MistroListCellRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.yes_no)));
        this.addComponent(calfSucklingCB);*/
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
        for(int i = 0; i < allCows.length; i++){
            if(allCows[i].getSex().equals(Cow.SEX_FEMALE)){
                validCows.addElement(allCows[i]);
            }
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
        
        if(quantityTF.getText()==null || quantityTF.getText().trim().length() == 0){
            infoDialog.setText(Locale.getStringInLocale(locale, StringResources.enter_quantity_of_milk_produced));
            quantityTF.requestFocus();
            infoDialog.show();
            return false;
        }
        
        if(validateDate()!=null){
            infoDialog.setText(validateDate());
            dateB.requestFocus();
            infoDialog.show();
            return false;
        }
        
        String[] quantityTypesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.quantity_types);
        String quantityType = quantityTypesInEN[quantityTypeCB.getSelectedIndex()];
        if(quantityType.equals("Litres") || quantityType.equals("KGs")){
            if(Integer.parseInt(quantityTF.getText()) > 50){
                infoDialog.setText(Locale.getStringInLocale(locale, StringResources.milk_too_much));
                quantityTF.requestFocus();
                infoDialog.show();
                return false;
            }
        }
        return true;
    }
    
    private String validateDate(){
        Date dateSelected  = (Date) dateS.getValue();
        long currentTime = System.currentTimeMillis();
        
        long timeDiffDays = (currentTime - dateSelected.getTime())/86400000;
        
        if(timeDiffDays > MilkProduction.MAX_MILK_PRODUCTION_DAYS){
            return Locale.getStringInLocale(locale, StringResources.milk_data_too_old);
        }
        else if(timeDiffDays < 0){
            return Locale.getStringInLocale(locale, StringResources.date_in_future);
        }
        
        return null;
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
    
    private void actOnServerResponse(String response){
        if(response == null){
            final InformationDialog infoDialog = new InformationDialog(Locale.getStringInLocale(locale, StringResources.error), locale, false);
            infoDialog.setDialogType(Dialog.TYPE_ERROR);
            
            infoDialog.setText(Locale.getStringInLocale(locale, StringResources.problem_connecting_to_server));
            infoDialog.show();
        }
        else if(response.equals(DataHandler.ACKNOWLEDGE_OK)){
            farmer.update();
            final InformationDialog infoDialog = new InformationDialog(Locale.getStringInLocale(locale, StringResources.success), locale, true);
            
            infoDialog.addCommandListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if(evt.getCommand().equals(infoDialog.getBackCommand())){
                        MilkProductionScreen milkProductionScreen = new MilkProductionScreen(midlet, locale, farmer);
                        milkProductionScreen.start();
                    }
                }
            });
            
            infoDialog.setText(Locale.getStringInLocale(locale, StringResources.information_successfully_sent_to_server));
            infoDialog.show();
        }
        else if(response.equals(DataHandler.DATA_ERROR)){
            final InformationDialog infoDialog = new InformationDialog(Locale.getStringInLocale(locale, StringResources.error), locale, false);
            infoDialog.setDialogType(Dialog.TYPE_ERROR);
            infoDialog.setText(Locale.getStringInLocale(locale, StringResources.problem_in_data_sent_en));
            infoDialog.show();
        }
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

    public void dateSelected(Spinner spinner, Date date) {
        if(spinner.equals(dateS)){
            dateB.setText(DateDialog.dateToString((Date)dateS.getValue()));
        }
    }
    
    private class MilkProductionHandler implements Runnable{

        private JSONObject jSONObject;
        
        public MilkProductionHandler(JSONObject jSONObject) {
            this.jSONObject = jSONObject;
        }
        
        
        public void run() {
            String response = DataHandler.sendDataToServer(jSONObject, DataHandler.FARMER_ADD_MILK_PRODUCTION_URL);
            actOnServerResponse(response);
        }
        
    }
}
