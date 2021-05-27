package com.sd.group4f;

import java.util.ArrayList;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import java.sql.Time;
import java.sql.Date;

/**
 * Patient tab to display list of patients, their details and actions to perform on them.
 * @author jjrf2, eh443
 * @version 2021.03.30
 */
public class PatientTab extends Tab
{
	private static final long serialVersionUID = 1L;
	
	// Main class pointer
	private Main main;
	
	// Swing components
	private JPanel patientsPanel;
	private JPanel detailsPanel;
	private JPanel actionsPanel;
	
	/**
	 * Construct UI.
	 * @param main	Instance of main class to reference
	 */
	public PatientTab(Main main)
	{
		// Setup variables
		super();
		this.main = main;
		tabName = "Manage Patients";
		
		// Titled border of patient table
		patientsPanel = new JPanel(new GridBagLayout());
		patientsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Patients"));
		this.add(patientsPanel, Main.generateConstraints(0, 0, 1, 2, 2, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(8, 8, 8, 4)));
		
		// No patient data notice
		JLabel label = new JLabel("No patients to show");
		label.setForeground(new Color(166, 166, 166));
		patientsPanel.add(label);

		// Titled border of patient details
		detailsPanel = new JPanel(new GridBagLayout());
		detailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Details"));
		this.add(detailsPanel, Main.generateConstraints(1, 0, 1, 1, 1, 2, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(8, 4, 4, 8)));

		// No patient selected notice
		label = new JLabel("Please select a patient");
		label.setForeground(new Color(166, 166, 166));
		detailsPanel.add(label);

		// Titled border of actions panel
		actionsPanel = new JPanel(new GridBagLayout());
		actionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Actions"));
		this.add(actionsPanel, Main.generateConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(4, 4, 8, 8)));

		// Button to add new patient
		JButton addButton = new JButton("Add new patient");
		actionsPanel.add(addButton, Main.generateConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(4, 4, 4, 4)));
		// Popup form when pressed
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				addPatientPopup();
			}
		});

		// Button to add new patient
		JButton reassignButton = new JButton("Reassign a patients doctor");
		actionsPanel.add(reassignButton, Main.generateConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(4, 4, 4, 4)));
		// Popup form when pressed
		reassignButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				reassignPopup();
			}
		});
	}

	/**
	 * Popup window with form to enter details of new patient.
	 */
	private void addPatientPopup() 
	{
		// Swing input fields
		JTextField unField = new JTextField("");
		JTextField pwField = new JTextField("");
		JTextField forenameField = new JTextField("");
		JTextField surnameField = new JTextField("");
		// Combo box
        Character[] items = {'M', 'F', 'O'};
        JComboBox<Character> genderComboBox = new JComboBox<Character>(items);
		JTextField dobField = new JTextField("0000-00-00");
		JTextField phoneNoField = new JTextField("");

		// Fetch names of all doctors
		ArrayList<String[]> doctors = main.dbManager.fetchDoctors();
		String[] doctorNames = new String[doctors.size()];
		// Format for a JList component
		for (int i = 0; i<doctors.size(); i++) {
			doctorNames[i] = doctors.get(i)[1] + "." + doctors.get(i)[2];
		}
		// Create JList
		JList<String> doctorList = new JList<String>(doctorNames);

		// Panel to hold swing inputs
		JPanel panel = new JPanel(new GridLayout(1, 2));
		panel.add(new JLabel("username"));
		panel.add(unField);
		panel.add(new JLabel("password"));
		panel.add(pwField);
		panel.add(new JLabel("forename"));
		panel.add(forenameField);
		panel.add(new JLabel("surname"));
		panel.add(surnameField);
		panel.add(new JLabel("gender"));
		panel.add(genderComboBox);
		panel.add(new JLabel("date of birth"));
		panel.add(dobField);
		panel.add(new JLabel("phone number"));
		panel.add(phoneNoField);
		panel.add(new JLabel("select a doctor"));
		panel.add(doctorList);

		// Show popup dialog with the above panel as its body
		int result = JOptionPane.showConfirmDialog(null, panel, "Add a new doctor",
			JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		// If the user clicks yes
		if (result == JOptionPane.OK_OPTION) {
			// Get the selected doctors did (from the JList component)
			int did = 0;
			for (int i = 0; i<doctors.size(); i++) {
				if (i == doctorList.getSelectedIndex()) did = Integer.parseInt(doctors.get(i)[0]);
			}
			// Call db mtd to add the patient with values extracted from the swing inputs
			int pid = main.dbManager.addPatient(forenameField.getText(), surnameField.getText(), (Character) genderComboBox.getSelectedItem(), dobField.getText(), phoneNoField.getText(), did, unField.getText(), pwField.getText(), 'P');
			main.dbManager.logAccess(9, null, pid, null);
			
			// Send confirmation messages to patient
			String msgBody = "You have been assigned to doctor: " + doctorNames[doctorList.getSelectedIndex()];
			sendConfirmationMsg(msgBody, unField.getText());
		}
	}

	/**
	 * Popup window with form to select a doctor and the patient to reassign
	 */
	private void reassignPopup() 
	{
		// Fetch names of all doctors
		ArrayList<String[]> doctors = main.dbManager.fetchDoctors();
		String[] doctorNames = new String[doctors.size()];
		for (int i = 0; i<doctors.size(); i++) {
			doctorNames[i] = doctors.get(i)[1] + "." + doctors.get(i)[2];
		}
		JList<String> doctorList = new JList<String>(doctorNames);

		// Fetch names of all patients
		ArrayList<String[]> patients = main.dbManager.fetchPatients();
		String[] patientNames = new String[patients.size()];
		for (int i = 0; i<patients.size(); i++) {
			patientNames[i] = patients.get(i)[1] + " " + patients.get(i)[2];
		}
		JList<String> patientList = new JList<String>(patientNames);

		// Panel to hold swing inputs
		JPanel panel = new JPanel(new GridLayout(2, 2, 10, 0));

		panel.add(new JLabel("select a doctor"));
		panel.add(new JLabel("select a patient"));
		panel.add(doctorList);
		panel.add(patientList);

		// Show popup dialog with the above panel as its body
		int result = JOptionPane.showConfirmDialog(null, panel, "Reassign a patients doctor",
			JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		// If the user clicks yes
		if (result == JOptionPane.OK_OPTION) {
			// Get the selected doctors did (from the JList component)
			int did = 0;
			String doctorUsername = "";
			for (int i = 0; i<doctors.size(); i++) {
				if (i == doctorList.getSelectedIndex()) {
					did = Integer.parseInt(doctors.get(i)[0]);
					doctorUsername = doctors.get(i)[6];
				}
			}
			// Get the selected patients pid (from the JList component)
			int pid = 0;
			String patientUsername = "";
			for (int i = 0; i<patients.size(); i++) {
				if (i == patientList.getSelectedIndex()) {
					pid = Integer.parseInt(patients.get(i)[0]);
					patientUsername = patients.get(i)[7];
				}
			}

			// Call db mtd to update a patients assigned doctor
			main.dbManager.reassignDoctor(did, pid);
			main.dbManager.logAccess(10, did, pid, null);
			
			// Send confirmation messages to doctor and patient
			String doctorMsg = "You have been assigned patient: " + patientUsername;
			sendConfirmationMsg(doctorMsg, doctorUsername);
			String patientBody = "You have been assigned to doctor: " + doctorUsername; 
			sendConfirmationMsg(patientBody, patientUsername);
		}
	}

	/**
	 * Send a confirmation message to a user
	 * @param msgBody  The body of the message to send
	 * @param username The user to send the message to
	 */
	private void sendConfirmationMsg(String msgBody, String username){
		// Get current date and time
		long millis=System.currentTimeMillis();  
		String time = new Time(millis).toString();
		String date = new Date(millis).toString();  
		// Setup confirmation text
		String msgHead = "Confirmation message";
		// Call db mtd to send messages
		main.dbManager.sendMessage(time, date, msgHead, msgBody, username);
	}
	
	/**
	 * Log system access.
	 */
	public void refresh()
	{
		main.dbManager.logAccess(7, null, null, null);
	}
}
