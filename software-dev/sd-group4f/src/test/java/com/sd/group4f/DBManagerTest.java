package com.sd.group4f;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * DBManager test class.
 * @author jjrf2, eh443, mb2021
 * @version 2021.04.01
 */
public class DBManagerTest
{
	// Database manager to test
	private DBManager d;

    // Catch any program output
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private String report;
    private Boolean printTestPassed;
	
	@Before
	public void setUp() throws Exception
	{
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        report = null;
        
        // Setup database test double with dummy data
		d = new DBManager("testDouble");
		d.connect();
		d.execute("DROP DATABASE IF EXISTS testDouble;");
		d.execute("CREATE DATABASE testDouble;");
		d.execute("USE testDouble;");
		d.execute("DROP TABLE IF EXISTS User CASCADE;");
		d.execute("DROP TABLE IF EXISTS Doctor CASCADE;");
		d.execute("DROP TABLE IF EXISTS Patient CASCADE;");
		d.execute("DROP TABLE IF EXISTS Message CASCADE;");
		d.execute("DROP TABLE IF EXISTS Booking CASCADE;");
		d.execute("DROP TABLE IF EXISTS Activity CASCADE;");
		d.execute("CREATE TABLE User (username CHAR(5) PRIMARY KEY, password VARCHAR(16) NOT NULL, role CHAR(1) CHECK (role IN ('R', 'D', 'P')) NOT NULL);");
		d.execute("CREATE TABLE Doctor (did INTEGER AUTO_INCREMENT PRIMARY KEY, initial CHAR(1) NOT NULL, surname VARCHAR(10) NOT NULL, roomNo CHAR(2) NOT NULL UNIQUE, phoneNo VARCHAR(11) NOT NULL UNIQUE, background TEXT, username CHAR(5) NOT NULL, FOREIGN KEY (username) REFERENCES User (username) ON UPDATE CASCADE ON DELETE CASCADE);");
		d.execute("CREATE TABLE Patient (pid INTEGER AUTO_INCREMENT PRIMARY KEY, forename VARCHAR(10) NOT NULL, surname VARCHAR(10) NOT NULL, gender CHAR(1) CHECK (gender IN ('M', 'F', 'O')) NOT NULL, dob DATE NOT NULL, phoneNo INTEGER(11), did INTEGER, username CHAR(5) NOT NULL, FOREIGN KEY (username) REFERENCES User (username) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (did) REFERENCES Doctor (did) ON UPDATE CASCADE ON DELETE SET NULL);");
		d.execute("CREATE TABLE Message (msgid INTEGER AUTO_INCREMENT PRIMARY KEY, time TIME NOT NULL, date DATE NOT NULL, msgHead TEXT NOT NULL, msgBody TEXT, username CHAR(5) NOT NULL, FOREIGN KEY (username) REFERENCES User (username) ON UPDATE CASCADE ON DELETE CASCADE);");
		d.execute("CREATE TABLE Booking (did INTEGER NOT NULL, pid INTEGER NOT NULL, date DATE NOT NULL, time TIME NOT NULL, PRIMARY KEY (did, date, time), FOREIGN KEY (did) REFERENCES Doctor (did) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (pid) REFERENCES Patient (pid) ON UPDATE CASCADE ON DELETE CASCADE);");
		d.execute("CREATE TABLE Activity (actid INTEGER AUTO_INCREMENT PRIMARY KEY, username CHAR(5) NOT NULL, actCode INTEGER NOT NULL, details TEXT, date DATE NOT NULL, time TIME NOT NULL, did INTEGER, pid INTEGER, FOREIGN KEY (username) REFERENCES User (username) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (did) REFERENCES Doctor (did) ON UPDATE CASCADE ON DELETE CASCADE, FOREIGN KEY (pid) REFERENCES Patient (pid) ON UPDATE CASCADE ON DELETE CASCADE);");
	}

	@After
    public void tearDown()
    {
        System.setOut(originalOut);
        System.setErr(originalErr);
        if(report != null) {
            System.err.println(report);
            report = null;
        }
        printTestPassed = false;
	}

	/**
	 * Test whether connecting to the database works
	 * @author jjrf2
	 */
	@Test
	public void testConnect()
	{
		assertTrue(d.connect()); // Successful connection
	}

	/**
	 * Test whether logging into the system works
	 * @author jjrf2
	 */
	@Test
	public void testLogin()
	{
		// Dummy data
		d.execute("INSERT INTO User VALUES ('jas', 'seinfeld', 'R');");
		d.execute("INSERT INTO User VALUES ('glc', 'costanza', 'D');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) VALUES ('G', 'Costanza', '2B', '07823458234', 'glc');");
		d.execute("INSERT INTO Message (time, date, msgHead, msgBody, username) VALUES ('23:26', '2021/02/24', 'Notice of new booking', 'Your patient Donkey Kong has scheduled a new meeting.', 'jas');");

		assertNull(d.username); // No username
		assertNull(d.role); // No role
		assertFalse(d.login("glc", "seinfeld")); // Incorrect username
		assertFalse(d.login("jas", "costanza")); // Incorrect password
		assertTrue(d.login("jas", "seinfeld")); // Successful login
		assertEquals(d.username, "jas"); // Correct username
		assertEquals(d.role, Role.RECEPTIONIST); // Correct role
		assertTrue(d.login("glc", "costanza")); // Successful login
		assertEquals(d.username, "glc"); // Correct username
		assertEquals(d.role, Role.DOCTOR); // Correct role
		d.disconnect();
		assertFalse(d.login("jas", "seinfeld")); // No connection
	}

	/**
	 * Test whether logging out of the system works
	 * @author jjrf2
	 */
	@Test
	public void testLogout()
	{
		d.logout();
		assertNull(d.username); // No username
		assertNull(d.role); // No role
	}

	/**
	 * Test whether fetching the messages work
	 * @author jjrf2
	 */
	@Test
	public void testFetchMessages()
	{
		// Dummy data
		d.execute("INSERT INTO User VALUES ('jas', 'seinfeld', 'R');");
		d.execute("INSERT INTO User VALUES ('glc', 'costanza', 'D');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) VALUES ('G', 'Costanza', '2B', '07823458234', 'glc');");
		d.execute("INSERT INTO Message (time, date, msgHead, msgBody, username) VALUES ('23:26', '2021/02/24', 'Notice of new booking', 'Your patient Donkey Kong has scheduled a new meeting.', 'jas');");

		d.logout();
		d.disconnect();
		assertEquals(d.fetchMessages().size(), 0); // No connection
		d.connect();
		assertEquals(d.fetchMessages().size(), 0); // No user
		d.login("jas", "seinfeld");
		assertEquals(d.fetchMessages().size(), 1); // Message exists
		assertEquals(d.fetchMessages().get(0)[0], "Notice of new booking"); // Correct header
		assertEquals(d.fetchMessages().get(0)[1], "23:26"); // Correct time
		assertEquals(d.fetchMessages().get(0)[2], "2021/02/24"); // Correct date
		assertEquals(d.fetchMessages().get(0)[3], "Your patient Donkey Kong has scheduled a new meeting."); // Correct body
		d.login("glc", "costanza");
		assertEquals(d.fetchMessages().size(), 0); // No messages
	}
	
	/**
	 * Test whether fetching the doctors work
	 * @author eh443
	 */
	@Test
	public void testFetchDoctors()
	{
		// Dummy data
		d.execute("INSERT INTO User VALUES ('jas', 'seinfeld', 'R');");
		d.execute("INSERT INTO User VALUES ('glc', 'costanza', 'D');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) VALUES ('G', 'Costanza', '2B', '07823458234', 'glc');");
		d.execute("INSERT INTO Message (time, date, msgHead, msgBody, username) VALUES ('23:26', '2021/02/24', 'Notice of new booking', 'Your patient Donkey Kong has scheduled a new meeting.', 'jas');");

		d.connect();
		assertEquals(d.fetchDoctors().size(), 1); // Doctor exists
		assertEquals(d.fetchDoctors().get(0)[0], "1"); // Correct DID
		assertEquals(d.fetchDoctors().get(0)[1], "G"); // Correct initial
		assertEquals(d.fetchDoctors().get(0)[2], "Costanza"); // Correct surname
		assertEquals(d.fetchDoctors().get(0)[3], "2B"); // Correct room number
		assertEquals(d.fetchDoctors().get(0)[4], "07823458234"); // Correct phone number
		assertNull(d.fetchDoctors().get(0)[5]); // No background
		assertEquals(d.fetchDoctors().get(0)[6], "glc"); // Correct username
		d.disconnect();
		assertEquals(d.fetchDoctors().size(), 0); // No data
	}

	/**
	 * Test whether fetching the patients work
	 * @author mb2021
	 */
	@Test
	public void testFetchPatients()
	{
		// Dummy data
		d.execute("INSERT INTO User VALUES ('pp212', 'queens', 'D');");
		d.execute("INSERT INTO User VALUES ('ts425', 'loveyou3000', 'P');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) " +
				"VALUES ('P', 'Parker', '9J', '07823458234', 'pp212');");
		d.execute("INSERT INTO Patient (forename, surname, gender, dob, did, username) " +
				"VALUES ('Tony', 'Stark', 'M', '1970-05-29', 1, 'ts425');");

		d.connect();
		ArrayList<String[]> results = d.fetchPatients();
		assertEquals(results.size(), 1); 				 // Bookings exist
		assertEquals(results.get(0)[0], "1"); 		 // Correct pid
		assertEquals(results.get(0)[1], "Tony"); 	 	 // Correct forename
		assertEquals(results.get(0)[2], "Stark"); 	 // Correct surname
		assertEquals(results.get(0)[3], "M"); 	 	 // Correct gender
		assertEquals(results.get(0)[4], "1970-05-29"); // Correct dob
		assertNull(results.get(0)[5]); 	 					 // Correct phoneNo
		assertEquals(results.get(0)[6], "1"); 	 	 // Correct did
		assertEquals(results.get(0)[7], "ts425"); 	 // Correct username
		d.disconnect();
		assertEquals(d.fetchPatients().size(), 0); 	 // No data
	}

	/**
	 * Test whether fetching the bookings work
	 * @author mb2021
	 */
	@Test
	public void testFetchBookings()
	{
		// Dummy data
		d.execute("INSERT INTO User VALUES ('pp212', 'queens', 'D');");
		d.execute("INSERT INTO User VALUES ('bb198', 'hulk', 'D');");
		d.execute("INSERT INTO User VALUES ('ts425', 'loveyou3000', 'P');");
		d.execute("INSERT INTO User VALUES ('ca290', 'america', 'P');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) " +
				"VALUES ('P', 'Parker', '9J', '07823458234', 'pp212');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) " +
				"VALUES ('B', 'Banner', '2B', '07823458235', 'bb198');");
		d.execute("INSERT INTO Patient (forename, surname, gender, dob, did, username) " +
				"VALUES ('Tony', 'Stark', 'M', '1970-05-29', 1, 'ts425');");
		d.execute("INSERT INTO Patient (forename, surname, gender, dob, did, username) " +
				"VALUES ('Steve', 'Rogers', 'M', '1918-07-04', 2, 'ca290');");
		d.execute("INSERT INTO Booking " +
				"VALUES (1, 1, '2021-03-11', '11:00:00');");
		d.execute("INSERT INTO Booking " +
				"VALUES (2, 2, '2021-03-12', '10:00:00');");

		d.connect();
		ArrayList<String[]> results = d.fetchBookings("");
		assertEquals(results.size(), 2); 				 // Bookings exist
		assertEquals(results.get(0)[0], "P"); 		 // Correct doctor initial
		assertEquals(results.get(0)[1], "Parker"); 	 // Correct doctor surname
		assertEquals(results.get(0)[2], "Tony"); 		 // Correct patient forename
		assertEquals(results.get(0)[3], "Stark"); 	 // Correct patient surname
		assertEquals(results.get(0)[4], "2021-03-11"); // Correct date
		assertEquals(results.get(0)[5], "11:00:00");   // Correct time

		assertEquals(results.get(1)[0], "B"); 		 // Correct doctor initial
		assertEquals(results.get(1)[1], "Banner"); 	 // Correct doctor surname
		assertEquals(results.get(1)[2], "Steve"); 	 // Correct patient forename
		assertEquals(results.get(1)[3], "Rogers"); 	 // Correct patient surname
		assertEquals(results.get(1)[4], "2021-03-12"); // Correct date
		assertEquals(results.get(1)[5], "10:00:00"); 	 // Correct time
		d.disconnect();
		assertEquals(d.fetchBookings("").size(), 0); // No data
	}

	/**
	 * Test whether fetching the usernames work
	 * @author mb2021
	 */
	@Test
	public void testFetchUsernames()
	{
		// Dummy data
		d.execute("INSERT INTO User VALUES ('pp212', 'queens', 'D');");
		d.execute("INSERT INTO User VALUES ('bb198', 'hulk', 'D');");
		d.execute("INSERT INTO User VALUES ('ts425', 'loveyou3000', 'P');");
		d.execute("INSERT INTO User VALUES ('ca290', 'america', 'P');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) " +
				"VALUES ('P', 'Parker', '9J', '07823458234', 'pp212');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) " +
				"VALUES ('B', 'Banner', '2B', '07823458235', 'bb198');");
		d.execute("INSERT INTO Patient (forename, surname, gender, dob, did, username) " +
				"VALUES ('Steve', 'Rogers', 'M', '1918-07-04', 2, 'ca290');");
		d.execute("INSERT INTO Patient (forename, surname, gender, dob, did, username) " +
				"VALUES ('Tony', 'Stark', 'M', '1970-05-29', 1, 'ts425');");

		d.connect();
		String[] results = d.fetchUsernames("Tony", "Stark");
		assertEquals(results.length, 2);
		assertEquals(results[0], "ts425");
		assertEquals(results[1], "pp212");
		d.disconnect();
		assertEquals(d.fetchUsernames("Tony", "Stark").length, 0);
	}

	/**
	 * Normal operation.
	 * @author eh443
	 */
	@Test
	public void addUser1(){
		d.addUser("jc", "saviour", 'P');
		assertEquals(d.executeSelect("SELECT * FROM User WHERE username='jc'")[0], "jc");
		
	}

	/**
	 * Password is 1 char too long
	 * @author eh443
	 */
	@Test
	public void addUser2(){
		d.addUser("foo", "passwordisover16c", 'P');
		//since password is too long program should print MysqlDataTruncation
		checkOutput("com.mysql.cj.jdbc.exceptions.MysqlDataTruncation");
		assertTrue(printTestPassed);
	}

	/**
	 * Username is 1 char too long
	 * @author eh443
	 */
	@Test
	public void addUser3(){
		d.addUser("sixcha", "saviour", 'P');
		checkOutput("com.mysql.cj.jdbc.exceptions.MysqlDataTruncation");
		assertTrue(printTestPassed);
	}

	/**
	 * Role is invalid
	 * @author eh443
	 */
	@Test
	public void addUser4(){
		d.addUser("foo", "bar", 'X');
		checkOutput("java.sql.SQLException");
		assertTrue(printTestPassed);
	}

	/**
	 * Normal operation
	 * (note: foreign key restriction cannot be tested as addDoctor always adds the user first)
	 * @author eh443
	 */
	@Test
	public void addDoctor1(){
		d.addDoctor('E', "Benes", "4C", "02843859628", "", "eb", "benes", 'D');

		assertEquals(d.executeSelect("SELECT * FROM User WHERE username ='eb'")[0], "eb");
		assertEquals(d.executeSelect("SELECT * FROM Doctor WHERE username ='eb'")[2], "Benes");
		// todo: initial > 1, roomNo or phoneNo not unique, username not in User table
	}

	/**
	 * Phone number too long (testing SQL length restrictions)
	 * @author eh443
	 */
	@Test
	public void addDoctor2(){
		d.addDoctor('E', "Benes", "4C", "028438596280000", "", "eb", "benes", 'D');
		checkOutput("com.mysql.cj.jdbc.exceptions.MysqlDataTruncation");
		assertTrue(printTestPassed);
	}

	/**
	 * Normal operation
	 * @author eh443
	 */
	@Test
	public void addPatient1(){
		d.execute("INSERT INTO User VALUES ('glc', 'costanza', 'D');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) VALUES ('G', 'Costanza', '2B', '07823458234', 'glc');");

		d.addPatient("Jesus", "Christ", 'M', "1900/1/1", "0724376166", 1, "jc", "saviour", 'P');
		assertEquals(d.executeSelect("SELECT * FROM User WHERE username ='jc'")[0], "jc");
		assertEquals(d.executeSelect("SELECT * FROM Patient WHERE forename = 'Jesus'")[1], "Jesus");
	}

	/**
	 * Patient is assigned a doctor that doesn't exist
	 * @author eh443
	 */
	@Test
	public void addPatient2(){
		d.addPatient("Jesus", "Christ", 'M', "1900/1/1", "0724376166", 2, "jc", "saviour", 'P');
		checkOutput("java.sql.SQLIntegrityConstraintViolationException");
		assertTrue(printTestPassed);
	}

	/**
	 * Test add booking method with normal operations.
	 * @author mb2021
	 */
	@Test
	public void addBooking1()
	{
		d.execute("INSERT INTO User VALUES ('pp212', 'queens', 'D');");
		d.execute("INSERT INTO User VALUES ('ts425', 'loveyou3000', 'P');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) " +
				"VALUES ('P', 'Parker', '9J', '07823458234', 'pp212');");
		d.execute("INSERT INTO Patient (forename, surname, gender, dob, did, username) " +
				"VALUES ('Tony', 'Stark', 'M', '1970-05-29', 1, 'ts425');");

		d.addBooking("Tony", "Stark", "2021-03-20", "10:00:00");
		String[] booking = d.executeSelect("SELECT * FROM Booking WHERE did=1");
		assertEquals(booking[0], "1");
		assertEquals(booking[1], "1");
		assertEquals(booking[2], "2021-03-20");
		assertEquals(booking[3], "10:00:00");
	}

	/**
	 * Test add booking method with wrong date format
	 * @author mb2021
	 */
	@Test
	public void addBooking2()
	{
		d.execute("INSERT INTO User VALUES ('pp212', 'queens', 'D');");
		d.execute("INSERT INTO User VALUES ('ts425', 'loveyou3000', 'P');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) " +
				"VALUES ('P', 'Parker', '9J', '07823458234', 'pp212');");
		d.execute("INSERT INTO Patient (forename, surname, gender, dob, did, username) " +
				"VALUES ('Tony', 'Stark', 'M', '1970-05-29', 1, 'ts425');");

		d.addBooking("Tony", "Stark", "20/03/2021", "10:00:00");
		checkOutput("com.mysql.cj.jdbc.exceptions.MysqlDataTruncation");
		assertTrue(printTestPassed);
	}

	/**
	 * Test add booking method with wrong time format
	 * @author mb2021
	 */
	@Test
	public void addBooking3()
	{
		d.execute("INSERT INTO User VALUES ('pp212', 'queens', 'D');");
		d.execute("INSERT INTO User VALUES ('ts425', 'loveyou3000', 'P');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) " +
				"VALUES ('P', 'Parker', '9J', '07823458234', 'pp212');");
		d.execute("INSERT INTO Patient (forename, surname, gender, dob, did, username) " +
				"VALUES ('Tony', 'Stark', 'M', '1970-05-29', 1, 'ts425');");

		d.addBooking("Tony", "Stark", "2021-03-10", "10-00");
		checkOutput("com.mysql.cj.jdbc.exceptions.MysqlDataTruncation");
		assertTrue(printTestPassed);
	}

	/**
	 * Test add booking method with wrong patient forename format
	 * @author mb2021
	 */
	@Test
	public void addBooking4()
	{
		d.execute("INSERT INTO User VALUES ('pp212', 'queens', 'D');");
		d.execute("INSERT INTO User VALUES ('ts425', 'loveyou3000', 'P');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) " +
				"VALUES ('P', 'Parker', '9J', '07823458234', 'pp212');");
		d.execute("INSERT INTO Patient (forename, surname, gender, dob, did, username) " +
				"VALUES ('Tony', 'Stark', 'M', '1970-05-29', 1, 'ts425');");

		d.addBooking("Steve", "Stark", "2021-03-10", "10:00:00");
		checkOutput("java.sql.SQLIntegrityConstraintViolationException");
		assertTrue(printTestPassed);
	}

	/**
	 * Test add booking method with wrong patient surname format
	 * @author mb2021
	 */
	@Test
	public void addBooking5()
	{
		d.execute("INSERT INTO User VALUES ('pp212', 'queens', 'D');");
		d.execute("INSERT INTO User VALUES ('ts425', 'loveyou3000', 'P');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) " +
				"VALUES ('P', 'Parker', '9J', '07823458234', 'pp212');");
		d.execute("INSERT INTO Patient (forename, surname, gender, dob, did, username) " +
				"VALUES ('Tony', 'Stark', 'M', '1970-05-29', 1, 'ts425');");

		d.addBooking("Tony", "Rogers", "2021-03-10", "10:00:00");
		checkOutput("java.sql.SQLIntegrityConstraintViolationException");
		assertTrue(printTestPassed);
	}

	/**
	 * Test remove booking method with normal operations.
	 * @author mb2021
	 */
	@Test
	public void removeBooking1()
	{
		d.execute("INSERT INTO User VALUES ('pp212', 'queens', 'D');");
		d.execute("INSERT INTO User VALUES ('ts425', 'loveyou3000', 'P');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) " +
				"VALUES ('P', 'Parker', '9J', '07823458234', 'pp212');");
		d.execute("INSERT INTO Patient (forename, surname, gender, dob, did, username) " +
				"VALUES ('Tony', 'Stark', 'M', '1970-05-29', 1, 'ts425');");
		d.execute("INSERT INTO Booking " +
				"VALUES (1, 1, '2021-03-11', '11:00:00');");

		// Check before that row exists, check after that row does not exist
		assertEquals(d.executeSelect("SELECT EXISTS (SELECT * FROM Booking);")[0], "1");
		d.removeBooking("Tony", "Stark", "2021-03-11", "11:00:00");
		assertEquals(d.executeSelect("SELECT EXISTS (SELECT * FROM Booking);")[0], "0");
	}

	/**
	 * Test remove booking method with no rows beforehand in table.
	 * @author mb2021
	 */
	@Test
	public void removeBooking2()
	{
		assertEquals(d.executeSelect("SELECT EXISTS (SELECT * FROM Booking);")[0], "0");
		d.removeBooking("Tony", "Stark", "2021-03-11", "11:00:00");
		checkOutput("");
		assertTrue(printTestPassed);
	}

	/**
	 * Test remove booking method with row being removed not in the table.
	 * @author mb2021
	 */
	@Test
	public void removeBooking3()
	{
		d.execute("INSERT INTO User VALUES ('bb198', 'hulk', 'D');");
		d.execute("INSERT INTO User VALUES ('ca290', 'america', 'P');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) " +
				"VALUES ('B', 'Banner', '2B', '07823458235', 'bb198');");
		d.execute("INSERT INTO Patient (forename, surname, gender, dob, did, username) " +
				"VALUES ('Steve', 'Rogers', 'M', '1918-07-04', 2, 'ca290');");

		// Check before that row exists, check after that row does not exist
		d.removeBooking("Tony", "Stark", "2021-03-11", "11:00:00");
		checkOutput("");
		assertTrue(printTestPassed);
	}

	/**
	 * Test update booking method with normal operations.
	 * @author mb2021
	 */
	@Test
	public void updateBooking1()
	{
		d.execute("INSERT INTO User VALUES ('pp212', 'queens', 'D');");
		d.execute("INSERT INTO User VALUES ('ts425', 'loveyou3000', 'P');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) " +
				"VALUES ('P', 'Parker', '9J', '07823458234', 'pp212');");
		d.execute("INSERT INTO Patient (forename, surname, gender, dob, did, username) " +
				"VALUES ('Tony', 'Stark', 'M', '1970-05-29', 1, 'ts425');");
		d.execute("INSERT INTO Booking " +
				"VALUES (1, 1, '2021-03-11', '11:00:00');");

		// Should not be any rows with time '13:00:00' before update
		assertEquals(d.executeSelect("SELECT EXISTS (SELECT * FROM Booking WHERE time='11:00:00');")[0], "1");
		assertEquals(d.executeSelect("SELECT EXISTS (SELECT * FROM Booking WHERE date='2021-03-11');")[0], "1");
		assertEquals(d.executeSelect("SELECT EXISTS (SELECT * FROM Booking WHERE time='13:00:00');")[0], "0");
		assertEquals(d.executeSelect("SELECT EXISTS (SELECT * FROM Booking WHERE date='2021-03-22');")[0], "0");
		d.updateBooking("Tony", "Stark", "2021-03-11", "11:00:00",
				"2021-03-22", "13:00:00");
		// Should not be any rows with time '11:00:00' after update
		assertEquals(d.executeSelect("SELECT EXISTS (SELECT * FROM Booking WHERE time='11:00:00');")[0], "0");
		assertEquals(d.executeSelect("SELECT EXISTS (SELECT * FROM Booking WHERE date='2021-03-11');")[0], "0");
		assertEquals(d.executeSelect("SELECT EXISTS (SELECT * FROM Booking WHERE time='13:00:00');")[0], "1");
		assertEquals(d.executeSelect("SELECT EXISTS (SELECT * FROM Booking WHERE date='2021-03-22');")[0], "1");
	}

	/**
	 * Test update booking method with no rows in the table.
	 * @author mb2021
	 */
	@Test
	public void updateBooking2()
	{
		// Should not be any rows with time '13:00:00' before update
		assertEquals(d.executeSelect("SELECT EXISTS (SELECT * FROM Booking WHERE time='13:00:00');")[0], "0");
		assertEquals(d.executeSelect("SELECT EXISTS (SELECT * FROM Booking WHERE date='2021-03-22');")[0], "0");
		d.updateBooking("Tony", "Stark", "2021-03-11", "11:00:00",
				"2021-03-22", "13:00:00");
		// Should not be any rows with time '13:00:00' after update
		assertEquals(d.executeSelect("SELECT EXISTS (SELECT * FROM Booking WHERE time='13:00:00');")[0], "0");
		assertEquals(d.executeSelect("SELECT EXISTS (SELECT * FROM Booking WHERE date='2021-03-22');")[0], "0");
	}

	/**
	 * Test update booking method with wrong row in the table.
	 * @author mb2021
	 */
	@Test
	public void updateBooking3()
	{
		d.execute("INSERT INTO User VALUES ('bb198', 'hulk', 'D');");
		d.execute("INSERT INTO User VALUES ('ca290', 'america', 'P');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) " +
				"VALUES ('B', 'Banner', '2B', '07823458235', 'bb198');");
		d.execute("INSERT INTO Patient (forename, surname, gender, dob, did, username) " +
				"VALUES ('Steve', 'Rogers', 'M', '1918-07-04', 2, 'ca290');");

		// Should not be any rows with time '13:00:00' before update
		assertEquals(d.executeSelect("SELECT EXISTS (SELECT * FROM Booking WHERE time='13:00:00');")[0], "0");
		assertEquals(d.executeSelect("SELECT EXISTS (SELECT * FROM Booking WHERE date='2021-03-22');")[0], "0");
		d.updateBooking("Tony", "Stark", "2021-03-11", "11:00:00",
				"2021-03-22", "13:00:00");
		// Should not be any rows with time '11:00:00' after update
		assertEquals(d.executeSelect("SELECT EXISTS (SELECT * FROM Booking WHERE time='13:00:00');")[0], "0");
		assertEquals(d.executeSelect("SELECT EXISTS (SELECT * FROM Booking WHERE date='2021-03-22');")[0], "0");
	}

	/**
	 * Normal operation
	 * @author eh443
	 */
	@Test
	public void sendMessage1(){
		d.addUser("jc", "saviour", 'P');
		d.sendMessage("10:00:00", "1900/1/1", "confirmation header", "confirmation body", "jc");
		assertEquals(d.executeSelect("SELECT * FROM Message WHERE username ='jc'")[5], "jc");
	}

	/**
	 * Message sender doesnt exist
	 * @author eh443
	 */
	@Test
	public void sendMessage2(){
		d.sendMessage("10:00:00", "1900/1/1", "confirmation header", "confirmation body", "jc");
		//assertEquals(d.executeSelect("SELECT * FROM Message WHERE username ='jc'")[5], "jc");
		checkOutput("java.sql.SQLIntegrityConstraintViolationException");
		assertTrue(printTestPassed);
	}

	/**
	 * Invalid date
	 * @author eh443
	 */
	@Test
	public void sendMessage3(){
		d.addUser("jc", "saviour", 'P');
		d.sendMessage("10:00:00", "1000/200/23", "confirmation header", "confirmation body", "jc");
		checkOutput("com.mysql.cj.jdbc.exceptions.MysqlDataTruncation");
		assertTrue(printTestPassed);
	}

	/**
	 * Normal operation
	 * @author eh443
	 */
	@Test
	public void reassignDoctor1(){
		d.execute("INSERT INTO User VALUES ('glc', 'costanza', 'D');");
		d.execute("INSERT INTO User VALUES ('ck', 'kramer', 'D');");
		d.execute("INSERT INTO User VALUES ('jc', 'saviour', 'P');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) VALUES ('G', 'Costanza', '2B', '07823458234', 'glc');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) VALUES ('C', 'Kramer', '5B', '05923995721', 'ck');");
		d.execute("INSERT INTO Patient (forename, surname, gender, dob, phoneNo, did, username) VALUES ('Jesus', 'Christ', 'M', '1900/1/1', '0724376166', 1, 'jc');");

		d.reassignDoctor(2, 1);
		assertEquals(d.executeSelect("SELECT * FROM Patient WHERE username ='jc'")[6], "2");
	}

	/**
	 * Reassign patient to doctor that doesnt exist
	 * @author eh443
	 */
	@Test
	public void reassignDoctor2(){
		//d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) VALUES ('G', 'Costanza', '2B', '07823458234', 'glc');");
		d.execute("INSERT INTO User VALUES ('glc', 'costanza', 'D');");
		d.execute("INSERT INTO User VALUES ('jc', 'saviour', 'P');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) VALUES ('G', 'Costanza', '2B', '07823458234', 'glc');");
		d.execute("INSERT INTO Patient (forename, surname, gender, dob, phoneNo, did, username) VALUES ('Jesus', 'Christ', 'M', '1900/1/1', '0724376166', 1, 'jc');");

		d.reassignDoctor(2, 1);
		checkOutput("java.sql.SQLIntegrityConstraintViolationException");
		assertTrue(printTestPassed);
	}

	/**
	 * Test whether logging activity and fetching it works
	 * @author jjrf2
	 */
	@Test
	public void testActivityLog()
	{
		// Dummy data
		d.execute("INSERT INTO User VALUES ('jas', 'seinfeld', 'R');");
		d.execute("INSERT INTO User VALUES ('glc', 'costanza', 'D');");
		d.execute("INSERT INTO User VALUES ('jc', 'saviour', 'P');");
		d.execute("INSERT INTO Doctor (initial, surname, roomNo, phoneNo, username) VALUES ('G', 'Costanza', '2B', '07823458234', 'glc');");
		d.execute("INSERT INTO Patient (forename, surname, gender, dob, phoneNo, did, username) VALUES ('Jesus', 'Christ', 'M', '1900/1/1', '0724376166', NULL, 'glc');");
		d.execute("INSERT INTO Booking (forename, surname, gender, dob, phoneNo, did, username) VALUES ('Jesus', 'Christ', 'M', '1900/1/1', '0724376166', NULL, 'glc');");
		
		d.connect(); // Connected
		d.login("jas", "seinfeld"); // Logged in;
		d.logAccess(1, null, null, null);
		d.logAccess(10, 1, 1, null);
		d.logAccess(13, 1, 1, "Booking at 10:00 on 2021/03/01");
		
		ArrayList<String[]> results = d.fetchActivity();
		assertEquals(results.size(), 3); // Activity exists
		
		assertEquals(results.get(2)[0], "jas (R)"); // Correct username
		assertEquals(results.get(2)[1], "1"); // Correct activity index
		assertNull(results.get(2)[2]); // No details
		assertNull(results.get(2)[5]); // No doctor
		assertNull(results.get(2)[6]); // No patient
		
		assertEquals(results.get(1)[0], "jas (R)"); // Correct username
		assertEquals(results.get(1)[1], "10"); // Correct activity index
		assertNull(results.get(1)[2]); // No details
		assertEquals(results.get(1)[5], "G. Costanza"); // Correct doctor
		assertEquals(results.get(1)[6], "Jesus Christ"); // Correct patient
		
		assertEquals(results.get(0)[0], "jas (R)"); // Correct username
		assertEquals(results.get(0)[1], "13"); // Correct activity index
		assertEquals(results.get(0)[2], "Booking at 10:00 on 2021/03/01"); // Correct details
		assertEquals(results.get(0)[5], "G. Costanza"); // Correct doctor
		assertEquals(results.get(0)[6], "Jesus Christ"); // Correct patient
		
		d.disconnect();
		assertEquals(d.fetchActivity().size(), 0); // No data
	}

	/**
	 * Method for checking printed output from program (mostly for printed exceptions)
	 * @param expectedOutput The expected output.
	 */
    private void checkOutput(String expectedOutput)
    {  	
        String actualOutput = outContent.toString().trim()
                            + errContent.toString().trim();
        if (!actualOutput.contains(expectedOutput)) {
        	StringBuilder builder = new StringBuilder();
        	if(expectedOutput.isEmpty()) {
                builder.append("No output expected");
            }
        	else {
        		printTestPassed = false;
        		builder.append("Expected to contain: ")
                .append(expectedOutput);
        	}
        	builder.append("\n")
            .append("    Actual output was: ")
            .append(actualOutput);
            report = builder.toString();
        }
        else {
            report = null;
            printTestPassed = true;
        }   
    }

	/**
	 * Debug jUnit class
	 * @param x String to standard output.
	 */
	private void printSomething(String x)
    {
    	System.setOut(originalOut);
        System.setErr(originalErr);
        System.out.println(x);
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }
}
