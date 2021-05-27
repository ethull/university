package com.sd.group4f;

import java.util.ArrayList;

import java.sql.*;

/**
 * Role of user to determine range of access to system.
 */
enum Role
{
	RECEPTIONIST { public String toString() {  return "Receptionist"; } },
	DOCTOR { public String toString() {  return "Doctor"; } },
	PATIENT { public String toString() {  return "Patient"; } },
	NONE { public String toString() {  return "No Role"; } }
}

/**
 * DBManager test class.
 * @author jjrf2, eh443, mb2021
 * @version 2021.04.02
 */
public class DBManager
{
	// Database user credentials
    private static final String DB_USERNAME = "jemstone";
    private static final String DB_PASSWORD = "passworD1-";
    
    // Database name
    private String database;
    
    // User information
	public String username = null;
	public Role role = null;
    
	// Database access elements
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    
    /**
     * Class constructor.
     * @param database
     */
    public DBManager(String database)
    {
    	this.database = database;
    }
    
    /**
     * Connect to database.
     * @return	Whether connection successful
     */
	public boolean connect()
	{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(String.format("jdbc:mysql://localhost/%s?user=%s&password=%s", database, DB_USERNAME, DB_PASSWORD));
			return (connection != null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

    /**
     * Clear connection to database.
     */
	public void disconnect()
	{
	    connection = null;
	}
	
	/**
	 * Execute SQL statement.
	 * @param statement	SQL statement to execute
	 */
	public void execute(String statement)
    {
    	if (connection != null) {
	    	try {
	    		Statement statementObject = connection.createStatement();
	            statementObject.executeUpdate(statement);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }
	
	/**
	 * Execute SQL selection statement.
	 * @param statement	SQL selection statement to execute
	 * @return			Set of results
	 */
	public String[] executeSelect(String statement)
    {
    	if (connection != null) {
	    	try {
	    		Statement statementObject = connection.createStatement();
	            resultSet = statementObject.executeQuery(statement);
				int colCount = resultSet.getMetaData().getColumnCount();
				String[] res = new String[colCount];
				if (resultSet.next()){
					for (int i = 1; i<=colCount; i++){
						res[i-1] = resultSet.getString(i);
					}
					return res;
				} else {
					return new String[0];
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				return new String[0];
			}
    	}
		return new String[0];
    }
    
	/**
	 * Login to system with given credentials.
	 * @param username	User login name
	 * @param password	User login password
	 * @return			Whether login successful
	 */
    public boolean login(String username, String password)
    {
    	if (connection != null) {
	    	try {
	    		// Check whether given credentials exist in user table
	            statement = connection.createStatement();
	            resultSet = statement.executeQuery(String.format("SELECT * FROM User WHERE username='%s' AND password='%s';", username, password));
	            
	            // Assign role
	            if (resultSet.next()) {
		            this.username = username;
		            String roleVal = (resultSet.getString("role"));
		            if (roleVal.equals("R")) role = Role.RECEPTIONIST;
		            else if (roleVal.equals("D")) role = Role.DOCTOR;
		            else if (roleVal.equals("P")) role = Role.PATIENT;
		            else role = Role.NONE;
		            return true;
	            }
	            return false;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	return false;
    }
    
    /**
     * Reset user information
     */
    public void logout()
    {
    	username = null;
    	role = null;
    	
    }
    
    /**
     * Fetch list of all messages for current user from database.
     * @return	List of messages
     */
    public ArrayList<String[]> fetchMessages()
    {
    	if (connection != null && username != null) {
	    	try {
	    		// Select all messages addressed to logged in user
	            statement = connection.createStatement();
	            resultSet = statement.executeQuery(String.format("SELECT * FROM Message WHERE username='%s' ORDER BY date DESC, time DESC;", username));
	            
	            // Format messages as array of details
	            ArrayList<String[]> messages = new ArrayList<>();
	            while (resultSet.next()) {
	            	String[] message = new String[4];
	            	message[0] = resultSet.getString("msgHead");
	            	message[1] = resultSet.getString("time").substring(0, 5);
	            	message[2] = resultSet.getString("date").replace("-", "/");
	            	message[3] = resultSet.getString("msgBody");
	            	messages.add(message);
	            }
	            return messages;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	return new ArrayList<>();
    }
    
    /**
     * Fetch list of all doctors from database.
     * @return	List of all doctors
     */
    public ArrayList<String[]> fetchDoctors()
    {
    	if (connection != null) {
	    	try {
	    		// Select all doctors
	            statement = connection.createStatement();
	            resultSet = statement.executeQuery("SELECT * FROM Doctor;");
	            
	            // Format doctors as array of details
	            ArrayList<String[]> doctors = new ArrayList<>();
	            while (resultSet.next()) {
	            	String[] doctor = new String[7];
	            	doctor[0] = resultSet.getString("did");
	            	doctor[1] = resultSet.getString("initial");
	            	doctor[2] = resultSet.getString("surname");
	            	doctor[3] = resultSet.getString("roomNo");
	            	doctor[4] = resultSet.getString("phoneNo");
	            	doctor[5] = resultSet.getString("background");
	            	doctor[6] = resultSet.getString("username");
	            	doctors.add(doctor);
	            }
	            return doctors;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	return new ArrayList<>();
    }

	/**
	 * Fetch list of all patients from database.
	 * @return	List of all patients
	 */
	public ArrayList<String[]> fetchPatients()
	{
		if (connection != null) {
			try {
				// Select all patients
				statement = connection.createStatement();
				resultSet = statement.executeQuery("SELECT * FROM Patient;");

				// Format patients as array of details
				ArrayList<String[]> patients = new ArrayList<>();
				while (resultSet.next()) {
					String[] patient = new String[8];
					patient[0] = resultSet.getString("pid");
					patient[1] = resultSet.getString("forename");
					patient[2] = resultSet.getString("surname");
					patient[3] = resultSet.getString("gender");
					patient[4] = resultSet.getString("dob");
					patient[5] = resultSet.getString("phoneNo");
					patient[6] = resultSet.getString("did");
					patient[7] = resultSet.getString("username");
					patients.add(patient);
				}
				return patients;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
    	return new ArrayList<>();
	}

	/**
	 * Fetch list of all bookings from database.
	 * @return	List of all bookings
	 */
	public ArrayList<String[]> fetchBookings(String additional)
	{
		if (connection != null) {
			try {
				// Select all bookings
				statement = connection.createStatement();
				resultSet = statement.executeQuery(
						"SELECT initial, d.surname, forename, p.surname, date, time, d.did, p.pid " +
						"FROM Doctor d " +
						"INNER JOIN Booking b ON b.did=d.did " +
						"INNER JOIN Patient p ON b.pid=p.pid " +
						additional +
						"ORDER BY b.date, b.time;"
				);

				// Format bookings as array of details
				ArrayList<String[]> bookings = new ArrayList<>();
				while (resultSet.next()) {
					String[] booking = new String[8];
					booking[0] = resultSet.getString("initial");
					booking[1] = resultSet.getString("d.surname");
					booking[2] = resultSet.getString("forename");
					booking[3] = resultSet.getString("p.surname");
					booking[4] = resultSet.getString("date");
					booking[5] = resultSet.getString("time");
					booking[6] = resultSet.getString("did");
					booking[7] = resultSet.getString("pid");
					bookings.add(booking);
				}
				return bookings;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
    	return new ArrayList<>();
	}

	/**
	 * Fetches the usernames of the patient and associated doctor.
	 * @param patForename Forename of the patient.
	 * @param patSurname Surname of the patient.
	 * @return Array storing the usernames.
	 */
	public String[] fetchUsernames(String patForename, String patSurname)
	{
		if (connection != null) {
			try {
				statement = connection.createStatement();
				resultSet = statement.executeQuery(String.format(
						"SELECT p.username, d.username " +
							"FROM Patient p " +
							"JOIN Doctor d ON p.did=d.did " +
							"WHERE forename='%s' AND p.surname='%s';",
						patForename, patSurname)
				);

				// Format usernames as array
				resultSet.next();
				String[] usernames = new String[2];
				usernames[0] = resultSet.getString("p.username");
				usernames[1] = resultSet.getString("d.username");
				return usernames;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new String[]{};
	}
    
	/**
	 * Add a user to the User table and gets its own method so possible errors could be easier traced to User table.
	 * @param username	Desired username
	 * @param pw		Desired password
	 * @param role		Desired role
	 */
	public void addUser(String username, String pw, char role)
	{
		try {
	        statement = connection.createStatement();
			// Execute an update statement (statement that changes the database)
			statement.executeUpdate(String.format("INSERT INTO User (username, password, role) VALUES ('%s', '%s', '%c');", username, pw, role));
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Add a doctor to the Doctor table but first add a user to the User table to fit foreign key constraints.
	 * @param initial		Desired initial
	 * @param surname		Desired surname
	 * @param roomNo		Desired room number
	 * @param phoneNo		Desired phone number
	 * @param background	Desired background
	 * @param username		Desired username
	 * @param pw			Desired password
	 * @param role			Desired role
	 * @return				Resulting ID of new doctor
	 */
	public int addDoctor(char initial, String surname, String roomNo, String phoneNo, String background, String username, String pw, char role)
	{	
    	if (connection != null) {
			addUser(username, pw, role);
			try {
				statement = connection.createStatement();
				statement.executeUpdate(String.format("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, background, username) VALUES ('%c', '%s', '%s', '%s', '%s', '%s');", initial, surname, roomNo, phoneNo, background, username));
				
				statement = connection.createStatement();
	            resultSet = statement.executeQuery("SELECT did FROM Doctor WHERE username = '" + username + "';");
	            
	            // Return resulting ID
	            if (resultSet.next()) {
	            	return Integer.parseInt(resultSet.getString("did"));
	            }
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	/**
	 * Add a patient to the Patient table but first add a user to the User table to fit foreign key constraints.
	 * @param forename	Desired forename
	 * @param surname	Desired surname
	 * @param gender	Desired gender
	 * @param dob		Desired date of birth
	 * @param phoneNo	Desired phone number
	 * @param doctorID	Desired doctor ID
	 * @param username	Desired username
	 * @param pw		Desired password
	 * @param role		Desired role
	 * @return			Resulting ID of new patient
	 */
	public int addPatient(String forename, String surname, char gender, String dob, String phoneNo, int doctorID, String username, String pw, char role)
	{
		if (connection != null) {
			addUser(username, pw, role);
			try {
				statement = connection.createStatement();
				statement.executeUpdate(String.format("INSERT INTO Patient (forename, surname, gender, dob, phoneNo, did, username) VALUES ('%s', '%s', '%c', '%s', '%s', '%d', '%s');", forename, surname, gender, dob, phoneNo, doctorID, username));

				statement = connection.createStatement();
	            resultSet = statement.executeQuery("SELECT pid FROM Patient WHERE username = '" + username + "';");
	            
	            // Return resulting ID
	            if (resultSet.next()) {
	            	return Integer.parseInt(resultSet.getString("pid"));
	            }
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		return 0;
	}

	/**
	 * Add a booking to the Booking table.
	 * @param patForename Forename of the patient.
	 * @param patSurname Surname of the patient.
	 * @param date Date of the booking.
	 * @param time Time of the booking.
	 * @Return An array containing the doctor and patient's id.
	 */
	public String[] addBooking(String patForename, String patSurname, String date, String time)
	{
		if (connection != null) {
			try {
				statement = connection.createStatement();
				String select = String.format("FROM Patient WHERE forename='%s' AND surname='%s'", patForename, patSurname);

				statement.executeUpdate(String.format(
						"INSERT INTO Booking VALUES (" +
						"(SELECT did %s)," +
						"(SELECT pid %s)," +
						"'%s','%s'" +
						");", select, select, date, time));
				
				resultSet = statement.executeQuery(String.format("SELECT did, pid FROM Patient WHERE forename='%s' AND surname='%s'", patForename, patSurname));
				if (resultSet.next()) {
					return new String[] {resultSet.getString("did"), resultSet.getString("pid")};
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new String[0];
	}

	/**
	 * Removes a booking from the Booking table.
	 * @param patForename Forename of the patient.
	 * @param patSurname Surname of the Patient.
	 * @param date Date of the booking.
	 * @param time Time of the booking.
	 * @Return An array containing the doctor and patient's id.
	 */
	public String[] removeBooking(String patForename, String patSurname, String date, String time)
	{
		if (connection != null) {
			try {
				statement = connection.createStatement();
				statement.executeUpdate(String.format(
						"DELETE FROM Booking " +
						"WHERE pid=" +
						"(SELECT pid FROM Patient WHERE forename='%s' AND surname='%s') " +
						"AND date='%s' AND time='%s';",
						patForename, patSurname, date, time)
				);
				
				resultSet = statement.executeQuery(String.format("SELECT did, pid FROM Patient WHERE forename='%s' AND surname='%s'", patForename, patSurname));
				if (resultSet.next()) {
					return new String[] {resultSet.getString("did"), resultSet.getString("pid")};
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new String[0];
	}

	/**
	 * Update a booking from the Booking table.
	 * @param patForename Forename of the patient.
	 * @param patSurname Surname of the patient.
	 * @param oldDate The current date of the booking.
	 * @param oldTime The current time of the booking.
	 * @param newDate The new date of the booking.
	 * @param newTime The new time of the booking.
	 * @Return An array containing the doctor and patient's id.
	 */
	public String[] updateBooking(String patForename, String patSurname, String oldDate, String oldTime, String newDate, String newTime)
	{
		if (connection != null) {
			try {
				statement = connection.createStatement();
				statement.executeUpdate(String.format(
						"UPDATE Booking " +
								"SET date='%s', time='%s' " +
								"WHERE pid=(SELECT pid FROM Patient WHERE forename='%s' AND surname='%s') " +
								"AND date='%s' AND time='%s'",
						newDate, newTime, patForename, patSurname, oldDate, oldTime)
				);
				
				resultSet = statement.executeQuery(String.format("SELECT did, pid FROM Patient WHERE forename='%s' AND surname='%s'", patForename, patSurname));
				if (resultSet.next()) {
					return new String[] {resultSet.getString("did"), resultSet.getString("pid")};
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new String[0];
	}

	
	/**
	 * Send confirmation message to inbox of specified user.
	 * Need to check whether username exists first (so user is logged in).
	 * @param time		Current time
	 * @param date		Current date
	 * @param msgHead	Desired message subject
	 * @param msgBody	Desired message content
	 * @param username	Recipient username
	 */
	public void sendMessage(String time, String date, String msgHead, String msgBody, String username)
	{
    	if (connection != null) {
			try {
				statement = connection.createStatement();
				statement.executeUpdate(String.format("INSERT INTO Message (time, date, msgHead, msgBody, username) VALUES ('%s', '%s', '%s', '%s', '%s')", time, date, msgHead, msgBody, username));
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * Reassign a patient to a new doctor
	 * @param did Id of the doctor
	 * @param pid Id of the patient
	 */
	public void reassignDoctor(int did, int pid){
    	if (connection != null) {
			try {
				statement = connection.createStatement();
				statement.executeUpdate(String.format("UPDATE Patient SET did = '%d' WHERE pid = '%d';", did, pid));

			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
    
    /**
     * Fetch list of access history for current user from database.
     * @return	List of actions
     */
    public ArrayList<String[]> fetchActivity()
    {
    	if (connection != null && username != null) {
	    	try {
	    		// Select all activity
	            statement = connection.createStatement();
	            resultSet = statement.executeQuery("SELECT Activity.*, User.role, Doctor.initial, Doctor.surname, Patient.forename, Patient.surname FROM Activity LEFT JOIN User ON Activity.username = User.username  LEFT JOIN Doctor ON Activity.did = Doctor.did LEFT JOIN Patient ON Activity.pid = Patient.pid ORDER BY date DESC, time DESC, actid DESC;");
	            
	            // Format actions as array of details
	            ArrayList<String[]> activity = new ArrayList<String[]>();
	            while (resultSet.next()) {
	            	String[] action = new String[7];
	            	action[0] = resultSet.getString("username") + " (" + resultSet.getString("role") + ")";
	            	action[1] = resultSet.getString("actCode");
	            	action[2] = resultSet.getString("details");
	            	action[3] = resultSet.getString("date").replace("-", "/");
	            	action[4] = resultSet.getString("time").substring(0, 5);
	            	action[5] = resultSet.getString("initial") == null ? null : (resultSet.getString("initial") + ". " + resultSet.getString("Doctor.surname"));
	            	action[6] = resultSet.getString("initial") == null ? null : (resultSet.getString("forename") + " " + resultSet.getString("Patient.surname"));
	            	activity.add(action);
	            }
	            return activity;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	return new ArrayList<String[]>();
    }

	/**
	 * Log access of a feature by a user to the activity history
	 * @param actCode	Index of functionality accessed
	 * @param did		Id of the doctor to be referenced
	 * @param pid		Id of the patient to be referenced
	 * @param details	Additional details of action
	 */
	public void logAccess(int actCode, Integer did, Integer pid, String details)
	{
    	if (connection != null) {
    		long millis=System.currentTimeMillis();  
    		String time = new Time(millis).toString();
    		String date = new Date(millis).toString();  
			try {				
				String statementString = String.format("INSERT INTO Activity (username, actCode, details, date, time, did, pid) VALUES ('%s', '%s', ", username, actCode);
				
				if (details == null) statementString = statementString + "NULL, ";
				else statementString = statementString + "'" + details + "', ";
				
				statementString = statementString + String.format("'%s', '%s', ", date, time);
				
				if (did == null) statementString = statementString + "NULL, ";
				else statementString = statementString + "'" + did + "', ";
				
				if (pid == null) statementString = statementString + "NULL);";
				else statementString = statementString + "'" + pid + "');";
				
				statement = connection.createStatement();
				statement.executeUpdate(statementString);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
