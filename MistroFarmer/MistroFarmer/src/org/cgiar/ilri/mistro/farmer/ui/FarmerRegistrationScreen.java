package org.cgiar.ilri.mistro.farmer.ui;


import com.sun.lwuit.ComboBox;
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
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.cgiar.ilri.mistro.farmer.ui.Screen;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;
import org.cgiar.ilri.mistro.farmer.utils.DataHandler;
import org.cgiar.ilri.mistro.farmer.utils.ResponseListener;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jason
 */
public class FarmerRegistrationScreen extends Form implements Screen, ResponseListener{
    
    private final Midlet midlet;
    private final int locale;
    
    private BoxLayout parentBoxLayout;
    private Command backCommand;
    private Command nextCommand;
    private TextField fullNameTF;
    private Label fullNameL;
    private TextField mobileNoTF;
    private Label mobileNoL;
    private ComboBox ePersonnelCB;
    private Label ePersonnelL;
    private Label preferredLanguageL;
    private ComboBox preferredLanguageCB;
    private Label cowNumberL;
    private TextField cowNumberTF;
    private Farmer farmer;
    
    private String[] vetNames;
    private String[] languages;

    public FarmerRegistrationScreen(Midlet midlet, int locale, Farmer farmer) {
        super(Locale.getStringInLocale(locale, StringResources.register));
        
        this.locale = locale;
        this.midlet = midlet;
        if(farmer!=null){
            this.farmer = farmer;
        }
        else {
            this.farmer = new Farmer();
        }
        
        this.parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        
        backCommand = new Command(Locale.getStringInLocale(locale, StringResources.back));
        this.addCommand(backCommand);
        
        nextCommand = new Command(Locale.getStringInLocale(locale, StringResources.next));
        this.addCommand(nextCommand);
        
        this.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(backCommand)){
                    LoginScreen loginScreen = new LoginScreen(FarmerRegistrationScreen.this.midlet, FarmerRegistrationScreen.this.locale);
                    loginScreen.start();
                }
                else if(evt.getCommand().equals(nextCommand)) {
                    String noOfCowsString = cowNumberTF.getText();
                    if(validateInput()){
                        saveFarmerDetails();
                        if(noOfCowsString!= null && !noOfCowsString.equals("") && Integer.parseInt(noOfCowsString) > 0) {
                            CowRegistrationScreen firstCowScreen = new CowRegistrationScreen(FarmerRegistrationScreen.this.midlet, FarmerRegistrationScreen.this.locale, FarmerRegistrationScreen.this.farmer, 0, Integer.parseInt(noOfCowsString));
                            firstCowScreen.start();
                        }
                        else {
                            FarmerRegistrationScreen.this.farmer.syncWithServer(FarmerRegistrationScreen.this);
                        }
                    }
                }
            }
        });
        
        fullNameL = new Label(Locale.getStringInLocale(locale, StringResources.full_name));
        fullNameL.getStyle().setMargin(10, 0, 10, 0);
        fullNameL.getSelectedStyle().setMargin(10, 0, 10,0);
        this.addComponent(fullNameL);
        
        fullNameTF = new TextField();
        fullNameTF.getStyle().setMargin(5, 0, 0, 0);
        fullNameTF.getSelectedStyle().setMargin(5, 0, 0, 0);
        this.addComponent(fullNameTF);
        
        mobileNoL = new Label(Locale.getStringInLocale(locale, StringResources.mobile_number));
        mobileNoL.getStyle().setMargin(10, 0, 10, 0);
        mobileNoL.getSelectedStyle().setMargin(10, 0, 10, 0);
        this.addComponent(mobileNoL);
        
        mobileNoTF = new TextField();
        mobileNoTF.getStyle().setMargin(5, 0, 0, 0);
        mobileNoTF.getSelectedStyle().setMargin(5, 0, 0, 0);
        mobileNoTF.setConstraint(TextField.NUMERIC);
        mobileNoTF.setInputModeOrder(new String[] {"123"});
        this.addComponent(mobileNoTF);
        
        ePersonnelL = new Label(Locale.getStringInLocale(locale, StringResources.extension_p));
        ePersonnelL.getStyle().setMargin(10, 0, 10, 0);
        ePersonnelL.getSelectedStyle().setMargin(10, 0, 10, 0);
        this.addComponent(ePersonnelL);
        
        vetNames = new String[]{""};
        ePersonnelCB = new ComboBox(vetNames);
        ePersonnelCB.getStyle().setMargin(5, 0, 0, 0);
        ePersonnelCB.getSelectedStyle().setMargin(5, 0, 0, 0);
        ePersonnelCB.getSelectedStyle().setBgColor(0x2ecc71);
        ePersonnelCB.setRenderer(new MistroListCellRenderer(vetNames));
        this.addComponent(ePersonnelCB);
        
        preferredLanguageL = new Label(Locale.getStringInLocale(locale, StringResources.preferred_language));
        preferredLanguageL.getStyle().setMargin(10, 0, 10, 0);
        preferredLanguageL.getSelectedStyle().setMargin(10, 0, 10, 0);
        this.addComponent(preferredLanguageL);
        
        languages = Locale.getAllLanguages();
        preferredLanguageCB = new ComboBox(languages);
        preferredLanguageCB.getStyle().setMargin(5, 0, 0, 0);
        preferredLanguageCB.getSelectedStyle().setMargin(5, 0, 0, 0);
        preferredLanguageCB.getSelectedStyle().setBgColor(0x2ecc71);
        preferredLanguageCB.setRenderer(new MistroListCellRenderer(languages));
        this.addComponent(preferredLanguageCB);
        
        cowNumberL = new Label(Locale.getStringInLocale(locale, StringResources.number_of_cows));
        cowNumberL.getStyle().setMargin(10, 0, 10, 0);
        cowNumberL.getSelectedStyle().setMargin(10, 0, 10, 0);
        this.addComponent(cowNumberL);
        
        cowNumberTF = new TextField();
        cowNumberTF.getStyle().setMargin(5, 0, 0, 0);
        cowNumberTF.getSelectedStyle().setMargin(5, 0, 0, 0);
        cowNumberTF.setConstraint(TextField.NUMERIC);
        cowNumberTF.setInputModeOrder(new String[] {"123"});
        this.addComponent(cowNumberTF);
        
        restoreFarmerDetails();
        
        GetVetsThread getVetsThread = new GetVetsThread(this);
        getVetsThread.run();
    }
    
    private boolean validateInput(){
        final InformationDialog infoDialog = new InformationDialog(locale, false);
        
        if(fullNameTF.getText()== null || fullNameTF.getText().trim().length()==0){
            fullNameTF.requestFocus();
            infoDialog.setText(Locale.getStringInLocale(locale, StringResources.enter_your_name));
            infoDialog.show();
            return false;
        }
        if(mobileNoTF.getText()== null || mobileNoTF.getText().trim().length()==0){
            mobileNoTF.requestFocus();
            infoDialog.setText(Locale.getStringInLocale(locale, StringResources.enter_your_mobile_no));
            infoDialog.show();
            return false;
        }
        if(cowNumberTF.getText() == null || cowNumberTF.getText().trim().length() == 0){
            cowNumberTF.requestFocus();
            infoDialog.setText(Locale.getStringInLocale(locale, StringResources.enter_no_cows));
            infoDialog.show();
            return false;
        }
        return true;
    }
    
    private void saveFarmerDetails(){
        farmer.setMode(Farmer.MODE_INITIAL_REGISTRATION);
        farmer.setFullName(fullNameTF.getText());
        farmer.setMobileNumber(mobileNoTF.getText());
        farmer.setExtensionPersonnel(vetNames[ePersonnelCB.getSelectedIndex()]);
        farmer.setPreferredLanguage(languages[preferredLanguageCB.getSelectedIndex()]);
        if(cowNumberTF.getText()!=null && cowNumberTF.getText().trim().length() > 0){
            farmer.setCowNumber(Integer.parseInt(cowNumberTF.getText().trim()));
        }
    }
    
    private void restoreFarmerDetails(){
        if(farmer!=null){
            fullNameTF.setText(farmer.getFullName());
            mobileNoTF.setText(farmer.getMobileNumber());
            
            String selectedP = farmer.getExtensionPersonnel();
            int epIndex = -1;
            for(int i = 0; i < vetNames.length; i++){
                if(vetNames[i].equals(selectedP)){
                    epIndex = i;
                }
            }
            if(epIndex == -1){
                String[] tmp = new String[vetNames.length+1];
                for(int i = 0; i < vetNames.length; i++){
                    tmp[i] = vetNames[i];
                }
                tmp[tmp.length - 1] = selectedP;
                vetNames = tmp;
                epIndex = tmp.length - 1;
            }
            
            String prefLang = this.farmer.getPreferredLanguage();
            for(int i = 0; i < languages.length; i++){
                if(languages[i].equals(prefLang)){
                    preferredLanguageCB.setSelectedIndex(i);
                }
            }
            
            ePersonnelCB.setSelectedIndex(epIndex);
            cowNumberTF.setText(String.valueOf(farmer.getCowNumber()));
        }
    }
    
    public void confirmRegistration(String message){
        System.out.println(message);
    }
    
    public void start() {
        this.show();
    }

    public void destroy() {
    }

    public void pause() {
    }

    public void responseGotten(Object source, String message) {
        System.out.println("Message = "+message);
        if(this.farmer != null && source.getClass() == this.farmer.getClass()){
            if(message.equals(DataHandler.ACKNOWLEDGE_OK)){
                final InformationDialog infoDialog = new InformationDialog(Locale.getStringInLocale(locale, StringResources.successful_registration), locale, true);

                infoDialog.addCommandListener(new ActionListener() {

                    public void actionPerformed(ActionEvent evt) {
                        if(evt.getCommand().equals(backCommand)){
                            LoginScreen loginScreen = new LoginScreen(midlet, locale);
                            loginScreen.start();
                        }
                    }
                });

                infoDialog.setText(Locale.getStringInLocale(locale, StringResources.successful_registration_instructions));
                infoDialog.show();
            }
            else{
                final InformationDialog infoDialog = new InformationDialog(Locale.getStringInLocale(locale, StringResources.error), locale, false);
                infoDialog.setDialogType(Dialog.TYPE_ERROR);

                infoDialog.setText(Locale.getStringInLocale(locale, StringResources.something_went_wrong_try_again));
                infoDialog.show();
            }
        }
        else if(source.getClass() == this.getClass()){
            if(message == null){
                final InformationDialog infoDialog = new InformationDialog(Locale.getStringInLocale(locale, StringResources.error), locale, false);
                infoDialog.setDialogType(Dialog.TYPE_ERROR);

                infoDialog.setText(Locale.getStringInLocale(locale, StringResources.something_went_wrong_try_again));
                infoDialog.show();
            }
            else{
                try {
                    JSONArray vetJSONArray = new JSONArray(message);
                    String[] vetNames = new String[vetJSONArray.length() + 1];
                    vetNames[0] = "";
                    System.out.println("Length of vet names = "+String.valueOf(vetJSONArray.length()));
                    for(int i = 0; i < vetJSONArray.length(); i++){
                        vetNames[i+1] = vetJSONArray.getJSONObject(i).getString("name");
                        System.out.println(vetNames[i+1]);
                    }
                    
                    this.vetNames = vetNames;
                    
                    for(int i = 0; i < ePersonnelCB.getModel().getSize(); i++){
                        ePersonnelCB.getModel().removeItem(i);
                        i--;
                    }
                    
                    for(int i = 0; i < vetNames.length; i++){
                        ePersonnelCB.getModel().addItem(vetNames[i]);
                    }
                    
                    //ePersonnelCB.setRenderer(new MistroListCellRenderer(vetNames));
                    ((MistroListCellRenderer)ePersonnelCB.getRenderer()).updateList(vetNames);
                    
                    if(this.farmer != null){
                        for(int i = 0; i < vetNames.length; i++){
                            if(vetNames[i].equals(farmer.getExtensionPersonnel())){
                                ePersonnelCB.setSelectedIndex(i);
                            }
                        }
                    }
                } 
                catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }
        else{
            System.out.println("Source of the thread data unknown");
        }
    }
    
    private class GetVetsThread implements Runnable{

        ResponseListener responseListener;
        
        public GetVetsThread(ResponseListener responseListener) {
            this.responseListener = responseListener;
        }
        
        public void run() {
            String message = DataHandler.sendDataToServer(new JSONObject(), DataHandler.FARMER_FETCH_VETS_URL);
            responseListener.responseGotten(FarmerRegistrationScreen.this, message);
        }
        
    }
}
