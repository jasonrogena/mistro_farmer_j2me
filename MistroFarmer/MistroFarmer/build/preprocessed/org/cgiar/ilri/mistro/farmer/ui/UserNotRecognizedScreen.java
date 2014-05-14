/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Form;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import org.cgiar.ilri.mistro.farmer.Midlet;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;

/**
 *
 * @author jason
 */
public class UserNotRecognizedScreen extends Form implements Screen, ActionListener {
    private final int locale;
    private final Midlet midlet;
    
    private BoxLayout parentBoxLayout;
    private TextArea instructionsTA;
    private Button loginButton;
    private Button registerButton;
    private Command backCommand;
    
    private final float MARGIN_TOP = 0.1f;

    public UserNotRecognizedScreen(Midlet midlet, int locale) {
        super();
        this.locale = locale;
        this.midlet = midlet;
        
        //init all layout components
        this.parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        
        backCommand = new Command(Locale.getStringInLocale(locale, StringResources.back));
        this.addCommand(backCommand);
        this.addCommandListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if(actionEvent.getCommand().equals(backCommand)){
                    LoginScreen loginScreen = new LoginScreen(UserNotRecognizedScreen.this.midlet, UserNotRecognizedScreen.this.locale);
                    loginScreen.start();
                }
            }
        });
        
        int displayHeight = Screen.Util.getScreenHeight(this);
        
        instructionsTA = new TextField(Locale.getStringInLocale(locale, StringResources.number_card_not_registered));
        instructionsTA.setEditable(false);
        instructionsTA.setFocusable(false);
        instructionsTA.setRows(3);
        instructionsTA.setSingleLineTextArea(false);
        instructionsTA.getStyle().setAlignment(CENTER);
        instructionsTA.getStyle().setMargin((int)(displayHeight*MARGIN_TOP), 0, 0, 0);
        instructionsTA.getSelectedStyle().setMargin((int)(displayHeight*MARGIN_TOP), 0, 0, 0);
        this.addComponent(instructionsTA);
        
        loginButton = new Button(Locale.getStringInLocale(locale, StringResources.login_anyway));
        loginButton.getStyle().setAlignment(Component.CENTER);
        loginButton.getStyle().setMargin(10, 0, 0, 0);
        loginButton.getSelectedStyle().setAlignment(Component.CENTER);
        loginButton.getSelectedStyle().setMargin(10, 0, 0, 0);
        loginButton.getSelectedStyle().setBgColor(0x2ecc71);
        loginButton.addActionListener(this);
        this.addComponent(loginButton);
        
        registerButton = new Button(Locale.getStringInLocale(locale, StringResources.register));
        registerButton.getStyle().setAlignment(Component.CENTER);
        registerButton.getStyle().setMargin(10, 0, 0, 0);
        registerButton.getSelectedStyle().setAlignment(Component.CENTER);
        registerButton.getSelectedStyle().setMargin(10, 0, 0, 0);
        registerButton.getSelectedStyle().setBgColor(0x2ecc71);
        registerButton.addActionListener(this);
        this.addComponent(registerButton);
    }
    
    

    public void start() {
        this.show();
    }

    public void destroy() {
        midlet.destroy();
    }

    public void pause() {
    }
    
    public void actionPerformed(ActionEvent event) {
        if(event.getComponent().equals(registerButton)){
            FarmerRegistrationScreen farmerRegistrationScreen = new FarmerRegistrationScreen(midlet, locale, null);
            farmerRegistrationScreen.start();
        }
        else if(event.getComponent().equals(loginButton)){
            SimcardRegistrationScreen simcardRegistrationScreen = new SimcardRegistrationScreen(midlet, locale);
            simcardRegistrationScreen.start();
        }
    }
    
    
}
