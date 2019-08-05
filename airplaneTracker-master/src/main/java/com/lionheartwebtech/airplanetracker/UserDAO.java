/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lionheartwebtech.airplanetracker;

import java.sql.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 *
 * @author chunc
 */
public class UserDAO {
    
    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    public static ArrayList<String> getUserInterestingFlightList(Connection conn, String username){
        
        logger.info("trying to get User current Flight list");
        
        String query = "SELECT *";
        query += " from User WHERE username=? ";
        
        String s=null;

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            
            ResultSet rs = stmt.executeQuery();

            List<Map<String, String>> a = convertToList(rs);
            s=a.get(0).get("interestedFlights");
           
        } catch (SQLException ex) {
            logger.error(ex);
        }
        
      if (s==null){
          logger.info("user currently has an empty list");
         return null;
      }
         
      ArrayList<String> flightList = new ArrayList<String>(Arrays.asList((s.split(","))));
        
     
        return flightList; 
    }
    
    public static void addInterestingFlightsToUser(Connection conn,InterestingFlight flight, String username){
        String userFlightList="";
        
        int flightId= flight.getFlightId();
        
        ArrayList<String> flightListBeforeInsert= getUserInterestingFlightList(conn, username);
        
        if(flightListBeforeInsert==null){
            userFlightList=Integer.toString(flightId);
        }else{
            for(String s:flightListBeforeInsert){
                userFlightList+=s;
                userFlightList+=",";
            }
            userFlightList+=Integer.toString(flightId);
        }
       
        String updateQuery= "update User Set interestedFlights= ? ";
        updateQuery+="where username=?";
        
        try{
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            stmt.setString(1, userFlightList);
            stmt.setString(2, username);
            
            stmt.execute();
            stmt.close();
            
        }catch(SQLException e){
        logger.error(e);
        }
            
       
    }
    
    public static void changePassWord(){}
    
    public static void deleteUser(){}
    
    
    public static void deleteInterestingFlight(Connection conn, int flightToDelete, String username) {

        String userFlightList = "";
        String deleteFlight= Integer.toString(flightToDelete);

        ArrayList<String> flightListBeforeInsert = getUserInterestingFlightList(conn, username);

        if (flightListBeforeInsert == null) {
            logger.info("yout list is empty. nothing to delete.");
        } else {
            for(String s : flightListBeforeInsert) {
                
                if (s.equalsIgnoreCase(deleteFlight)!=true) {
                    
                    userFlightList += s;
                    userFlightList += ",";
                }
            }
        }
        userFlightList = userFlightList.replaceAll(",$", "");
        
        String updateQuery = "update User Set interestedFlights= ? ";
        updateQuery += "where username=?";

        try {
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            stmt.setString(1, userFlightList);
            stmt.setString(2, username);

            stmt.execute();
            stmt.close();

        } catch (SQLException e) {
            logger.error(e);
        }

    }
    
    
    
    public static void insertUserFromSignUpPage(Connection conn, String username, String pw, String email) {

        logger.info("inside dao, trying to insert to db");
        String query = "INSERT INTO User";
        query += "(username, pw, email)";
        query += " VALUES (?,?,?)";
        
        
        try {
            logger.info("Inside DAO try block..");
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, pw);
            pst.setString(3, email);
            logger.info(pst);
            pst.execute();
            pst.close();
            logger.info("done inserting");
        } catch (SQLException e) {
            logger.error("SQL Expection: " + e, e);
            
        }
    }
    
    public static int getUserIdByUserName(Connection conn, String username) {
        String query = "SELECT userId as USERID";
        query += " from User WHERE username=? ";
        
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            
            ResultSet rs = stmt.executeQuery();
            List<String> result = new ArrayList<>();
            while (rs.next()) {
                
                result.add(rs.getString("USERID"));
            }
            
            return Integer.parseInt(result.get(0));
            
        } catch (SQLException ex) {
            logger.error(ex);
            return 0;
        }
    }
    
    private static List<Map<String, String>> convertToList(ResultSet rs) {

        List<Map<String, String>> results = new ArrayList<>();

        try {
            ResultSetMetaData rsMetaData = rs.getMetaData();

            int nColumns = rsMetaData.getColumnCount();
            String[] columns = new String[nColumns];

            for (int i = 0; i < nColumns; i++) {
                columns[i] = rsMetaData.getColumnName(i + 1);
            }

            Map<String, String> row = new HashMap<>();
            while (rs.next()) {
                row = new HashMap<>();
                for (int i = 0; i < nColumns; i++) {
                    row.put(columns[i], rs.getString(i + 1));
                }
                results.add(row);
            }
        } catch (SQLException e) {

            return null;
        }

        return results;
    }
    
    public static boolean checkIfTheUsernameCanBeUse(Connection conn, String username) {

        String query = "select *";
        query += " From User";
        query += " where username=?";
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            List<Map<String, String>> result = convertToList(rs);
            if (result.isEmpty()) {
                return true;
            } else {
                return false;
            }
            
        } catch (Exception ex) {
            logger.error("username cannot be use");
            return true;
        }

    }
    
    public static User createUserObjectByUsername(Connection conn, String username,String pw) {
        String query = "SELECT *";
        query += " from User WHERE username=? and pw= ?";
        
        try {
            logger.info("preparestatement in DAO");
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2,pw);
            ResultSet rs = stmt.executeQuery();

            List<Map<String, String>> result = convertToList(rs);
            logger.info(result.toString());
            
         return new User(Integer.parseInt(result.get(0).get("userID")),username,result.get(0).get("pw"),
                 result.get(0).get("email"),result.get(0).get("interestedFlights"));
       
        } catch (SQLException ex) {
            logger.error("sql query error");
        }catch(Exception e){
           logger.error("username or password not correct");
           return null;
        }
return null;
    }
}
