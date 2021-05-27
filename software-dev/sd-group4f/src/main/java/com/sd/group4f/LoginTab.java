package com.sd.group4f;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * Login tab to display a temporary login prompt to the user
 * @author jjrf2, eh443
 * @version 2021.04.02
 */
public class LoginTab extends Tab
{
	private static final long serialVersionUID = 1L;
	
	private JPanel loginPanel;
	private JLabel userLabel;
	private JTextField userField;
	private JLabel passLabel;
	private JPasswordField passField;
	private JButton loginButton;
	private JLabel errorLabel;

	/**
	 * Construct UI.
	 * @param main	Instance of main class to reference
	 */
	public LoginTab(final Main main)
	{
		// Setup variables
		super();
		tabName = "Login";
		
		// Container panel for the login components
		loginPanel = new JPanel(new GridBagLayout());
		loginPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Login"));
		this.add(loginPanel, Main.generateConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(8, 8, 8, 8)));

		// Label for username field
		userLabel = new JLabel("Username:");
		loginPanel.add(userLabel, Main.generateConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(4, 4, 4, 4)));
		
		// Username input field
		userField = new JTextField(16);
		loginPanel.add(userField, Main.generateConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(4, 4, 4, 4)));
		userField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent event) {
				checkInputs();
			}
			public void insertUpdate(DocumentEvent event) {
				checkInputs();
			}
			public void removeUpdate(DocumentEvent event) {
				checkInputs();
			}
		});

		// Label for the password field
		passLabel = new JLabel("Password:");
		loginPanel.add(passLabel, Main.generateConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(4, 4, 4, 4)));
		
		// Password input field
		passField = new JPasswordField(16);
		loginPanel.add(passField, Main.generateConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(4, 4, 4, 4)));
		passField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent event) {
				checkInputs();
			}
			public void insertUpdate(DocumentEvent event) {
				checkInputs();
			}
			public void removeUpdate(DocumentEvent event) {
				checkInputs();
			}
		});

		// Login button, will trigger database login request and UI redirection if successful
		loginButton = new JButton("Login");
		loginPanel.add(loginButton, Main.generateConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.LINE_END, new Insets(4, 4, 4, 4)));
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				main.login(userField.getText(), new String(passField.getPassword()));
			}
		});
		loginButton.setEnabled(false);
		
		// Error label to show login failure to the user if neccessary
		errorLabel = new JLabel();
		errorLabel.setForeground(Color.RED);
		loginPanel.add(errorLabel, Main.generateConstraints(0, 3, 2, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.LINE_START, new Insets(4, 4, 4, 4)));
		errorLabel.setVisible(false);
	}
	
	/**
	 * Check inputted text for validity
	 */
	private void checkInputs()
	{
		loginButton.setEnabled(!userField.getText().equals("") && !(new String(passField.getPassword()).equals("")));
	}
	
	/**
	 * Clear input fields
	 */
	public void clear()
	{
		userField.setText("");
		passField.setText("");
		errorLabel.setVisible(false);
	}
	
	/**
	 * Show failed database connection to the user
	 */
	public void failedConnection()
	{
		errorLabel.setText("Failed to connect to database");
		errorLabel.setVisible(true);
	}
	
	/**
	 * Show failed login to the user
	 */
	public void failedLogin()
	{
		errorLabel.setText("Incorrect login details");
		errorLabel.setVisible(true);
	}
}
