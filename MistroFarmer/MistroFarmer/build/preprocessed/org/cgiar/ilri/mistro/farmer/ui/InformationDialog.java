/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.Command;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.plaf.Border;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;

/**
 *
 * @author jason
 */
public class InformationDialog extends Dialog {
    private TextField text;
    private Command backCommand;
    private int locale;
    
    private final float MARGIN_TOP = 0.1f;//ratio based on the height of the screen
    private final float MARGIN_BOTTOM = 0.1f;//ratio based on the height of the screen
    private final float MARGIN_HORIZONTAL = 0.05f;
    
    public InformationDialog(String title, int locale, boolean withPlaciboCommand) {
        super(title);
        
        intiChidViews(locale, withPlaciboCommand);
    }
    
    public InformationDialog(int locale, boolean withPlaciboCommand){
        super();
        intiChidViews(locale, withPlaciboCommand);
    }
    
    private void intiChidViews(int locale, boolean withPlaciboCommand){
        this.locale = locale;
        this.setDialogType(Dialog.TYPE_INFO);
        String backCommandText = Locale.getStringInLocale(locale, StringResources.back);
        if(withPlaciboCommand){
            this.setDialogType(Dialog.TYPE_CONFIRMATION);
            this.addCommand(new Command(""));
            backCommandText = Locale.getStringInLocale(locale, StringResources.okay);
        }
        
        backCommand = new Command(backCommandText);
        this.addCommand(backCommand);
        this.addCommandListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(backCommand)){
                    InformationDialog.this.dispose();
                }
            }
        });
        
        text = new TextField();
        text.setEditable(false);
        text.setFocusable(false);
        text.setRows(3);
        text.setSingleLineTextArea(false);
        text.getStyle().setAlignment(CENTER);
        text.getStyle().setMargin(10, 10, 12, 12);
        text.getStyle().setBorder(Border.createEmpty());
        this.addComponent(text);
    }
    
    public void setText(String text){
        this.text.setText(text);
    }
    
    public void show(){
        int displayHeight = Display.getInstance().getDisplayHeight();
        displayHeight = displayHeight - this.getTitleComponent().getPreferredH() - this.getSoftButtonCount();
        if(getSoftButtonCount() >= 1){
            displayHeight = displayHeight - this.getSoftButton(0).getParent().getPreferredH();
        }
        
        int displayWidth = Display.getInstance().getDisplayWidth();
        
        System.out.println("Display height = "+String.valueOf(displayHeight));
        System.out.println("Margin = "+String.valueOf((int)(displayHeight*MARGIN_BOTTOM)));
        super.show((int)(displayHeight*MARGIN_TOP), (int)(displayHeight*MARGIN_BOTTOM), (int)(displayWidth*MARGIN_HORIZONTAL), (int)(displayWidth*MARGIN_HORIZONTAL), true);
    }
    
    public Command getBackCommand(){
        return backCommand;
    }
}
