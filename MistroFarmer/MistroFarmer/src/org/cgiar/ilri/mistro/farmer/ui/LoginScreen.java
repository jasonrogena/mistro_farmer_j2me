package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import javax.microedition.lcdui.Displayable;
import org.cgiar.ilri.mistro.farmer.Midlet;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;
import org.cgiar.ilri.mistro.farmer.utils.DataHandler;
import org.json.me.JSONException;
import org.json.me.JSONObject;


/**
 * This is the Login screen for the application. It:
 *  - Is the first screen in the application (landing screen)
 *  - Contains part of the logic for user authentication
 *  - Contains part of the logic for redirecting user to register
 *    his/her mobile number if mobile number provided during logging in
 *    is unknown
 *  - Is the only screen where users are able to change their locale
 * 
 * @author Jason Rogena <j.rogena@cgiar.org>
 * 
 */
public class LoginScreen extends Form implements Screen, ActionListener{
    private final int locale;
    
    private BoxLayout parentBoxLayout;
    private Button loginButton;
    private Button registerButton;
    private Command exitCommand;
    private final Midlet midlet;
    private int displayHeight;
    
    public LoginScreen(Midlet midlet,int locale) {
        super();
        this.midlet = midlet;
        this.locale = locale;
        
        this.setTitle(midlet.APP_NAME + " " + midlet.APP_VERSION);
        
        //get screen Height
        displayHeight = Screen.Util.getScreenHeight(this);
        
        //init all layout components
        this.parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        
        //create logic for allowing user to exit from application
        exitCommand = new Command(Locale.getStringInLocale(locale, StringResources.exit));
        this.addCommand(exitCommand);
        this.addCommandListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(exitCommand)){
                    destroy();
                }
            }
        });
        
        loginButton = new Button(Locale.getStringInLocale(locale, StringResources.login));
        loginButton.getStyle().setAlignment(Component.CENTER);
        loginButton.getStyle().setMargin((int)(displayHeight*0.1), 10, 0, 0);
        loginButton.getSelectedStyle().setAlignment(Component.CENTER);
        loginButton.getSelectedStyle().setMargin((int)(displayHeight*0.1), 10, 0, 0);
        loginButton.getSelectedStyle().setBgColor(0x2ecc71);
        loginButton.addActionListener(this);
        this.addComponent(loginButton);
        
        registerButton = new Button(Locale.getStringInLocale(locale, StringResources.register));
        registerButton.getStyle().setAlignment(Component.CENTER);
        registerButton.getStyle().setMargin(10, 10, 0, 0);
        registerButton.getSelectedStyle().setAlignment(Component.CENTER);
        registerButton.getSelectedStyle().setMargin(10, 10, 0, 0);
        registerButton.getSelectedStyle().setBgColor(0x2ecc71);
        registerButton.addActionListener(this);
        this.addComponent(registerButton);
    }

    /**
     * This method should be called after the LoginScreen object has been initialized,
     * Otherwise the screen will not show.
     * Add logic here that you want to run just before the screen shows.
     */
    public void start() {
        this.show();
    }

    public void destroy() {
        midlet.destroy();
    }

    public void pause() {
    }

    /**
     * Abstract method from the ActionListener interface. This method is charged with listening
     * for events in this screen. This method is called by the Event Listener abstraction on JavaME
     * and not by code in this application.
     * 
     * @param event Object representing the event that has occurred
     */
    public void actionPerformed(ActionEvent event) {
        if(event.getComponent().equals(registerButton)){
            //Switch to the farmer registration screen
            FarmerRegistrationScreen farmerRegistrationScreen = new FarmerRegistrationScreen(midlet, locale, null);
            farmerRegistrationScreen.start();
        }
        else if(event.getComponent().equals(loginButton)){
            //Display to the user a dialog on which they can provide their mobile number
            //TODO: fetch the mobile number automatically from the divice. Code may bahave differently in different devices. Fff JavaME
            final Dialog loginDialog = new Dialog(Locale.getStringInLocale(locale, StringResources.login));
            loginDialog.setDialogType(Dialog.TYPE_INFO);
            loginDialog.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
            
            final Command cancelCommand = new Command(Locale.getStringInLocale(locale, StringResources.cancel));
            loginDialog.addCommand(cancelCommand);
            
            final Command loginCommand = new Command(Locale.getStringInLocale(locale, StringResources.login));
            loginDialog.addCommand(loginCommand);
            
            Label mobileNumberL = new Label(Locale.getStringInLocale(locale, StringResources.mobile_number));
            mobileNumberL.getStyle().setMargin(10, 0, 10, 0);
            mobileNumberL.getSelectedStyle().setMargin(10, 0, 10, 0);
            loginDialog.addComponent(mobileNumberL);
            
            final TextField mobileNumberTF = new TextField();
            mobileNumberTF.getStyle().setMargin(5, 0, 0, 0);
            mobileNumberTF.getSelectedStyle().setMargin(5, 0, 0, 0);
            mobileNumberTF.setConstraint(TextField.NUMERIC);
            mobileNumberTF.setInputModeOrder(new String[] {"123"});
            loginDialog.addComponent(mobileNumberTF);
            
            //listen for command events
            loginDialog.addCommandListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if(evt.getCommand().equals(cancelCommand)){
                        //if cancel command pressed, close dialog
                        loginDialog.dispose();
                    }
                    else if(evt.getCommand().equals(loginCommand)){
                        if(mobileNumberTF.getText()!=null || mobileNumberTF.getText().trim().length()>0){
                            //if login command pressed and user has specified mobile number
                            String mobileNumber = mobileNumberTF.getText();
                            Thread thread = new Thread(new LoginHandler(mobileNumber, loginDialog));
                            thread.run();
                        }
                    }
                }
            });
            
            loginDialog.show((int)(displayHeight*0.1), (int)(displayHeight*0.1), 11, 11, true);
        }
    }
    
    /**
     * This method is called after server responds.
     * Be very careful, this method is not called by the main thread
     * 
     * @param response The response (in form of a string) from the server
     */
    private void actOnServerResponse(String response){
        if(response == null){
            System.err.println("no response from server");
        }
        else if(response.equals(DataHandler.CODE_USER_NOT_AUTHENTICATED)) {
            UserNotRecognizedScreen notRecognizedScreen = new UserNotRecognizedScreen(midlet, locale);
            notRecognizedScreen.start();
        }
        else{
            try {
                JSONObject farmerJSONObject = new JSONObject(response);
                Farmer farmer = new Farmer(farmerJSONObject);
                
                MainMenuScreen mainMenuScreen = new MainMenuScreen(midlet, locale, farmer);
                mainMenuScreen.start();
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * This class initializes a thread for communicating with the server
     * for purposes of authenticating the user.
     * Make sure you call the constructor before you start the thread.
     */
    private class LoginHandler implements Runnable{
        
        String mobileNumber;
        Dialog parentDialog;

        /**
         * Constructor for the LoginHandler class that creates a thread
         * for communicating with the server for purposes of authentication.
         * 
         * @param mobileNumber Mobile number specified by user
         * @param parentDialog Dialog that provided a means for the user to specify their number
         */
        public LoginHandler(String mobileNumber, Dialog parentDialog) {
            this.mobileNumber = mobileNumber;
            this.parentDialog = parentDialog;
        }
        

        public void run() {
            //1. Initialize a json object for transporting the data to the server
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("mobileNumber", mobileNumber);
                
                //2. Get response from the server
                String response = DataHandler.sendDataToServer(jSONObject, DataHandler.FARMER_AUTHENTICATION_URL);
                
                //3. Close the dialog where user specified his/her phone number
                parentDialog.dispose();
                actOnServerResponse(response);
            } 
            catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        
    }
    
}
