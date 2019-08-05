/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lionheartwebtech.airplanetracker;

/**
 *
 * @author chunc
 */
public class InterestingFlight {
    private int flightId;
    private int year;
    private int timeWeHaveSeen;
    private String aircraft;
    private String model;
    private String latitude;
    private String longitude;


    public InterestingFlight(int flightId, String aircraft, int year, int timeWeHaveSeen, String model, String latitude, String longitude) {
        this.flightId = flightId;
        this.aircraft = aircraft;
        this.year = year;
        this.timeWeHaveSeen = 0;
        this.model = model;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getFlightId() {
        return flightId;
    }

    public String getAircraft(){
        return aircraft;
    }

      public int getYear() {
        return year;
    }

    public int getTimeWeHaveSeen() {
        return timeWeHaveSeen;
    }

    public String getModel() {
        return model;
    }


    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void addTimeWeHaveSeen() {
        this.timeWeHaveSeen ++;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    

    public String getLatitude(){
        return latitude;
    }
    
    public String getLongitude(){
        return longitude;
    }
    
    public static int compareThem(InterestingFlight a, InterestingFlight b) {
    return a.aircraft.compareTo(b.aircraft);
}
    @Override
    public String toString() {
        return "Flight ID: " + this.flightId + "/n Model: " + this.model;
    }


}
   
