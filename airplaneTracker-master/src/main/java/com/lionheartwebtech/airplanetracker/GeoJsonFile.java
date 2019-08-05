/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lionheartwebtech.airplanetracker;
import org.apache.log4j.Logger;
import org.json.*;
/**
 *
 * @author javier Sarmiento
 */
public class GeoJsonFile {
    
     private static final Logger logger = Logger.getLogger(AirplaneTrackerServlet.class.getName());
     private InterestingFlight[] myFlights;
     
     public GeoJsonFile(InterestingFlight[] myFlights){
         this.myFlights = myFlights;
     }
     
     public JSONObject getGeoJsonFile(){
          JSONObject featureCollection = new JSONObject();
        try {
            featureCollection.put("type", "FeatureCollection");
            JSONArray featureList = new JSONArray();
            
            // iterate through your list
            for (InterestingFlight obj : myFlights) {
                // {"geometry": {"type": "Point", "coordinates": [-94.149, 36.33]}
                JSONObject point = new JSONObject();
                point.put("type", "Point");
                
                // construct a JSONArray from a string; can also use an array or list
                JSONArray coord = new JSONArray("["+obj.getLongitude()+","+obj.getLatitude()+"]");
                point.put("coordinates", coord);
                
                JSONObject feature = new JSONObject();
                feature.put("geometry", point);
                
                JSONObject properties = new JSONObject();
                properties.put("title", obj.getModel());
                properties.put("description", obj.getModel());

                properties.put("icon", "airport");
                properties.put("plane", obj.getAircraft());

                feature.put("properties", properties);
                featureList.put(feature);
                featureCollection.put("features", featureList);
            }
        } catch (JSONException e) {
            logger.error("can't save json object: "+e.toString());
        }
        
        logger.info("featureCollection="+featureCollection.toString());
        return featureCollection;
     }
    
}
