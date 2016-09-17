package com.kk.makexplore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;


@JsonIgnoreProperties({ "selection" })
public class ExperienceData implements Serializable{

    private String StartLocation;
    private String EndLocation;
    private String EXPDate;
    private String EXPTime;
    private String EXPDescription;
    private String Amount;
    private String PhotoURL;
    private String EXPTitle;

    // Default Constructor : Necessary for Firebase to deserializable UserData.
    public ExperienceData(){}

    // Getter Methods
    public String getStartLocation(){
        return StartLocation;
    }
    public String getEndLocation(){return EndLocation;}
    public String getEXPDate(){
        return EXPDate;
    }
    public String getEXPTime(){
        return getEXPTime();
    }
    public String getEXPDescription(){
        return EXPDescription;
    }
    public String getAmount(){
        return Amount;
    }
    public String getPhotoURL(){return PhotoURL;}
    public String getEXPTitle(){
        return EXPTitle;
    }
}
