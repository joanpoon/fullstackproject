package com.lionheartwebtech.airplanetracker;

import com.google.gson.*;
import org.apache.log4j.Logger;
import java.util.*;
import java.io.*;
import java.net.*;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;

import org.json.*;

public class PlaneJson {

    private static final Logger logger = Logger.getLogger(AirplaneTrackerServlet.class.getName());
    List<InterestingFlight> flights;
    
    public PlaneJson() {
        this.flights = buildEventList();
    }
    
    public PlaneJson(ArrayList<String> usersSavedFlight) {
        
        JsonObject flightDetail = convertFromUrlToJsonObject();
        JsonArray acList = flightDetail.get("acList").getAsJsonArray();
        JsonArray properties = getJsonArrayByName("properties", acList);
        
        List<InterestingFlight> flights = new ArrayList<>();
        
        Iterator<JsonElement> itProp = acList.iterator();
        JsonObject currentProperty = null;
        
        String flightId;
        String year;
        int numericYear;
        int timeWeHaveSeen = 0;
        String aircraft;
        String model;
        String latitude;
        String longitude;
        
        for (JsonElement elem : acList) {
            
            currentProperty = itProp.next().getAsJsonObject();            
            
            if ("true".equals(currentProperty.get("Interested").getAsString())) {
                //check id to see if user saved this flight, if 
                try {
                    flightId = currentProperty.get("Id").getAsString();
                    for (int i = 0; i < usersSavedFlight.size(); i++) {
                        
                        if (usersSavedFlight.get(i).equals(flightId)) {
                            logger.info("found flight with same ID: " + flightId);
                            try {
                                year = currentProperty.get("Year").getAsString();
                                numericYear = Integer.parseInt(year);
                            } catch (Exception e) {
                                year = "0000";
                                numericYear = 0;
                                logger.error("Error or empty year field, using default value" + e.getMessage());
                                
                            }
                            
                            timeWeHaveSeen = 0;
                            try {
                                aircraft = currentProperty.get("Mdl").getAsString();
                                model = currentProperty.get("Mdl").getAsString();
                            } catch (Exception e) {
                                logger.error("Error or empty aircraft model field, using default value" + e.getMessage());
                                aircraft = "unknown";
                                model = "unknown";
                            }
                            
                            try {
                                latitude = currentProperty.get("Lat").getAsString();
                            } catch (Exception e) {
                                logger.error("Error or empty latitude field, using default value" + e.getMessage());
                                latitude = "0";
                            }
                            
                            try {
                                longitude = currentProperty.get("Long").getAsString();
                            } catch (Exception e) {
                                logger.error("Error or empty latitude field, using default value" + e.getMessage());
                                longitude = "0";
                            }
                            
                            InterestingFlight flight = new InterestingFlight(Integer.parseInt(flightId), aircraft, numericYear, timeWeHaveSeen, model, latitude, longitude);
                            flights.add(flight);
                            logger.info(flight.toString() + "is added.");
                        }
                        
                    }
                } catch (Exception e) {
                    logger.error("Error or empty ID field, using default value" + e.getMessage());
                    flightId = "000000";
                }
                
            }
            
        }
        logger.info("Flights: " + flights.toString() );
        
        if(flights == null || flights.isEmpty()){
            logger.info("None of the saved flights is flying...");
        }else{
            logger.info("Found saved flights..");
        }
        
        this.flights = flights;
    }
    
    public List<InterestingFlight> getFlights() {
        return new ArrayList<>(flights);
    }
    
    public List<InterestingFlight> buildEventList() {
        
        JsonObject flightDetail = convertFromUrlToJsonObject();
        JsonArray acList = flightDetail.get("acList").getAsJsonArray();
        JsonArray properties = getJsonArrayByName("properties", acList);
        
        List<InterestingFlight> flights = new ArrayList<>();
        
        Iterator<JsonElement> itProp = acList.iterator();
        JsonObject currentProperty = null;
        
        String flightId;
        String year;
        int numericYear;
        int timeWeHaveSeen = 0;
        String aircraft;
        String model;
        String latitude;
        String longitude;
        
        for (JsonElement elem : acList) {
            
            currentProperty = itProp.next().getAsJsonObject();            
            
            if ("true".equals(currentProperty.get("Interested").getAsString())) {
                try {
                    flightId = currentProperty.get("Id").getAsString();
                } catch (Exception e) {
                    logger.error("Error or empty ID field, using default value" + e.getMessage());
                    flightId = "000000";
                }
                
                try {
                    year = currentProperty.get("Year").getAsString();
                    numericYear = Integer.parseInt(year);
                } catch (Exception e) {
                    year = "0000";
                    numericYear = 0;
                    logger.error("Error or empty year field, using default value" + e.getMessage());
                    
                }
                
                timeWeHaveSeen = 0;
                try {
                    aircraft = currentProperty.get("Mdl").getAsString();
                    model = currentProperty.get("Mdl").getAsString();
                } catch (Exception e) {
                    logger.error("Error or empty aircraft model field, using default value" + e.getMessage());
                    aircraft = "unknown";
                    model = "unknown";
                }
                
                try {
                    latitude = currentProperty.get("Lat").getAsString();
                } catch (Exception e) {
                    logger.error("Error or empty latitude field, using default value" + e.getMessage());
                    latitude = "0";
                }
                
                try {
                    longitude = currentProperty.get("Long").getAsString();
                } catch (Exception e) {
                    logger.error("Error or empty latitude field, using default value" + e.getMessage());
                    longitude = "0";
                }
                    
                if(((Double.parseDouble(latitude) < 49.8576) && (Double.parseDouble(longitude) > -126.2196)) && ((Double.parseDouble(latitude) > 23.6693) && (Double.parseDouble(longitude) < -63.8752)))
                {
                    InterestingFlight flight = new InterestingFlight(Integer.parseInt(flightId), aircraft, numericYear, timeWeHaveSeen, model, latitude,longitude);
                    flights.add(flight);
                    logger.info(flight.toString() + "is added.");
                }

                
                InterestingFlight flight = new InterestingFlight(Integer.parseInt(flightId), aircraft, numericYear, timeWeHaveSeen, model, latitude, longitude);
                flights.add(flight);
                logger.info(flight.toString() + "is added.");
                

            }
            
        }
        return flights;
    }
    
    private JsonArray getJsonArrayByName(String name, JsonArray array) {
        Iterator<JsonElement> it = array.iterator();
        JsonArray result = new JsonArray();
        JsonElement current = null;
        
        while (it.hasNext()) {
            current = it.next();
            result.add(current.getAsJsonObject().getAsJsonObject(name));
        }
        if (result.size() > 0) {
            return result;
        }
        return null;
    }
    
    private JsonObject convertFromUrlToJsonObject() {
        String url = "https://public-api.adsbexchange.com/VirtualRadar/AircraftList.json?fTypQN=";
        InputStream inputStream = null;
        JsonObject json = null;
        BufferedReader reader = null;
        JsonElement jsonElement = null;
        
        try {            
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
            
            String jsonContent = readContents(reader);
            JsonParser jsonParser = new JsonParser();
            jsonElement = jsonParser.parse(jsonContent);
            if (jsonElement.isJsonObject()) {
                json = jsonElement.getAsJsonObject();
            }
            
        } catch (Exception e) {
            logger.error("Unable to process USGS data feed...\n" + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    logger.error("Unable to close inputStream...\n" + ex.getMessage());
                }
            }
        }
        return json;
    }
    
    private String readContents(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        int character;
        while ((character = reader.read()) != -1) {
            sb.append((char) character);
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < flights.size(); i++) {
            sb.append(flights.get(i)).append("\n\n");
        }
        return sb.toString();
    }
}
