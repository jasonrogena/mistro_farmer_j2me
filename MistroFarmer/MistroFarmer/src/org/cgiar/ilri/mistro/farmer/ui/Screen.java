package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.Display;
import com.sun.lwuit.Form;

/**
 *
 * @author jason
 */
public interface Screen {
    public void start();
    public void destroy();
    public void pause();
    
    public static class Util{
        public static int getScreenHeight(Form form){
            int displayHeight = Display.getInstance().getDisplayHeight();
            displayHeight = displayHeight - form.getTitleComponent().getPreferredH() - form.getSoftButtonCount();
            if(form.getSoftButtonCount() >= 1){
                displayHeight = displayHeight - form.getSoftButton(0).getParent().getPreferredH();
            }
            return displayHeight;
        }
        
        public static int getScreenWidth(Form form){
            int displayWidth = Display.getInstance().getDisplayWidth();
            
            return displayWidth;
        }
    }
}
