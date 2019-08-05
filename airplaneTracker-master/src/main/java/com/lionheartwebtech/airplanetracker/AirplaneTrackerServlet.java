package com.lionheartwebtech.airplanetracker;

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import freemarker.core.ParseException;
import freemarker.template.*;


import java.nio.charset.Charset;
import java.net.*;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.json.*;



public class AirplaneTrackerServlet extends HttpServlet {
    private static final String FEED = "https://public-api.adsbexchange.com/VirtualRadar/AircraftList.json?fTypQN=";
    private static final Logger logger = Logger.getLogger(AirplaneTrackerServlet.class.getName());
    private static String username = "";
    private static Connection jdbcConnection = null;
    private static Configuration fmConfig = new Configuration(Configuration.getVersion());
    private static final String TEMPLATE_DIR = "/WEB-INF/templates";
    
    @Override
    public void init(ServletConfig config) throws UnavailableException {
        logger.info("==============================");
        logger.info("Starting " + AirplaneTrackerServlet.class.getSimpleName() + " servlet init");
        logger.info("==============================");
        
        logger.info("Getting real path for templateDir");
        String templateDir = config.getServletContext().getRealPath(TEMPLATE_DIR);
        logger.info("...real path is: " + templateDir);
        
        logger.info("Initializing Freemarker, templateDir: " + templateDir);
        try {
            fmConfig.setDirectoryForTemplateLoading(new File(templateDir));
            logger.info("Successfully Loaded Freemarker");
        } catch (IOException e) {
            logger.error("Template directory not found, directory: " + templateDir + ", exception: " + e);
        }
          


        logger.info("Connecting to the database...");
        
        String jdbcDriver = "org.mariadb.jdbc.Driver";
        logger.info("Loading JDBC Driver: " + jdbcDriver);
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            logger.error("Unable to find JDBC driver on classpath.");
            return;
        }
        
        String connString = "jdbc:mariadb://";
        connString += "lionheartwebtech-db.cv18zcsjzteu.us-west-2.rds.amazonaws.com:3306";
        connString += "/whidbey";
        connString += "?user=whidbey&password=whidbey";
        connString += "&useSSL=true&trustServerCertificate=true";
     
        try {
            jdbcConnection = DriverManager.getConnection(connString);
        } catch (SQLException e) {
            logger.error("Unable to connect to SQL Database with JDBC string: " + connString);
            throw new UnavailableException("Unable to connect to database.");
        }
        
        logger.info("...connected!");
        
        logger.info("==============================");
        logger.info("Finished init");
        logger.info("==============================");
    }
    
    @Override
    public void destroy() {
        logger.info("##############################");
        logger.info("Destroying " + AirplaneTrackerServlet.class.getSimpleName() + " servlet");
        logger.info("##############################");

        logger.info("Disconnecting from the database.");
        try {
            jdbcConnection.close();
        } catch (SQLException e) {
            logger.error("Exception thrown while trying to close SQL Connection: " + e, e);
        }
        logger.info("Disconneced!");
        
        logger.info("##############################");
        logger.info("...done");
        logger.info("##############################");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long timeStart = System.currentTimeMillis();
        logger.debug("IN - doGet()");
                
        String command = request.getParameter("cmd");
        if (command == null) command = "home";

        String template = "";
        Map<String, Object> model = new HashMap<>();
        

        switch (command) {
            case "home":
                template = "homepage.tpl";
                username = "";
                break;
            case "login":
                template = "login.tpl";
                break;
            case "register":
                template = "register.tpl";
                break;
            case "savedPlanes":
                template = "savedPlanes.tpl";
                model.put("username", username);
                PlaneJson thisJson = new PlaneJson();
                List<InterestingFlight> flightList = thisJson.getFlights();
                
                for(InterestingFlight i : flightList){
                    interestingFlightDAO.insertFlightToDB(jdbcConnection,i);
                }
                model.put("allFlights", flightList);
                break;
                
            case "search":
                template = "airflightMap.tpl";
                model.put("username", username);
                PlaneJson myJson = new PlaneJson();
                List<InterestingFlight> myFlights = myJson.getFlights();
                
                for(InterestingFlight i : myFlights){
                    interestingFlightDAO.insertFlightToDB(jdbcConnection,i);
                }
                
                InterestingFlight[] TransFile = (InterestingFlight[]) myFlights.toArray(new InterestingFlight[myFlights.size()]);
                
                GeoJsonFile myFile = new GeoJsonFile(TransFile);

               
                JSONObject fileJ = myFile.getGeoJsonFile();
                model.put("geoJsonFile", fileJ);
                
                
                break;
            case "detail":
                template = "aircraftTable.tpl";
                logger.info("sucessfully logged in");
                model.put("username", username);
                String pass = request.getParameter("pass");
                ArrayList<String> usersSavedFlight;
                try{
                     usersSavedFlight = UserDAO.getUserInterestingFlightList(jdbcConnection,username);
                }catch(Exception e){
                    usersSavedFlight = null;
                }
                    
                   if(usersSavedFlight==null || usersSavedFlight.isEmpty()){
                        String myFlight = "no flight to display";
                        ArrayList<String> noFlightSaved = new ArrayList<String>();
                        noFlightSaved.add(0,myFlight);
                        model.put("InterestingFlight",noFlightSaved);
                    }else{
                       model.put("InterestingFlight",usersSavedFlight);
                   }
                    
                   
                
                break;
            case "searchLogged":
                model.put("username", username);
                model.put("toPrint", "");
                   
                    
                List<InterestingFlight> flightsToBeSaved = getToSaveFlightsFromRequest(request);
                if(flightsToBeSaved == null || flightsToBeSaved.isEmpty()){


                        PrintWriter out = response.getWriter();  
                        template = "userAirflightMap.tpl";

                        usersSavedFlight = UserDAO.getUserInterestingFlightList(jdbcConnection,username);
                        PlaneJson myJson1;
                        if( usersSavedFlight == null){
                            logger.info("user did not save any flight, displaying all interesting flights...");
                            myJson1 = new PlaneJson();
                            PrintWriter out1 = response.getWriter();  
                            response.setContentType("text/html");  
                            out.println("<script type=\"text/javascript\">");  
                            out.println("alert(\"You did not save any flight, displaying all interesting flights.\");");  
                            out.println("</script>");
                        }else{
                            logger.info("getting users saved flight...");
                            myJson1 = new PlaneJson(usersSavedFlight);
                            logger.info("myJson: " + myJson1.toString());
                        }

                   
                    List<InterestingFlight> myFlights1 = myJson1.getFlights();

                    if(myFlights1 == null || myFlights1.isEmpty()){
                        
                        response.setContentType("text/html");  
                        out.println("<script type=\"text/javascript\">");  
                        out.println("alert(\"None of the saved flights is flying, displaying all flights \");");  
                        out.println("</script>");
                        myJson1 = new PlaneJson();
                        myFlights1 = myJson1.getFlights();
                    }
                 
                    
                    InterestingFlight[] TransFile1 = (InterestingFlight[]) myFlights1.toArray(new InterestingFlight[myFlights1.size()]);

                    GeoJsonFile myFile1 = new GeoJsonFile(TransFile1);

                    JSONObject fileJ1 = myFile1.getGeoJsonFile();
                    model.put("geoJsonFile", fileJ1);
                }else{
                    for(int i =0; i< flightsToBeSaved.size(); i++){
                        logger.info("flight to be saved (ID): " + flightsToBeSaved.get(i));
                        if(flightsToBeSaved.get(i)!=null)
                            UserDAO.addInterestingFlightsToUser(jdbcConnection, flightsToBeSaved.get(i), username);
                    }
                    PrintWriter out = response.getWriter();  
                    response.setContentType("text/html");  
                    out.println("<script type=\"text/javascript\">");  
                    out.println("alert(\"Planes have been saved. \");");  
                    out.println("</script>");
                
                
                template = "aircraftTable.tpl";
                
                try{
                     usersSavedFlight = UserDAO.getUserInterestingFlightList(jdbcConnection,username);
                }catch(Exception e){
                    usersSavedFlight = null;
                }
                   if(usersSavedFlight==null || usersSavedFlight.isEmpty()){
                        String myFlight = "no flight to display";
                        ArrayList<String> noFlightSaved = new ArrayList<String>();
                        noFlightSaved.add(0,myFlight);
                        model.put("InterestingFlight",noFlightSaved);
                    }else{
                       model.put("InterestingFlight",usersSavedFlight);
                   }
                   
                  }
                
                break;
            default:
                logger.info("Invalid GET command received: " + command);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
        }

        processTemplate(response, template, model);
        long time = System.currentTimeMillis() - timeStart;
        logger.info("OUT - doGet() - " + time + "ms");
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long timeStart = System.currentTimeMillis();
        logger.debug("IN - doPost()");

        String command = request.getParameter("cmd");
        if (command == null) {
            logger.info("No cmd parameter received");
            command = "";
        }

        String template = "";
        Map<String, Object> model = new HashMap<>();
        ArrayList<String> usersSavedFlight;
        switch (command) {
            case "saveFlights":
                model.put("username", username);
                model.put("toPrint", "");
                   
                    
                List<InterestingFlight> flightsToBeSaved = getToSaveFlightsFromRequest(request);
                if(flightsToBeSaved == null || flightsToBeSaved.isEmpty()){
                    template = "savedPlanes.tpl";
                    
                    PrintWriter out = response.getWriter();  
                    response.setContentType("text/html");  
                    out.println("<script type=\"text/javascript\">");  
                    out.println("alert(\"You did not choose any flight, please try again. \");");  
                    out.println("</script>");
                    template = "userAirflightMap.tpl";
                    
                    usersSavedFlight = UserDAO.getUserInterestingFlightList(jdbcConnection,username);
                    PlaneJson myJson;
                    if( usersSavedFlight == null){
                        logger.info("user did not save any flight, displaying all interesting flights...");
                        myJson = new PlaneJson();
                        PrintWriter out1 = response.getWriter();  
                        response.setContentType("text/html");  
                        out.println("<script type=\"text/javascript\">");  
                        out.println("alert(\"You did not save any flight, displaying all interesting flights.\");");  
                        out.println("</script>");
                    }else{
                        logger.info("getting users saved flight...");
                        myJson = new PlaneJson(usersSavedFlight);
                        logger.info("myJson: " + myJson.toString());
                    }

                   
                    List<InterestingFlight> myFlights = myJson.getFlights();

                    if(myFlights == null || myFlights.isEmpty()){
                        PrintWriter out1 = response.getWriter();  
                        response.setContentType("text/html");  
                        out.println("<script type=\"text/javascript\">");  
                        out.println("alert(\"None of the saved flights is flying, displaying all flights \");");  
                        out.println("</script>");
                        myJson = new PlaneJson();
                        myFlights = myJson.getFlights();
                    }
                    
                    for(InterestingFlight i : myFlights){
                        interestingFlightDAO.insertFlightToDB(jdbcConnection,i);
                    }
                    
                    InterestingFlight[] TransFile = (InterestingFlight[]) myFlights.toArray(new InterestingFlight[myFlights.size()]);

                    GeoJsonFile myFile = new GeoJsonFile(TransFile);

                    JSONObject fileJ = myFile.getGeoJsonFile();
                    model.put("geoJsonFile", fileJ);
                }else{
                    for(int i =0; i< flightsToBeSaved.size(); i++){
                        logger.info("flight to be saved (ID): " + flightsToBeSaved.get(i));
                        if(flightsToBeSaved.get(i)!=null)
                            UserDAO.addInterestingFlightsToUser(jdbcConnection, flightsToBeSaved.get(i), username);
                    }
                    PrintWriter out = response.getWriter();  
                    response.setContentType("text/html");  
                    out.println("<script type=\"text/javascript\">");  
                    out.println("alert(\"Planes have been saved. \");");  
                    out.println("</script>");
                
                
                template = "aircraftTable.tpl";
                
                try{
                     usersSavedFlight = UserDAO.getUserInterestingFlightList(jdbcConnection,username);
                }catch(Exception e){
                    usersSavedFlight = null;
                }
                   if(usersSavedFlight==null || usersSavedFlight.isEmpty()){
                        String myFlight = "no flight to display";
                        ArrayList<String> noFlightSaved = new ArrayList<String>();
                        noFlightSaved.add(0,myFlight);
                        model.put("InterestingFlight",noFlightSaved);
                    }else{
                       model.put("InterestingFlight",usersSavedFlight);
                   }
                   
                  }
                break;
            case "login":
                username = request.getParameter("username");
                String pass = request.getParameter("pass");
                logger.info("username: " + username + " and pw: " + pass);
                model.put("username", username);
                User currentAttempt = UserDAO.createUserObjectByUsername(jdbcConnection,username,pass);
                if(currentAttempt==null){
                    PrintWriter out = response.getWriter();  
                    response.setContentType("text/html");  
                    out.println("<script type=\"text/javascript\">");  
                    out.println("alert(\"Sorry, the username/password is incorrect. Please try again\");");  
                    out.println("</script>");
                    
                    logger.info("error logging in...");
                    template = "login.tpl";
                    
                }else{
                    logger.info("sucessfully logged in");
                    
                    template = "userAirflightMap.tpl";
                    usersSavedFlight = UserDAO.getUserInterestingFlightList(jdbcConnection,username);
                    PlaneJson myJson;
                    if( usersSavedFlight == null){
                        logger.info("user did not save any flight, displaying all interesting flights...");
                        myJson = new PlaneJson();
                        PrintWriter out = response.getWriter();  
                        response.setContentType("text/html");  
                        out.println("<script type=\"text/javascript\">");  
                        out.println("alert(\"You did not save any flight, displaying all interesting flights.\");");  
                        out.println("</script>");
                    }else{
                        logger.info("getting users saved flight...");
                        myJson = new PlaneJson(usersSavedFlight);
                        logger.info("myJson: " + myJson.toString());
                    }

                   
                    List<InterestingFlight> myFlights = myJson.getFlights();

                    if(myFlights == null || myFlights.isEmpty()){
                        PrintWriter out = response.getWriter();  
                        response.setContentType("text/html");  
                        out.println("<script type=\"text/javascript\">");  
                        out.println("alert(\"None of the saved flights is flying, displaying all flights \");");  
                        out.println("</script>");
                        myJson = new PlaneJson();
                        myFlights = myJson.getFlights();
                    }
                    
                    for(InterestingFlight i : myFlights){
                        interestingFlightDAO.insertFlightToDB(jdbcConnection,i);
                    }
                    
                    InterestingFlight[] TransFile = (InterestingFlight[]) myFlights.toArray(new InterestingFlight[myFlights.size()]);

                    GeoJsonFile myFile = new GeoJsonFile(TransFile);

                    JSONObject fileJ = myFile.getGeoJsonFile();
                    model.put("geoJsonFile", fileJ);
                }
                
                break;
            case "registerUser":
                String userName =request.getParameter("userName");
                String userPW = request.getParameter("password");
                String userEmail = request.getParameter("email");
                
                logger.info(userName + userPW + userEmail);

                if(UserDAO.checkIfTheUsernameCanBeUse(jdbcConnection, userName)!=true){
                  logger.error("the username has been used.");
                  PrintWriter out = response.getWriter();  
                    response.setContentType("text/html");  
                    out.println("<script type=\"text/javascript\">");  
                    out.println("alert(\"Sorry, the username has been used. Please try again\");");  
                    out.println("</script>");
                  template = "register.tpl";
                  break;
                }else{
                
                    try{
                        UserDAO.insertUserFromSignUpPage(jdbcConnection, userName, userPW, userEmail);
                        template = "login.tpl";
                    }catch(Exception e){
                        logger.error("Unable to insert user to DAO." + e.getMessage());
                        
                    }
                    
                }
                break;
                
            default:
                logger.info("Invalid POST command received: " + command);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
        }

        processTemplate(response, template, model);
        long time = System.currentTimeMillis() - timeStart;
        logger.debug("OUT - doPost() - " + time + "ms");
    }        


private void processTemplate(HttpServletResponse response, String template, Map<String, Object> model) {
        logger.debug("Processing Template: " + template);
        
        try (PrintWriter out = response.getWriter()) {
            Template view = fmConfig.getTemplate(template);
            view.process(model, out);
        } catch (TemplateException e) {
            logger.error("Template Error:", e);
        } catch (MalformedTemplateNameException e) {
            logger.error("Malformed Template Error:", e);
        } catch (ParseException e) {
            logger.error("Parsing Error:", e);
        } catch (IOException e) {
            logger.error("IO Error:", e);
        } 
    }
    
    private List<InterestingFlight> getToSaveFlightsFromRequest(HttpServletRequest request) {
        List<InterestingFlight> toSaveFlights = new ArrayList<>();
        String flights[] = request.getParameterValues("flights");
        if(flights != null){
                for(int i = 0; i < flights.length; i++){
                if(flights[i] != null || !flights[i].isEmpty()){
                    logger.info("Flights to be saved: " + flights[i].toString());
                    toSaveFlights.add(interestingFlightDAO.getInterestingFlight(jdbcConnection, Integer.parseInt(flights[i].trim().replace(",",""))));
                }
            }
                
               return toSaveFlights;
        } else{
            toSaveFlights = null;
            return toSaveFlights;
        }
        

        
    }
   
    }

