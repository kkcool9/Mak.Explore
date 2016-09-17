package com.kk.makexplore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;


@JsonIgnoreProperties({ "selection" })
public class UserData implements Serializable{
    private String FirstName;
    private String LastName;
    private String Email;
    private String Contact;
    private String Gender;
    private String EventsHosted;

    // Default Constructor : Necessary for Firebase to deserializable UserData.
    public UserData(){}

    // Getter Methods
    public String getFirstName(){
        return FirstName;
    }
    public String getLastName(){
        return LastName;
    }
    public String getEmail(){
        return Email;
    }
    public String getContact(){
        return Contact;
    }
    public String getGender(){
        return Gender;
    }
    public String getEventsHosted(){
        return EventsHosted;
    }
}
