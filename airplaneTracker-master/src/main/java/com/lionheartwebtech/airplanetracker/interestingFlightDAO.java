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
public class interestingFlightDAO {

    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    public static void insertFlightToDB(Connection conn, InterestingFlight flight) {

        logger.info("trying to insert new flight to the DB.");

        int id = flight.getFlightId();

        if (checkIfFlightAlreadyInDB(conn, id)) {

            updateTheFlightDetail(conn, flight);
            logger.info("Flight exists, updated info");
        } else {

            String queryInsert = "insert into interestingFlights( flightId,aircraft,madeYear,model,latitude,longitude)";
            queryInsert += "values(?,?,?,?,?,?)";
            try {
                PreparedStatement pst = conn.prepareStatement(queryInsert);
                pst.setInt(1, flight.getFlightId());
                pst.setString(2, flight.getAircraft());
                pst.setInt(3, flight.getYear());
                pst.setString(4, flight.getModel());
                pst.setString(5, flight.getLongitude());
                pst.setString(6, flight.getLatitude());

                pst.execute();
                pst.close();

                logger.info("...done inserting.");

            } catch (SQLException ex) {
                logger.info("Sql error while trying yo insert: " + ex);
            }

        }

    }

    public static boolean checkIfFlightAlreadyInDB(Connection conn, int flightId) {

        String query = "select *";
        query += " From interestingFlights";
        query += " where flightId=?";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, flightId);

            ResultSet rs = stmt.executeQuery();
            List<Map<String, String>> a = convertToList(rs);
            if (a.isEmpty()) {
                return false;
            } else {
                return true;
            }

        } catch (SQLException ex) {
            System.out.println(ex);

        }
        return true;
    }

    public static List<Map<String, String>> getInterestingFlightList(Connection conn, int flightId) {

        String query = "select *";
        query += " From interestingFlights";
        query += " where flightId=?";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, flightId);

            ResultSet rs = stmt.executeQuery();
            List<Map<String, String>> aircraftList = convertToList(rs);
            
                return aircraftList;
            

        } catch (SQLException ex) {
            System.out.println(ex);

        }
        return null;
        
    }
    
    public static InterestingFlight getInterestingFlight(Connection conn, int flightId) {

        String query = "select *";
        query += " From interestingFlights";
        query += " where flightId=?";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, flightId);

            ResultSet rs = stmt.executeQuery();
           while (rs.next()) {
                int Id = rs.getInt(2);
                int year= rs.getInt(3);
                int timeWeHaveSeen= 0;
                String aircraft= rs.getString(1);
                String model= rs.getString(4);
                String latitude= "0";
                String longitude= "0";

                InterestingFlight myFlight = new InterestingFlight(Id, aircraft, year, timeWeHaveSeen, model, latitude, longitude);
                
                return myFlight;

            }
            
            
            

        } catch (SQLException ex) {
            System.out.println(ex);

        }
        return null;
        
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

    public static void updateTheFlightDetail(Connection conn, InterestingFlight flight) {
        String updateQuery = "UPDATE interestingFlights ";
        updateQuery += "SET latitude = ?, longitude = ?";
        updateQuery += "WHERE flightId=?";
        try {
            PreparedStatement pst = conn.prepareStatement(updateQuery);
            pst.setString(1, flight.getLatitude());
            pst.setString(2, flight.getLongitude());
            pst.setInt(3, flight.getFlightId());

            pst.execute();
            pst.close();

            System.out.println("...updating.");

        } catch (SQLException ex) {
            System.out.println("Sql error while trying to update: " + ex);
        }

    }

}
