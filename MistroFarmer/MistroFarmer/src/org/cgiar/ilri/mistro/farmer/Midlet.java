package org.cgiar.ilri.mistro.farmer;

import com.sun.lwuit.Display;
import javax.microedition.midlet.*;
import org.cgiar.ilri.mistro.farmer.ui.LoginScreen;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;

/**
 * This is the queen bee. This is the first class to be called when App launches
 * You should only call Classes in the UI package here
 * 
 * @author Jason Rogena <j.rogena@cgiar.org>
 */
public class Midlet extends MIDlet {
    private final int locale = Locale.LOCALE_EN;
    public final String APP_NAME = "Ng'ombe Planner";
    public final String APP_VERSION = "O.8.2";
    
    /**
     * Insert code here that you want to run before the application starts
     * This method is called by JavaME directly and not by other objects in this project
     */
    public void startApp() {
        Display.init(this);
        LoginScreen loginScreen = new  LoginScreen(this, locale);
        loginScreen.start();
    }
    
    /**
     * Insert code here that you want to run when app is paused temporarily. 
     * Pausing occurs when for instance an incoming call is received.
     * This method is called by JavaME directly and not by other objects in this project
     */
    public void pauseApp() {
    }
    
    /**
     * This is the last method in Explicitly defined code to be called before application is destroyed
     * Insert any code that you want to run before the application is killed regardless of what killed it (murder or suicide)
     */
    public void destroyApp(boolean unconditional) {
    }
    
    /**
     * Call this method if you want to kill the application.
     * Insert any code that you want to run when the application is killed form within itself (Seppuku)
     */
    public void destroy(){
        this.destroyApp(true);
        this.notifyDestroyed();
    }
}
