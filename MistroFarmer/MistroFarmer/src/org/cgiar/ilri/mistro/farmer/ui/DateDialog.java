/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.Command;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.spinner.Spinner;
import java.util.Calendar;
import java.util.Date;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;

/**
 *
 * @author jason
 */
public class DateDialog extends Dialog {
    private final TextField text;
    private Command backCommand;
    private Command okayCommand;
    private final Spinner dateSpinner;
    private final OnDateEnteredListener onDateEnteredListener;
    
    private final float MARGIN_TOP = 0.1f;//ratio based on the height of the screen
    private final float MARGIN_BOTTOM = 0.1f;//ratio based on the height of the screen
    private final float MARGIN_HORIZONTAL = 0.05f;
    private final float COMPONENT_MARGIN_TOP = 0.05f;
    private final float COMPONENT_MARGIN_HORIZONTAL = 0.05f;
    private int displayHeight;
    private int displayWidth;
    
    public DateDialog(int locale, Spinner dateSpinner, OnDateEnteredListener onDateEnteredListener){
        super();
        
        this.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        
        displayHeight = Display.getInstance().getDisplayHeight();
        displayHeight = displayHeight - this.getTitleComponent().getPreferredH() - this.getSoftButtonCount();
        if(getSoftButtonCount() >= 1){
            displayHeight = displayHeight - this.getSoftButton(0).getParent().getPreferredH();
        }
        
        displayWidth = Display.getInstance().getDisplayWidth();
        
        this.dateSpinner = dateSpinner;
        this.onDateEnteredListener = onDateEnteredListener;
        
        this.setDialogType(Dialog.TYPE_INFO);
        
        String backCommandText = Locale.getStringInLocale(locale, StringResources.back);
        backCommand = new Command(backCommandText);
        this.addCommand(backCommand);
        
        okayCommand = new Command(Locale.getStringInLocale(locale, StringResources.okay));
        this.addCommand(okayCommand);
        
        this.addCommandListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(backCommand)){
                    DateDialog.this.dispose();
                }
                else if (evt.getCommand().equals(okayCommand)){
                    Date date = (Date) DateDialog.this.dateSpinner.getValue();
                    
                    DateDialog.this.onDateEnteredListener.dateSelected(DateDialog.this.dateSpinner, date);
                }
            }
        });
        
        
        
        text = new TextField();
        text.setEditable(false);
        text.setFocusable(false);
        text.setRows(1);
        text.setSingleLineTextArea(false);
        text.getStyle().setAlignment(CENTER);
        text.getStyle().setMargin((int)(displayHeight * COMPONENT_MARGIN_TOP), 0, (int)(displayWidth * COMPONENT_MARGIN_HORIZONTAL), (int)(displayWidth * COMPONENT_MARGIN_HORIZONTAL));
        this.addComponent(text);
        
        dateSpinner.animate();
        dateSpinner.setFocusable(true);
        dateSpinner.setFocus(true);
        dateSpinner.getStyle().setAlignment(CENTER);
        dateSpinner.getSelectedStyle().setAlignment(CENTER);
        dateSpinner.getSelectedStyle().setBgColor(0x2ecc71);
        dateSpinner.getStyle().setMargin(20, 0, (int)(displayWidth * COMPONENT_MARGIN_HORIZONTAL), (int)(displayWidth * COMPONENT_MARGIN_HORIZONTAL));
        dateSpinner.getSelectedStyle().setMargin(20, 0, (int)(displayWidth * COMPONENT_MARGIN_HORIZONTAL), (int)(displayWidth * COMPONENT_MARGIN_HORIZONTAL));
        
        this.addComponent(dateSpinner);
        dateSpinner.setWidth(displayWidth);
        dateSpinner.requestFocus();
    }
    
    public void setText(String text){
        this.text.setText(text);
    }
    
    public void show(){
        super.show((int)(displayHeight*MARGIN_TOP), (int)(displayHeight*MARGIN_BOTTOM), (int)(displayWidth*MARGIN_HORIZONTAL), (int)(displayWidth*MARGIN_HORIZONTAL), true);
    }
    
    public Command getBackCommand(){
        return backCommand;
    }
    
    public interface OnDateEnteredListener{
        public void dateSelected(Spinner spinner, Date date);
    }
    
    public static String dateToString(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.MONTH)+1)+"/"+String.valueOf(calendar.get(Calendar.YEAR));
    }
}
