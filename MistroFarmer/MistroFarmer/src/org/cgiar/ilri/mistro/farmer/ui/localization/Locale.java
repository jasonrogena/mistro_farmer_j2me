/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.ui.localization;

/**
 *
 * @author jason
 */
public class Locale {
    public static final int LOCALE_NO = 2;
    public static final int LOCALE_EN = 0;
    public static final int LOCALE_SW = 1;
    
    public static String getStringInLocale(int locale,String[] array){
        if(locale < array.length) {
            return array[locale];
        }
        else{
            return null;
        }
    }
    
    public static String[] getStringArrayInLocale(int locale,String[][] array){
        if(locale < array.length) {
            return array[locale];
        }
        else {
            return null;
        }
    }
    
    public static String[] getAllLanguages(){
        String[] languages = new String[2];
        languages[0] = "English";
        languages[1] = "Swahili";
        return languages;
    }
    
    public static String getLocaleString(String language){
        if(language.equals("English")){
            return "en";
        }
        else if(language.equals("Swahili")){
            return "sw";
        }
        else{
            return "en";//default locale
        }
    }
    
    public static String getLanguage(String locale){
        if(locale.equals("en")){
            return "English";
        }
        else if(locale.equals("sw")){
            return "Swahili";
        }
        else{
            return "English";
        }
    }
}
