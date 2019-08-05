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
public class User {

    private int userId;
    private String userName;
    private String password;
    private String email;
    private String interestingFlights;

    public User(int userId, String userName, String password,String email, String interestingFlights) {
        if (userId < 0 || userId > 999999) {
            throw new IllegalStateException();
        }
        
        this.email=email;
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.interestingFlights = interestingFlights;
    }

    public User(String userName, String password, String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public int getUserId() {
        return this.userId;
    }

    public boolean checkUserId(int i) {
        if (i == this.userId) {
            return true;
        }
        return false;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInterestingFlights() {
        return interestingFlights;
    }

    public void setInterestingFlights(String interestingFlights) {
        this.interestingFlights = interestingFlights;
    }

    @Override
    public String toString() {
        return "Username: " + userName + "/n UserID: " + userId;
    }

}
