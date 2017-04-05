package no.nc_spectrum.hendelseapplication;

/**
 * Created by Amir on 13.03.2017.
 */

public class User { //protected?
    private int userID;
    private String phoneNumber;
    private String userPass;
    private String userRealName;
    // array for sid-er ?
    private HendelseRegister hendelseRegister;
    // admin boolean verdi?


    public User( int userID, String phoneNumber, String userPass, String userRealName)
    {
        this.userID = userID;
        this.phoneNumber = phoneNumber;
        this.userPass = userPass;
        this.userRealName = userRealName;
        //TODO: implementere metode som initialiserer event registeret
    }

    public int getUserID() {
        return userID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUserPass() {
        return userPass;
    }

    public String getUserRealName() {
        return userRealName;
    }

    //TODO: lag get-metode for event-registeret og metode som laster inn data fra databasene til registeret


}
