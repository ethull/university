package com.sd.group4f;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;

/**
 * Message tab to display user inbox.
 * @author jjrf2
 * @version 2021.03.30
 */
public class MessageTab extends Tab
{
	private static final long serialVersionUID = 1L;

	// Main class pointer
	private Main main;
	
	private JPanel messagePanel;
	private JLabel messageLabel;
	private JTextPane textPane;
	private JScrollPane scrollPane;
	private String messageText;
	
	/**
	 * Construct UI.
	 * @param main	Instance of main class to reference
	 */
	public MessageTab(Main main)
	{
		// Setup variables
		super();
		this.main = main;
		tabName = "View Messages";
		
		// Titled border
		messagePanel = new JPanel(new GridBagLayout());
		messagePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Messages"));
		this.add(messagePanel, Main.generateConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(8, 8, 8, 8)));

		// Empty inbox notice
		messageLabel = new JLabel("No messages to show");
		messageLabel.setForeground(new Color(166, 166, 166));
		messagePanel.add(messageLabel);

		// Inbox
		textPane = new JTextPane();
		//textPane.setBorder(null);
		//textPane.setBackground(new Color(238, 238, 238));
		textPane.setContentType("text/html");
		textPane.setEditable(false);
		scrollPane = new JScrollPane(textPane);
		//scrollPane.setBorder(null);
		messagePanel.add(scrollPane, Main.generateConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(8, 8, 8, 8)));
		
		refresh();
	}
	
	/**
	 * Pull inbox from database.
	 */
	public void refresh()
	{
		// Pull messages from databse
		ArrayList<String[]> messages = main.dbManager.fetchMessages();
		
		// Setup HTML styling
		messageText = "<style> body { padding: 6px 8px; font-family: 'Courier New';  } div { background-color: #EAEFF5; padding: 8px; margin: 4px 0px; border: 1px solid; } i { color: #696969; } </style>";
		
		// Add messages as HTML
		for (String[] message : messages) {
			messageText = messageText + "<div><b><u>" + message[0] + "</u></b> <i>( " + message[1] + "  -  " + message[2] + " )</i><br>" + message[3] + "</div>";
		}
		
		// Show empty inbox notice if empty
		messageLabel.setVisible(messages.size() == 0);
		
		// Refresh text pane
		textPane.setContentType("text/plain");
		textPane.setContentType("text/html");
		textPane.setText(messageText);
		
		// Reset scroll position to top after frame has rendered
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	scrollPane.getVerticalScrollBar().setValue(0);
		    }
		});
		
		// Log system access
		main.dbManager.logAccess(3, null, null, null);
	}
}
