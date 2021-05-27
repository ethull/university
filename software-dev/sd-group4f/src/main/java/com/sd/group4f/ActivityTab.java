package com.sd.group4f;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;

/**
 * Activity tab to display activity log of all access to the system.
 * @author jjrf2
 * @version 2021.03.30
 */
public class ActivityTab extends Tab
{
	private static final long serialVersionUID = 1L;

	// Main class pointer
	private Main main;
	
	private JPanel activityPanel;
	private JLabel activityLabel;
	private JTextPane textPane;
	private JScrollPane scrollPane;
	private String activityText;
	
	/**
	 * Construct UI.
	 * @param main	Instance of main class to reference
	 */
	public ActivityTab(Main main)
	{
		// Setup variables
		super();
		this.main = main;
		tabName = "Activity Log";
		
		// Titled border
		activityPanel = new JPanel(new GridBagLayout());
		activityPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Access History"));
		this.add(activityPanel, Main.generateConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(8, 8, 8, 8)));

		// Empty history notice
		activityLabel = new JLabel("No activity to show");
		activityLabel.setForeground(new Color(166, 166, 166));
		activityPanel.add(activityLabel);

		// Activity log
		textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setEditable(false);
		scrollPane = new JScrollPane(textPane);
		activityPanel.add(scrollPane, Main.generateConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(8, 8, 8, 8)));
		
		refresh();
	}
	
	/**
	 * Pull activity from database.
	 */
	public void refresh()
	{
		// Pull activity from databse
		ArrayList<String[]> activity = main.dbManager.fetchActivity();
		
		// Setup HTML styling
		activityText = "<style> body { padding: 6px 8px; font-family: 'Courier New';  } div { background-color: #EAEFF5; padding: 8px; margin: 4px 0px; border: 1px solid; } i { color: #696969; } </style>";
		
		// Add activity as HTML
		for (String[] action : activity) {
			switch (Integer.parseInt(action[1])) {
				case 1:
					activityText = activityText + "<div><b>" + action[0] + "</b> logged in <i>( " + action[4] + "  -  " + action[3] + " )</i></div>";
					break;
				case 2:
					activityText = activityText + "<div><b>" + action[0] + "</b> logged out <i>( " + action[4] + "  -  " + action[3] + " )</i></div>";
					break;
				case 3:
					activityText = activityText + "<div style='background-color:#F3EAF5;'><b>" + action[0] + "</b> accessed message inbox <i>( " + action[4] + "  -  " + action[3] + " )</i></div>";
					break;
				case 4:
					activityText = activityText + "<div style='background-color:#F5EAEA;'><b>" + action[0] + "</b> accessed doctor list <i>( " + action[4] + "  -  " + action[3] + " )</i></div>";
					break;
				case 5:
					activityText = activityText + "<div style='background-color:#F5EAEA;'><b>" + action[0] + "</b> viewed doctor record of <b>" + action[5] + "</b> <i>( " + action[4] + "  -  " + action[3] + " )</i></div>";
					break;
				case 6:
					activityText = activityText + "<div style='background-color:#F5EAEA;'><b>" + action[0] + "</b> registered new doctor <b>" + action[5] + "</b> <i>( " + action[4] + "  -  " + action[3] + " )</i></div>";
					break;
				case 7:
					activityText = activityText + "<div style='background-color:#F5F2EA;'><b>" + action[0] + "</b> accessed patient list <i>( " + action[4] + "  -  " + action[3] + " )</i></div>";
					break;
				case 8:
					activityText = activityText + "<div style='background-color:#F5F2EA;'><b>" + action[0] + "</b> viewed patient record of <b>" + action[6] + "</b> <i>( " + action[4] + "  -  " + action[3] + " )</i></div>";
					break;
				case 9:
					activityText = activityText + "<div style='background-color:#F5F2EA;'><b>" + action[0] + "</b> registered new patient <b>" + action[6] + "</b> <i>( " + action[4] + "  -  " + action[3] + " )</i></div>";
					break;
				case 10:
					activityText = activityText + "<div style='background-color:#F5F2EA;'><b>" + action[0] + "</b> assigned doctor <b>" + action[5] + "</b> to patient <b>" + action[6] + "</b> <i>( " + action[4] + "  -  " + action[3] + " )</i></div>";
					break;
				case 11:
					activityText = activityText + "<div style='background-color:#EAF5EB;'><b>" + action[0] + "</b> accessed booking list <i>( " + action[4] + "  -  " + action[3] + " )</i></div>";
					break;
				case 12:
					activityText = activityText + "<div style='background-color:#EAF5EB;'><b>" + action[0] + "</b> viewed booking between <b>" + action[5] + "</b> and <b>" + action[6] + "</b> <i>( " + action[4] + "  -  " + action[3] + " )</i><br>" + action[2] + "</div>";
					break;
				case 13:
					activityText = activityText + "<div style='background-color:#EAF5EB;'><b>" + action[0] + "</b> registered new booking between <b>" + action[5] + "</b> and <b>" + action[6] + "</b> <i>( " + action[4] + "  -  " + action[3] + " )</i><br>" + action[2] + "</div>";
					break;
				case 14:
					activityText = activityText + "<div style='background-color:#EAF5EB;'><b>" + action[0] + "</b> deleted booking between <b>" + action[5] + "</b> and <b>" + action[6] + "</b> <i>( " + action[4] + "  -  " + action[3] + " )</i><br>" + action[2] + "</div>";
					break;
				case 15:
					activityText = activityText + "<div style='background-color:#EAF5EB;'><b>" + action[0] + "</b> rescheduled booking between <b>" + action[5] + "</b> and <b>" + action[6] + "</b> <i>( " + action[4] + "  -  " + action[3] + " )</i><br>" + action[2] + "</div>";
					break;
				case 16:
					activityText = activityText + "<div style='background-color:#F3EAF5;'><b>" + action[0] + "</b> accessed activity log <i>( " + action[4] + "  -  " + action[3] + " )</i></div>";
					break;
				default:
			}
		}
		
		// Show empty history notice if empty
		activityLabel.setVisible(activity.size() == 0);
		
		// Refresh text pane
		textPane.setContentType("text/plain");
		textPane.setContentType("text/html");
		textPane.setText(activityText);
		
		// Reset scroll position to top after frame has rendered
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	scrollPane.getVerticalScrollBar().setValue(0);
		    }
		});
		
		// Log system access
		main.dbManager.logAccess(16, null, null, null);
	}
}
