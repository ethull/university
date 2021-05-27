package com.sd.group4f;

import java.util.ArrayList;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 * Doctor tab to display list of doctors, their details and actions to perform on them.
 * @author jjrf2, eh443
 * @version 2021.03.30
 */
public class DoctorTab extends Tab
{
	private static final long serialVersionUID = 1L;
	
	// Main class pointer
	private Main main;
	
	// Table data
	private ArrayList<String[]> doctors;
	private String[][] data;
	
	// Swing components
	private JPanel doctorsPanel;
	private JTable doctorsTable;
	private JPanel detailsPanel;
	private JLabel detailsLabel;
	private JTextPane textPane;
	private JPanel actionsPanel;
	
	/**
	 * Construct UI and populate table of doctors.
	 * @param main	Instance of main class to reference
	 */
	public DoctorTab(Main main)
	{
		// Setup variables
		super();
		this.main = main;
		tabName = "Manage Doctors";
		
		// Titled border of doctor table
		doctorsPanel = new JPanel(new GridBagLayout());
		doctorsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Doctors"));
		this.add(doctorsPanel, Main.generateConstraints(0, 0, 1, 2, 2, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(8, 8, 8, 4)));
		
		// Setup table data
		String[] columnNames = {"Name", "Username", "Room No.", "Phone No."};
		doctors = main.dbManager.fetchDoctors();
		data = new String[doctors.size()][4];
		for (int i = 0; i < doctors.size(); i++) {
			String[] doctor = doctors.get(i);
			data[i][0] = doctor[1] + ". " + doctor[2];
			data[i][1] = doctor[6];
			data[i][2] = doctor[3];
			data[i][3] = doctor[4];
		}
		
		// Table of doctors
		doctorsTable = new JTable(data, columnNames);
	    doctorsTable.setDefaultEditor(Object.class, null);
	    doctorsTable.getTableHeader().setReorderingAllowed(false);
		JScrollPane scrollPane = new JScrollPane(doctorsTable);
		doctorsPanel.add(scrollPane, Main.generateConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(8, 8, 8, 8)));
		// Update detail panel on selection
		doctorsTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent arg0) {
                if (arg0.getButton() == MouseEvent.BUTTON1) {
                	refreshDetails(doctorsTable.getSelectedRow(), true);
                }
            }
        });
		
		// Titled border of doctor details
		detailsPanel = new JPanel(new GridBagLayout());
		detailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Details"));
		this.add(detailsPanel, Main.generateConstraints(1, 0, 1, 1, 1, 2, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(8, 4, 4, 8)));
		
		// No doctor selected notice
		detailsLabel = new JLabel("Please select a doctor");
		detailsLabel.setForeground(new Color(166, 166, 166));
		detailsPanel.add(detailsLabel);
		
		// Doctor information display
		textPane = new JTextPane();
		textPane.setBorder(null);
		textPane.setBackground(new Color(238, 238, 238));
		textPane.setContentType("text/html");
		textPane.setEditable(false);
		detailsPanel.add(textPane, Main.generateConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(8, 8, 8, 8)));
		//refreshDetails(0, true);
		
		// Titled border of actions panel
		actionsPanel = new JPanel(new GridBagLayout());
		actionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Actions"));
		this.add(actionsPanel, Main.generateConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(4, 4, 8, 8)));
		
		// Button to add new doctor
		JButton button = new JButton("Add new doctor");
		actionsPanel.add(button);
		// Popup form when pressed
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				addDoctorPopup();
			}
		});
	}
	
	/**
	 * Popup window with form to enter details of new doctor.
	 */
	private void addDoctorPopup() 
	{
		// Swing text fields
		JTextField unField = new JTextField("");
		JTextField pwField = new JTextField("");
		JTextField initialField = new JTextField("");
		JTextField surnameField = new JTextField("");
		JTextField roomNoField = new JTextField("");
		JTextField phoneNoField = new JTextField("");
		JTextField backgroundField = new JTextField("");
		
		// Panel to hold swing text inputs
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(new JLabel("username"));
		panel.add(unField);
		panel.add(new JLabel("password"));
		panel.add(pwField);
		panel.add(new JLabel("initial"));
		panel.add(initialField);
		panel.add(new JLabel("surname"));
		panel.add(surnameField);
		panel.add(new JLabel("room number"));
		panel.add(roomNoField);
		panel.add(new JLabel("phone number"));
		panel.add(phoneNoField);
		panel.add(new JLabel("background"));
		panel.add(backgroundField);
		
		// Show popup dialog with the above panel as its body
		int result = JOptionPane.showConfirmDialog(null, panel, "Add a new doctor",
			JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		// If the user clicks yes
		if (result == JOptionPane.OK_OPTION) {
			// Call db mtd to add the doctor with values extracted from the text inputs
			int did = main.dbManager.addDoctor(initialField.getText().toCharArray()[0], surnameField.getText(), roomNoField.getText(), phoneNoField.getText(), backgroundField.getText(), unField.getText(), pwField.getText(), 'D');
			main.dbManager.logAccess(6, did, null, null);
		}
	}
	
	/**
	 * Refresh doctor information panel upon selection of doctor.
	 * @param selection	Row of selected doctor
	 * @param selected	
	 */
	private void refreshDetails(int selection, boolean selected)
	{
		if (selected) {
			if (doctors.size() > selection) {
				String[] doctor = doctors.get(selection);

				// Setup HTML styling
				String details = "<style> body { font-family: 'Courier New'; background-color: #EEEEEE; } div { border-top: 1px dashed #A6A6A6; margin-top: 14px; } i { color: #696969; } </style>";

				// Add details as HTML
				details = details + "<b>" + doctor[1] + ". " + doctor[2] + "</b> <i>(" + doctor[6] + ")</i>";
				details = details + "<div></div> Room No. " + doctor[3];
				details = details + "<br> Phone No. " + doctor[4];
				if (doctor[5] == null) details = details + "<div></div> No background.";
				else details = details + "<div></div>" + doctor[5];
				
				main.dbManager.logAccess(5, Integer.parseInt(doctor[0]), null, null);

				// Refresh text pane
				textPane.setContentType("text/plain");
				textPane.setContentType("text/html");
				textPane.setText(details);
			} else {
				//there are no doctors to select
				selected=false;
			}
		}

		// Show no doctor selected notice if required
		detailsLabel.setVisible(!selected);
	}
	
	/**
	 * Log system access.
	 */
	public void refresh()
	{
		main.dbManager.logAccess(4, null, null, null);
	}
}
