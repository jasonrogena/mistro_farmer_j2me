/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.Command;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import org.cgiar.ilri.mistro.farmer.Midlet;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;
import org.cgiar.ilri.mistro.farmer.utils.DataHandler;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 *
 * @author jason
 */
public class SimcardRegistrationScreen extends Form implements Screen{
    
    private final Midlet midlet;
    private final int locale;
    
    private BoxLayout parentBoxLayout;
    private Command backCommand;
    private Command nextCommand;
    private TextField oldNumberTF;
    private Label oldNumberL;
    private TextField newNumberTF;
    private Label newNumberL;
    
    private final float MARGIN_TOP = 0.1f;

    public SimcardRegistrationScreen(Midlet midlet, int locale) {
        this.midlet = midlet;
        this.locale = locale;
        
        //init all layout components
        this.parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        
        int displayHeight = Screen.Util.getScreenHeight(this);
        
        backCommand = new Command(Locale.getStringInLocale(locale, StringResources.back));
        this.addCommand(backCommand);
        nextCommand = new Command(Locale.getStringInLocale(locale, StringResources.next));
        this.addCommand(nextCommand);
        this.addCommandListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if(actionEvent.getCommand().equals(backCommand)){
                    UserNotRecognizedScreen userNotRecognizedScreen = new UserNotRecognizedScreen(SimcardRegistrationScreen.this.midlet, SimcardRegistrationScreen.this.locale);
                    userNotRecognizedScreen.start();
                }
                else if(actionEvent.getCommand().equals(nextCommand)){
                    registerSimCard();
                }
            }
        });
        
        oldNumberL = new Label(Locale.getStringInLocale(locale, StringResources.old_mobile_number));
        oldNumberL.getStyle().setMargin(10, 0, 10, 0);
        oldNumberL.getSelectedStyle().setMargin(10, 0, 10,0);
        this.addComponent(oldNumberL);
        
        oldNumberTF = new TextField();
        oldNumberTF.getStyle().setMargin(5, 0, 0, 0);
        oldNumberTF.getSelectedStyle().setMargin(5, 0, 0, 0);
        oldNumberTF.setConstraint(TextField.NUMERIC);
        oldNumberTF.setInputModeOrder(new String[] {"123"});
        this.addComponent(oldNumberTF);
        
        newNumberL = new Label(Locale.getStringInLocale(locale, StringResources.new_mobile_number));
        newNumberL.getStyle().setMargin(10, 0, 10, 0);
        newNumberL.getSelectedStyle().setMargin(10, 0, 10,0);
        this.addComponent(newNumberL);
        
        newNumberTF = new TextField();
        newNumberTF.getStyle().setMargin(5, 0, 0, 0);
        newNumberTF.getSelectedStyle().setMargin(5, 0, 0, 0);
        newNumberTF.setConstraint(TextField.NUMERIC);
        newNumberTF.setInputModeOrder(new String[] {"123"});
        this.addComponent(newNumberTF);
    }

    public void start() {
        this.show();
    }

    public void destroy() {
        midlet.destroy();
    }

    public void pause() {
        
    }
    
    private boolean validateInput(){
        final InformationDialog infoDialog = new InformationDialog(locale, false);
        
        if(oldNumberTF.getText().length() == 0){
            oldNumberTF.requestFocus();
            infoDialog.setText(Locale.getStringInLocale(locale, StringResources.enter_your_old_mobile_no));
            infoDialog.show();
            return false;
        }
        if(newNumberTF.getText().length() == 0){
            newNumberTF.requestFocus();
            infoDialog.setText(Locale.getStringInLocale(locale, StringResources.enter_your_new_mobile_no));
            infoDialog.show();
            return false;
        }
        
        return true;
    }
    private void registerSimCard(){
        if(validateInput()){
            SimCardRegistrationThread simCardRegistrationThread = new SimCardRegistrationThread(oldNumberTF.getText(), newNumberTF.getText(), "");
            simCardRegistrationThread.run();
        }
    }
    
    private void actOnServerResponse(final String response){
        final InformationDialog infoDialog = new InformationDialog(locale, false);
        
        infoDialog.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(infoDialog.getBackCommand())){
                    if(response != null && response.equals(DataHandler.CODE_SIM_CARD_REGISTERED)){
                        LoginScreen loginScreen = new LoginScreen(midlet, locale);
                        loginScreen.start();
                    }
                }
            }
        });
        
        if(response == null){
            infoDialog.setText(Locale.getStringInLocale(locale, StringResources.problem_connecting_to_server));
            infoDialog.show();
        }
        else if(response.equals(DataHandler.CODE_SIM_CARD_REGISTERED)){
            infoDialog.setText(Locale.getStringInLocale(locale, StringResources.sim_card_registered));
            infoDialog.show();
        }
        else if(response.equals(DataHandler.CODE_USER_NOT_AUTHENTICATED)){
            infoDialog.setText(Locale.getStringInLocale(locale, StringResources.old_mobile_number_not_recognised));
            infoDialog.show();
        }
    }
    
    private class SimCardRegistrationThread implements Runnable {
        private String oldNumber;
        private String newNumber;
        private String simCardSN;

        public SimCardRegistrationThread(String oldNumber, String newNumber, String simCardSN) {
            this.oldNumber = oldNumber;
            this.newNumber = newNumber;
            this.simCardSN = simCardSN;
        }
        
        public void run() {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("newMobileNumber", newNumber);
                jSONObject.put("oldMobileNumber", oldNumber);
                jSONObject.put("newSimCardSN", simCardSN);
                String response = DataHandler.sendDataToServer(jSONObject, DataHandler.FARMER_SIM_CARD_REGISTRATION_URL);
                actOnServerResponse(response);
            } 
            catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        
    }
}
