package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.Button;
import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Font;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Border;
import com.sun.lwuit.spinner.Spinner;
import java.util.Date;
import java.util.Vector;
import org.cgiar.ilri.mistro.farmer.Midlet;
import org.cgiar.ilri.mistro.farmer.carrier.Cow;
import org.cgiar.ilri.mistro.farmer.carrier.Dam;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.cgiar.ilri.mistro.farmer.carrier.Sire;
import org.cgiar.ilri.mistro.farmer.ui.localization.ArrayResources;
import org.cgiar.ilri.mistro.farmer.ui.localization.GeneralArrays;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;
import org.cgiar.ilri.mistro.farmer.utils.DataHandler;
import org.cgiar.ilri.mistro.farmer.utils.ResponseListener;

/**
 *
 * @author jason
 */
public class CowRegistrationScreen extends Form implements Screen, ActionListener, ResponseListener, DateDialog.OnDateEnteredListener{

    private final Midlet midlet;
    private final int locale;
    private final int cowIndex;
    private final int cowNumber;
    private final Farmer farmer;
    private Vector validSires;
    private Vector validSireNames;
    private Vector validDams;
    private Vector validDamNames;
    
    private Command previousCommand;
    private Command nextCommand;
    
    private BoxLayout parentBoxLayout;
    private TextArea nameOrEarTagNumberTA;
    private Label cowNameL;
    private TextField cowNameTF;
    private Label earTagNumberL;
    private TextField earTagNumberTF;
    private TextArea ageOrDOBTA;
    private Label ageL;
    private TextField ageTF;
    private Label ageTypeL;
    private ComboBox ageTypeCB;
    private Label dateOfBirthL;
    private Spinner dateOfBirthS;
    private Button dateOfBirthB;
    private DateDialog dateDialog;
    private Label breedL;
    private ComboBox breedCB;
    private MultiselectRenderer breedMultiselectRenderer;
    private Label sexL;
    private ComboBox sexCB;
    private Label deformityL;
    private ComboBox deformityCB;
    private Label otherDeformityL;
    private TextField otherDeformityTF;
    private MultiselectRenderer deformityMultiselectRenderer;
    private Label serviceTypeL;
    private ComboBox serviceTypeCB;
    private Label sireL;
    private ComboBox sireCB;
    private Label damL;
    //private ComboBox damCB;
    private TextField damTF;
    private Label countryL;
    private ComboBox countryCB;
    private Label strawNumberL;
    private TextField strawNumberTF;
    private Label embryoNumberL;
    private TextField embryoNumberTF;
    public CowRegistrationScreen(Midlet midlet, int locale, Farmer farmer, int cowIndex, int cowNumber) {
        super();
        Cow thisCow = farmer.getCow(cowIndex);
        if(thisCow.getMode()!= null && thisCow.getMode().equals(Cow.MODE_BORN_CALF_REGISTRATION)){
            this.setTitle(Locale.getStringInLocale(locale,StringResources.calf_registration));
        }
        else if(farmer.getMode().equals(Farmer.MODE_NEW_COW_REGISTRATION) && thisCow.getMode()!=null && thisCow.getMode().equals(Cow.MODE_ADULT_COW_REGISTRATION)){
            this.setTitle(Locale.getStringInLocale(locale, StringResources.new_cow_registration));
        }
        else{
            this.setTitle(Locale.getStringInLocale(locale, StringResources.cow_registration_wth_indx)+String.valueOf(cowIndex+1));
        }
        
        this.midlet = midlet;
        this.locale = locale;
        this.cowIndex = cowIndex;
        this.cowNumber = cowNumber;
        this.farmer = farmer;
        
        parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        
        previousCommand = new Command(Locale.getStringInLocale(locale, StringResources.previous));
        this.addCommand(previousCommand);
        
        if(cowIndex < (cowNumber-1))
            nextCommand = new Command(Locale.getStringInLocale(locale, StringResources.next));
        else
            nextCommand = new Command(Locale.getStringInLocale(locale, StringResources.finish));
        this.addCommand(nextCommand);
        
        this.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {  
                if(evt.getCommand().equals(previousCommand)) {
                    if(CowRegistrationScreen.this.farmer.getMode().equals(Farmer.MODE_INITIAL_REGISTRATION)){
                        saveCowDetails();
                        if(CowRegistrationScreen.this.cowIndex>0){
                            CowRegistrationScreen previousScreen = new CowRegistrationScreen(CowRegistrationScreen.this.midlet, CowRegistrationScreen.this.locale, CowRegistrationScreen.this.farmer, CowRegistrationScreen.this.cowIndex - 1, CowRegistrationScreen.this.cowNumber);
                            previousScreen.start();
                        }
                        else {
                            FarmerRegistrationScreen farmerRegistrationScreen = new FarmerRegistrationScreen(CowRegistrationScreen.this.midlet, CowRegistrationScreen.this.locale, CowRegistrationScreen.this.farmer);
                            farmerRegistrationScreen.start();
                        }   
                    }
                    else if(CowRegistrationScreen.this.farmer.getMode().equals(Farmer.MODE_NEW_COW_REGISTRATION)){
                        Cow thisCow = CowRegistrationScreen.this.farmer.getCow(CowRegistrationScreen.this.cowIndex);
                        if(thisCow.getMode().equals(Cow.MODE_BORN_CALF_REGISTRATION)){
                            CowRegistrationScreen.this.farmer.unAppendCow();
                            AddCalvingScreen addCalvingScreen = new AddCalvingScreen(CowRegistrationScreen.this.midlet, CowRegistrationScreen.this.locale, CowRegistrationScreen.this.farmer);
                            addCalvingScreen.start();
                        }
                        else if(thisCow.getMode().equals(Cow.MODE_ADULT_COW_REGISTRATION)){
                            CowRegistrationScreen.this.farmer.unAppendCow();
                            AddAcquisitionEventScreen acquisitionEventScreen = new AddAcquisitionEventScreen(CowRegistrationScreen.this.midlet, CowRegistrationScreen.this.locale, CowRegistrationScreen.this.farmer);
                            acquisitionEventScreen.start();
                        }
                    }
                    
                }
                else if(evt.getCommand().equals(nextCommand)) {
                    if(validateInput()) {
                        saveCowDetails();
                        if(CowRegistrationScreen.this.farmer.getMode().equals(Farmer.MODE_INITIAL_REGISTRATION)){
                            if(CowRegistrationScreen.this.cowIndex < (CowRegistrationScreen.this.cowNumber -1)) {
                                CowRegistrationScreen nextScreen = new CowRegistrationScreen(CowRegistrationScreen.this.midlet, CowRegistrationScreen.this.locale, CowRegistrationScreen.this.farmer, CowRegistrationScreen.this.cowIndex + 1, CowRegistrationScreen.this.cowNumber);
                                nextScreen.start();
                            }
                            else{
                                CowRegistrationScreen.this.farmer.syncWithServer(CowRegistrationScreen.this);
                            }                       
                        }
                        else if(CowRegistrationScreen.this.farmer.getMode().equals(Farmer.MODE_NEW_COW_REGISTRATION)){
                            CowRegistrationScreen.this.farmer.syncWithServer(CowRegistrationScreen.this);
                        }
                        
                    }
                }
            }
        });
        
        nameOrEarTagNumberTA = new TextArea(" * "+Locale.getStringInLocale(locale, StringResources.name_or_ear_tag_number));
        setMultilineLableStyle(nameOrEarTagNumberTA);
        this.addComponent(nameOrEarTagNumberTA);
        
        cowNameL = new Label(Locale.getStringInLocale(locale, StringResources.name));
        setLabelStyle(cowNameL);
        this.addComponent(cowNameL);
        
        cowNameTF = new TextField();
        setComponentStyle(cowNameTF, false);
        this.addComponent(cowNameTF);
        
        earTagNumberL = new Label(Locale.getStringInLocale(locale, StringResources.ear_tag_number));
        setLabelStyle(earTagNumberL);
        this.addComponent(earTagNumberL);
        
        earTagNumberTF = new TextField();
        setComponentStyle(earTagNumberTF, false);
        this.addComponent(earTagNumberTF);
        
        sexL = new Label(" * "+Locale.getStringInLocale(locale, StringResources.sex));
        setLabelStyle(sexL);
        this.addComponent(sexL);
        
        sexCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.sex_array));
        setComponentStyle(sexCB, true);
        sexCB.setRenderer(new MistroListCellRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.sex_array)));
        this.addComponent(sexCB);
        
        ageOrDOBTA = new TextArea(" * "+Locale.getStringInLocale(locale, StringResources.age_or_dob));
        setMultilineLableStyle(ageOrDOBTA);
        this.addComponent(ageOrDOBTA);
        
        ageL = new Label(Locale.getStringInLocale(locale, StringResources.age));
        setLabelStyle(ageL);
        this.addComponent(ageL);
        
        ageTF = new TextField();
        setComponentStyle(ageTF, false);
        ageTF.setConstraint(TextField.NUMERIC);
        ageTF.setInputModeOrder(new String[] {"123"});
        this.addComponent(ageTF);
        
        ageTypeL = new Label(Locale.getStringInLocale(locale, StringResources.age_type));
        setLabelStyle(ageTypeL);
        this.addComponent(ageTypeL);
        
        ageTypeCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.age_type_array));
        setComponentStyle(ageTypeCB, true);
        ageTypeCB.setRenderer(new MistroListCellRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.age_type_array)));
        this.addComponent(ageTypeCB);
        
        dateOfBirthL = new Label(Locale.getStringInLocale(locale, StringResources.date_of_birth));
        setLabelStyle(dateOfBirthL);
        this.addComponent(dateOfBirthL);
        
        dateOfBirthS = Spinner.createDate(System.currentTimeMillis() - (31536000730l*20), System.currentTimeMillis() + 86400000l, System.currentTimeMillis() + 86400000l, '/', Spinner.DATE_FORMAT_DD_MM_YYYY);
        //setComponentStyle(dateOfBirthS, true);
        //dateOfBirthS.getSelectedStyle().setFgColor(0x2ecc71);
        //this.addComponent(dateOfBirthS);
        dateOfBirthB = new Button(Locale.getStringInLocale(locale, StringResources.click_to_set_date));
        setComponentStyle(dateOfBirthB, true);
        dateOfBirthB.addActionListener(this);
        this.addComponent(dateOfBirthB);
        
        breedL = new Label(Locale.getStringInLocale(locale, StringResources.breed));
        setLabelStyle(breedL);
        this.addComponent(breedL);
        
        breedCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.breeds_array));
        setComponentStyle(breedCB, true);
        breedMultiselectRenderer = new MultiselectRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.breeds_array),4);
        breedCB.setRenderer(breedMultiselectRenderer);
        breedCB.addActionListener(breedMultiselectRenderer);
        this.addComponent(breedCB);
        
        deformityL = new Label(Locale.getStringInLocale(locale, StringResources.deformity));
        setLabelStyle(deformityL);
        this.addComponent(deformityL);
        
        deformityCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.deformities_array));
        deformityMultiselectRenderer = new MultiselectRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.deformities_array),0);
        deformityCB.setRenderer(deformityMultiselectRenderer);
        deformityCB.addActionListener(deformityMultiselectRenderer);
        deformityCB.addActionListener(this);
        setComponentStyle(deformityCB, true);
        //deformityCB.setRenderer(new MultiselectRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.deformities_array)));
        this.addComponent(deformityCB);
        
        otherDeformityL = new Label(Locale.getStringInLocale(locale, StringResources.other_deformity));
        setLabelStyle(otherDeformityL);
        this.addComponent(otherDeformityL);
        
        otherDeformityTF = new TextField();
        setComponentStyle(otherDeformityTF, false);
        this.addComponent(otherDeformityTF);
        
        serviceTypeL = new Label(Locale.getStringInLocale(locale, StringResources.service_type_used));
        setLabelStyle(serviceTypeL);
        this.addComponent(serviceTypeL);
        
        serviceTypeCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.service_types));
        setComponentStyle(serviceTypeCB, true);
        serviceTypeCB.setRenderer(new MistroListCellRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.service_types)));
        this.addComponent(serviceTypeCB);
        serviceTypeCB.addActionListener(this);
        
        sireL = new Label(Locale.getStringInLocale(locale, StringResources.sire));
        setLabelStyle(sireL);
        this.addComponent(sireL);
        
        String[] sireNames = getValidSires();
        sireCB = new ComboBox(sireNames);
        setComponentStyle(sireCB, true);
        sireCB.setRenderer(new MistroListCellRenderer(sireNames));
        this.addComponent(sireCB);
        
        countryL = new Label(Locale.getStringInLocale(locale, StringResources.sire_country_of_origin));
        setLabelStyle(countryL);
        this.addComponent(countryL);
        
        countryCB = new ComboBox(GeneralArrays.all_countries);
        setComponentStyle(countryCB, true);
        countryCB.setRenderer(new MistroListCellRenderer(GeneralArrays.all_countries));
        this.addComponent(countryCB);
        
        strawNumberL = new Label(Locale.getStringInLocale(locale, StringResources.straw_number));
        setLabelStyle(strawNumberL);
        this.addComponent(strawNumberL);
        
        strawNumberTF = new TextField();
        setComponentStyle(strawNumberTF, false);
        this.addComponent(strawNumberTF);
        
        damL = new Label(Locale.getStringInLocale(locale, StringResources.dam));
        setLabelStyle(damL);
        this.addComponent(damL);
        
        /*String[] damNames = getValidDams();
        damCB = new ComboBox(damNames);
        setComponentStyle(damCB, true);
        damCB.setRenderer(new MistroListCellRenderer(damNames));
        this.addComponent(damCB);*/
        damTF = new TextField();
        setComponentStyle(damTF, false);
        this.addComponent(damTF);
        
        embryoNumberL = new Label(Locale.getStringInLocale(locale, StringResources.embryo_number));
        setLabelStyle(embryoNumberL);
        this.addComponent(embryoNumberL);
        
        embryoNumberTF = new TextField();
        setComponentStyle(embryoNumberTF, false);
        this.addComponent(embryoNumberTF);
        
        restoreCowDetails();
    }
    
    private void setLabelStyle(Label label){
        label.getStyle().setMargin(10, 0, 10, 0);
        label.getSelectedStyle().setMargin(10, 0, 10,0);
    }
    
    private void setComponentStyle(Component component, boolean isFocusable){
        component.getStyle().setMargin(5, 0, 0, 0);
        component.getSelectedStyle().setMargin(5, 0, 0, 0);
        if(isFocusable){
            component.getSelectedStyle().setBgColor(0x2ecc71);
        }
    }
    
    private boolean validateInput(){
        final InformationDialog infoDialog = new InformationDialog(locale, false);
        
        if((earTagNumberTF.getText()== null || earTagNumberTF.getText().trim().length()==0) && (cowNameTF.getText() == null || cowNameTF.getText().trim().length()==0)){
            cowNameTF.requestFocus();
            infoDialog.setText(Locale.getStringInLocale(locale, StringResources.enter_ear_tag_no_or_name));
            infoDialog.show();
            return false;
        }
        
        if((ageTF.getText()== null || ageTF.getText().trim().length()==0) &&
                (dateOfBirthB.getText() == null || dateOfBirthB.getText().trim().length()==0 || dateOfBirthB.getText().equals(Locale.getStringInLocale(locale, StringResources.click_to_set_date)))){
            ageTF.requestFocus();
            infoDialog.setText(Locale.getStringInLocale(locale, StringResources.enter_age_or_dob));
            infoDialog.show();
            return false;
        }
        
        if(otherDeformityTF.isFocusable()){
            if(otherDeformityTF.getText().trim().length() == 0){
                otherDeformityTF.requestFocus();
                infoDialog.setText(Locale.getStringInLocale(locale, StringResources.spec_other_deformity));
                infoDialog.show();
                return false;
            }
        }
        return true;
    }
    
    private void saveCowDetails() {
        Cow thisCow = farmer.getCow(cowIndex);
        if(farmer.getMode().equals(Farmer.MODE_INITIAL_REGISTRATION))
            thisCow.setMode(Cow.MODE_ADULT_COW_REGISTRATION);
        
        thisCow.setName(cowNameTF.getText());
        thisCow.setEarTagNumber(earTagNumberTF.getText());
        if(ageTF.getText()!=null && ageTF.getText().trim().length() > 0){
            thisCow.setAge(Integer.parseInt(ageTF.getText().trim()));
        }
        String[] ageTypesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.age_type_array);
        thisCow.setAgeType(ageTypesInEN[ageTypeCB.getSelectedIndex()]);
        thisCow.setDateOfBirth((Date) dateOfBirthS.getValue());
        thisCow.setBreeds(breedMultiselectRenderer.getSelectedItems(Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.breeds_array)));
        String[] sexInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.sex_array);
        thisCow.setSex(sexInEN[sexCB.getSelectedIndex()]);
        thisCow.setDeformities(deformityMultiselectRenderer.getSelectedItems(Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.deformities_array)));
        if(otherDeformityTF.isFocusable()){
            thisCow.setOtherDeformity(otherDeformityTF.getText());
        }
        
        if(!thisCow.getMode().equals(Cow.MODE_BORN_CALF_REGISTRATION)){
            String[] serviceTypesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.service_types);
            thisCow.setServiceType(serviceTypesInEN[serviceTypeCB.getSelectedIndex()]);

            if(serviceTypesInEN[serviceTypeCB.getSelectedIndex()].equals(Cow.SERVICE_TYPE_BULL)) {
                Sire sire = new Sire();
                Cow selectedSire = (Cow)validSires.elementAt(sireCB.getSelectedIndex());
                sire.setName(selectedSire.getName());
                sire.setEarTagNumber(selectedSire.getEarTagNumber());

                Dam dam = new Dam();
                /*Cow selectedDam = (Cow)validDams.elementAt(damCB.getSelectedIndex());
                dam.setName(selectedDam.getName());
                dam.setEarTagNumber(selectedDam.getEarTagNumber());*/
                dam.setEarTagNumber(damTF.getText());
                thisCow.setDam(dam);
                String[] countries = GeneralArrays.all_countries;
                sire.setCountryOfOrigin(countries[countryCB.getSelectedIndex()]);
                thisCow.setSire(sire);
            }
            else if(serviceTypesInEN[serviceTypeCB.getSelectedIndex()].equals(Cow.SERVICE_TYPE_AI)){
                Sire sire = new Sire();
                sire.setStrawNumber(strawNumberTF.getText());
                thisCow.setSire(sire);

                Dam dam = new Dam();
                /*Cow selectedDam = (Cow)validDams.elementAt(damCB.getSelectedIndex());
                dam.setName(selectedDam.getName());
                dam.setEarTagNumber(selectedDam.getEarTagNumber());*/
                dam.setEarTagNumber(damTF.getText());
                thisCow.setDam(dam);
            }
            else if(serviceTypesInEN[serviceTypeCB.getSelectedIndex()].equals(Cow.SERVICE_TYPE_ET)){
                Dam dam = new Dam();
                dam.setEmbryoNumber(embryoNumberTF.getText());
                thisCow.setDam(dam);
            }
        }
    }
    
    private void restoreCowDetails() {
        Cow thisCow = farmer.getCow(cowIndex);
        
        cowNameTF.setText(thisCow.getName());
        earTagNumberTF.setText(thisCow.getEarTagNumber());
        if(thisCow.getAge()!=-1)
            ageTF.setText(String.valueOf(thisCow.getAge()));
        String[] ageTypesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.age_type_array);
        for(int i = 0; i < ageTypesInEN.length; i ++) {
            if(ageTypesInEN[i].equals(thisCow.getAgeType())){
                ageTypeCB.setSelectedIndex(i);
            }
        }
        
        //dateOfBirthS = Spinner.createDate(System.currentTimeMillis() - (31536000730l*50), System.currentTimeMillis(), System.currentTimeMillis(), '/', Spinner.DATE_FORMAT_DD_MM_YYYY);
        long dateOfBirthLong = thisCow.getDateOfBirthMilliseconds();
        if(dateOfBirthLong != -1){
            Date date = new Date(dateOfBirthLong);
            dateOfBirthS.setValue(date);//TODO: not sure this will work
        }
        else{
            Date date = new Date(System.currentTimeMillis());
            dateOfBirthS.setValue(date);
        }
        
        String[] breedsInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.breeds_array);
        //for each breed, check if breed in cow's breed array
        String[] cowBreeds = thisCow.getBreeds();
        int noSelectedBreeds = 0;
        if(cowBreeds!=null){
            for(int i = 0; i < breedsInEN.length; i++) {
                for(int j = 0; j < cowBreeds.length; j++) {
                    if(cowBreeds[j].equals(breedsInEN[i])){
                        noSelectedBreeds++;
                    }
                }
            }
            int[] cowBreedsIndexes = new int[noSelectedBreeds];
            int count=0;
            for(int i = 0; i < breedsInEN.length; i++) {
                for(int j = 0; j < cowBreeds.length; j++) {
                    if(cowBreeds[j].equals(breedsInEN[i])){
                        cowBreedsIndexes[count] = i;
                        count++;
                    }
                }
            }
            if(cowBreedsIndexes.length > 0)
                breedMultiselectRenderer.setSelectedItems(cowBreedsIndexes);
        }
        
        
        String[] sexInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.sex_array);
        for(int i = 0; i < sexInEN.length; i++){
            if(sexInEN[i].equals(thisCow.getSex())){
                sexCB.setSelectedIndex(i);
            }
        }
        
        String[] deformitiesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.deformities_array);
        String[] cowDeformities = thisCow.getDeformities();
        int noDeformities = 0;
        boolean hasOtherDeformity = false;
        if(cowDeformities!=null){
            for(int i = 0; i < deformitiesInEN.length; i++ ){
                for(int j = 0; j < cowDeformities.length; j++){
                    if(deformitiesInEN[i].equals(cowDeformities[j])){
                        noDeformities++;
                    }
                    if(cowDeformities[j].equals("Other")){
                        hasOtherDeformity = true;
                    }
                }
            }
            int[] cowDeformitiesIndexes = new int[noDeformities];
            int count = 0;
            for(int i = 0; i < deformitiesInEN.length; i++ ){
                for(int j = 0; j < cowDeformities.length; j++){
                    if(deformitiesInEN[i].equals(cowDeformities[j])){
                        cowDeformitiesIndexes[count] = i;
                        count++;
                    }
                }
            }
            if(cowDeformitiesIndexes.length>0)
                deformityMultiselectRenderer.setSelectedItems(cowDeformitiesIndexes);
        }
        if(hasOtherDeformity){
            otherDeformityTF.setText(thisCow.getOtherDeformity());
        }
        deformitySelected();
        
        String[] serviceTypesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.service_types);
        String serviceType = null;
        for(int i =0; i < serviceTypesInEN.length; i++){
            if(serviceTypesInEN[i].equals(thisCow.getServiceType())){
                serviceTypeCB.setSelectedIndex(i);
                serviceType = serviceTypesInEN[i];
            }
        }
        serviceTypeSelected();
        
        if(serviceType!=null){
            if(serviceType.equals(Cow.SERVICE_TYPE_BULL)){
                Sire cowSire = thisCow.getSire();
                if(cowSire != null) {
                    for(int i = 0; i < validSires.size(); i++){
                        Cow currentSire = (Cow)validSires.elementAt(i);
                        if(currentSire.getName().equals(cowSire.getName()) && currentSire.getEarTagNumber().equals(cowSire.getEarTagNumber())){
                            sireCB.setSelectedIndex(i);
                        }
                    }
                }
                
                if(cowSire!=null){
                    String[] countries = GeneralArrays.all_countries;
                    for(int i = 0; i<countries.length;i++){
                        if(countries[i].equals(cowSire.getCountryOfOrigin())){
                            countryCB.setSelectedIndex(i);
                        }
                    }
                }
                
                Dam cowDam = thisCow.getDam();
                if(cowDam != null) {
                    damTF.setText(cowDam.getEarTagNumber());
                    /*for(int i = 0; i < validDams.size(); i++){
                        Cow currentDam = (Cow)validDams.elementAt(i);
                        if(currentDam.getName().equals(cowDam.getName()) && currentDam.getEarTagNumber().equals(cowDam.getEarTagNumber())){
                            sireCB.setSelectedIndex(i);
                        }
                    }*/
                }
            }
            
            else if(serviceType.equals(Cow.SERVICE_TYPE_AI)){
                Sire cowSire = thisCow.getSire();
                if(cowSire!=null){
                    strawNumberTF.setText(cowSire.getStrawNumber());
                }
                
                Dam cowDam = thisCow.getDam();
                if(cowDam != null) {
                    damTF.setText(cowDam.getEarTagNumber());
                    /*for(int i = 0; i < validDams.size(); i++){
                        Cow currentDam = (Cow)validDams.elementAt(i);
                        if(currentDam.getName().equals(cowDam.getName()) && currentDam.getEarTagNumber().equals(cowDam.getEarTagNumber())){
                            sireCB.setSelectedIndex(i);
                        }
                    }*/
                }
            }
            
            else if(serviceType.equals(Cow.SERVICE_TYPE_ET)){
                Dam cowDam = thisCow.getDam();
                if(cowDam!=null){
                    embryoNumberTF.setText(cowDam.getEmbryoNumber());
                }
            }
        }
        
        if(thisCow.getMode().equals(Cow.MODE_BORN_CALF_REGISTRATION)){
            System.out.println("Mode is calf");
            setLabelFocusable(serviceTypeL, false);
            setComponentFocusable(serviceTypeCB, false);
            setLabelFocusable(sireL, false);
            setComponentFocusable(sireCB, false);
            setLabelFocusable(strawNumberL, false);
            setComponentFocusable(strawNumberTF, false);
            setLabelFocusable(damL, false);
            //setComponentFocusable(damCB, false);
            setComponentFocusable(damTF, false);
            setLabelFocusable(embryoNumberL, false);
            setComponentFocusable(embryoNumberTF, false);
            setLabelFocusable(countryL, false);
            setComponentFocusable(countryCB, false);
        }
        else{
            System.out.println("Mode is cow");
        }
    }
    
    private String[] getValidSires() {
        Cow[] allCows = farmer.getCows();
        
        validSires = new Vector(allCows.length);
        validSireNames = new Vector(allCows.length);
        //add placibo
        validSires.addElement(new Cow(true));
        validSireNames.addElement(" ");
        
        for(int i = 0;i < allCows.length; i++) {
            Cow currentCow = allCows[i];
            if(i != cowIndex && currentCow.getEarTagNumber() != null && currentCow.getEarTagNumber().length() > 0 && currentCow.getSex().equals(Cow.SEX_MALE)) {
                validSires.addElement(currentCow);
                if(currentCow.getName()!= null && currentCow.getName().trim().length() > 0)
                    validSireNames.addElement((currentCow.getEarTagNumber()+" ("+currentCow.getName()+")"));
                else
                    validSireNames.addElement(currentCow.getEarTagNumber());
            }
        }
        
        String[] sireNames = new String[validSireNames.size()];
        for(int i = 0; i < validSireNames.size(); i++) {
            sireNames[i] = (String)validSireNames.elementAt(i);
        }
        
        return sireNames;
    }
    
    private String[] getValidDams() {
        Cow[] allCows = farmer.getCows();
        
        validDams = new Vector(allCows.length);
        validDamNames = new Vector(allCows.length);
        //add placibo
        validDams.addElement(new Cow(true));
        validDamNames.addElement(" ");
        
        for(int i = 0;i < allCows.length; i++) {
            Cow currentCow = allCows[i];
            if(i != cowIndex && currentCow.getEarTagNumber() != null && currentCow.getEarTagNumber().length() > 0 && currentCow.getSex().equals(Cow.SEX_FEMALE)) {
                validDams.addElement(currentCow);
                if(currentCow.getName()!= null && currentCow.getName().trim().length() > 0)
                    validDamNames.addElement((currentCow.getEarTagNumber()+" ("+currentCow.getName()+")"));
                else
                    validDamNames.addElement((currentCow.getEarTagNumber()));
            }
        }
        
        String[] damNames = new String[validDamNames.size()];
        for(int i = 0; i < validDamNames.size(); i++) {
            damNames[i] = (String)validDamNames.elementAt(i);
        }
        
        return damNames;
    }
    
    private void serviceTypeSelected(){
        String[] serviceTypesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.service_types);
        int selectedIndex = serviceTypeCB.getSelectedIndex();
        System.out.println(serviceTypesInEN[selectedIndex]);
        if(serviceTypesInEN[selectedIndex].equals(Cow.SERVICE_TYPE_BULL)){
            setLabelFocusable(sireL, true);
            setComponentFocusable(sireCB, true);
            
            setLabelFocusable(damL, true);
            //setComponentFocusable(damCB, true);
            setComponentFocusable(damTF, true);
            
            setLabelFocusable(countryL, true);
            setComponentFocusable(countryCB, true);
            
            setLabelFocusable(strawNumberL, false);
            setComponentFocusable(strawNumberTF, false);
            
            setLabelFocusable(embryoNumberL, false);
            setComponentFocusable(embryoNumberTF, false);
        }
        else if(serviceTypesInEN[selectedIndex].equals(Cow.SERVICE_TYPE_AI)){
            setLabelFocusable(sireL, false);
            setComponentFocusable(sireCB, false);
            
            setLabelFocusable(damL, true);
            //setComponentFocusable(damCB, true);
            setComponentFocusable(damTF, true);
            
            setLabelFocusable(countryL, false);
            setComponentFocusable(countryCB, false);
            
            setLabelFocusable(strawNumberL, true);
            setComponentFocusable(strawNumberTF, true);
            
            setLabelFocusable(embryoNumberL, false);
            setComponentFocusable(embryoNumberTF, false);
        }
        else if(serviceTypesInEN[selectedIndex].equals(Cow.SERVICE_TYPE_ET)){
            setLabelFocusable(sireL, false);
            setComponentFocusable(sireCB, false);
            
            setLabelFocusable(damL, false);
            //setComponentFocusable(damCB, false);
            setComponentFocusable(damTF, false);
            
            setLabelFocusable(countryL, false);
            setComponentFocusable(countryCB, false);
            
            setLabelFocusable(strawNumberL, false);
            setComponentFocusable(strawNumberTF, false);
            
            setLabelFocusable(embryoNumberL, true);
            setComponentFocusable(embryoNumberTF, true);
        }
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
        if(evt.getComponent().equals(serviceTypeCB)){
            serviceTypeSelected();
        }
        else if(evt.getComponent().equals(deformityCB)){
            deformitySelected();
        }
        if(evt.getComponent().equals(dateOfBirthB)){
            if(dateDialog == null){
                dateDialog = new DateDialog(locale, dateOfBirthS, this);
                dateDialog.setText(Locale.getStringInLocale(locale, StringResources.enter_date));
            }
            
            dateDialog.show();
        }
    }
    
    private void deformitySelected(){
        System.out.println("deformity selected");
        String[] selectedDeformities = deformityMultiselectRenderer.getSelectedItems(Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.deformities_array));
        boolean hasOther = false;
        if(selectedDeformities != null){
            for(int i = 0; i < selectedDeformities.length; i++){
                if(selectedDeformities[i].equals("Other")){
                    hasOther = true;
                }
            }
            if(hasOther){
                setLabelFocusable(otherDeformityL, true);
            setComponentFocusable(otherDeformityTF, true);
            }
            else{
                setLabelFocusable(otherDeformityL, false);
            setComponentFocusable(otherDeformityTF, false);
            }
        }
        else{
            setLabelFocusable(otherDeformityL, false);
            setComponentFocusable(otherDeformityTF, false);
        }
        
    }
    
    private void setLabelFocusable(Label label, boolean focusable){
        if(focusable){
            label.getStyle().setFgColor(0x000000);
        }
        else{
            label.getStyle().setFgColor(0xC0C0C0);
        }
    }
    
    private void setComponentFocusable(Component component, boolean focusable){
        if(focusable){
            component.getStyle().setBgColor(0xFFFFFF);
            component.setFocusable(true);
        }
        else{
            component.getStyle().setBgColor(0xC0C0C0);
            component.setFocusable(false);
        }
    }
    
    private void setMultilineLableStyle(TextArea textArea){
        textArea.getStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_SMALL));
        textArea.setRows(2);  
        textArea.setVisible(true);  
        textArea.setEditable(false); 
        textArea.setFocusable(false);
        Border border= Border.createEmpty();
        textArea.getStyle().setBorder(border);
        textArea.getStyle().setMargin(10, 0, 10, 0);
    }

    public void responseGotten(Object source, String message) {
        System.out.println("Response gotten from server");
        if(farmer.getMode().equals(Farmer.MODE_INITIAL_REGISTRATION)){
            System.out.println("farmer mode is initial reg");
            if(message == null){
                final InformationDialog infoDialog = new InformationDialog(Locale.getStringInLocale(locale, StringResources.error), locale, false);
                infoDialog.setDialogType(Dialog.TYPE_ERROR);

                infoDialog.setText(Locale.getStringInLocale(locale, StringResources.something_went_wrong_try_again));
                infoDialog.show();
            }
            else if(message.equals(DataHandler.ACKNOWLEDGE_OK)){
                final InformationDialog infoDialog = new InformationDialog(Locale.getStringInLocale(locale, StringResources.successful_registration), locale, true);
                infoDialog.setDialogType(Dialog.TYPE_CONFIRMATION);
                
                infoDialog.addCommandListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if(evt.getCommand().equals(infoDialog.getBackCommand())){
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
                
                if(message.equals(DataHandler.CODE_NUMBER_IN_USE))
                    infoDialog.setText(Locale.getStringInLocale(locale, StringResources.number_in_use));
                else
                    infoDialog.setText(Locale.getStringInLocale(locale, StringResources.something_went_wrong_try_again));
                infoDialog.show();
            }
        }
        else if(farmer.getMode().equals(Farmer.MODE_NEW_COW_REGISTRATION)){
            System.out.println("farmer mode is new cow reg");
            Cow thisCow = farmer.getCow(cowIndex);
            System.out.println("cow index = "+String.valueOf(cowIndex));
            if(thisCow.getMode().equals(Cow.MODE_BORN_CALF_REGISTRATION)){
                System.out.println("response gotten for calf");
               if(message == null){
                   final InformationDialog infoDialog = new InformationDialog(Locale.getStringInLocale(locale, StringResources.error), locale, false);
                    infoDialog.setDialogType(Dialog.TYPE_ERROR);
                    
                    infoDialog.setText(Locale.getStringInLocale(locale, StringResources.problem_connecting_to_server));
                    infoDialog.show();
               }
               else if(message.equals(DataHandler.ACKNOWLEDGE_OK)){
                    farmer.update();
                    final InformationDialog infoDialog = new InformationDialog(Locale.getStringInLocale(locale, StringResources.successful_registration), locale, true);
                    
                    infoDialog.addCommandListener(new ActionListener() {

                        public void actionPerformed(ActionEvent evt) {
                            if(evt.getCommand().equals(infoDialog.getBackCommand())){
                                FertilityScreen fertilityScreen = new FertilityScreen(midlet, locale, farmer);
                                fertilityScreen.start();
                            }
                        }
                    });
                    
                    infoDialog.setText(Locale.getStringInLocale(locale, StringResources.information_successfully_sent_to_server));
                    infoDialog.show();
               }
               else{
                   final InformationDialog infoDialog = new InformationDialog(Locale.getStringInLocale(locale, StringResources.error), locale, false);
                    infoDialog.setDialogType(Dialog.TYPE_ERROR);
                    
                    infoDialog.setText(Locale.getStringInLocale(locale, StringResources.something_went_wrong_try_again));
                    infoDialog.show();
               }
               
            }
            
            else if(thisCow.getMode().equals(Cow.MODE_ADULT_COW_REGISTRATION)){
                if(message == null){
                   final InformationDialog infoDialog = new InformationDialog(Locale.getStringInLocale(locale, StringResources.error), locale, false);
                    infoDialog.setDialogType(Dialog.TYPE_ERROR);
                    
                    infoDialog.setText(Locale.getStringInLocale(locale, StringResources.problem_connecting_to_server));
                    infoDialog.show();
               }
               else if(message.equals(DataHandler.ACKNOWLEDGE_OK)){
                    farmer.update();
                    final InformationDialog infoDialog = new InformationDialog(Locale.getStringInLocale(locale, StringResources.successful_registration), locale, true);
                    
                    infoDialog.addCommandListener(new ActionListener() {

                        public void actionPerformed(ActionEvent evt) {
                            if(evt.getCommand().equals(infoDialog.getBackCommand())){
                                EventsScreen eventsScreen = new EventsScreen(midlet, locale, farmer);
                                eventsScreen.start();
                            }
                        }
                    });
                    
                    infoDialog.setText(Locale.getStringInLocale(locale, StringResources.information_successfully_sent_to_server));
                    infoDialog.show();
               }
               else{
                    final InformationDialog infoDialog = new InformationDialog(Locale.getStringInLocale(locale, StringResources.error), locale, false);
                    infoDialog.setDialogType(Dialog.TYPE_ERROR);
                    
                    infoDialog.setText(Locale.getStringInLocale(locale, StringResources.something_went_wrong_try_again));
                    infoDialog.show();
               }
            }
        }
        
    }

    public void dateSelected(Spinner spinner, Date date) {
        dateOfBirthB.setText(dateDialog.dateToString((Date)dateOfBirthS.getValue()));
    }
    
}