/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import org.cgiar.ilri.mistro.farmer.Midlet;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;

/**
 *
 * @author jason
 */
public class AddEventScreen extends Form implements Screen, ActionListener{

    private final Midlet midlet;
    private final int locale;
    private final Farmer farmer;
    
    private BoxLayout parentBoxLayout;
    private Command backCommand;
    
    
    
    public AddEventScreen(Midlet midlet, int locale, Farmer farmer) {
        super(Locale.getStringInLocale(locale, StringResources.add_an_event));
        
        this.midlet = midlet;
        this.locale = locale;
        this.farmer = farmer;
        
        parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        
        backCommand = new Command(Locale.getStringInLocale(locale, StringResources.back));
        this.addCommand(backCommand);
        
        this.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(backCommand)){
                    EventsScreen eventsScreen = new EventsScreen(AddEventScreen.this.midlet, AddEventScreen.this.locale, AddEventScreen.this.farmer);
                    eventsScreen.start();
                }
            }
        });
        
        
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
