package com.sd.group4f;

import java.util.ArrayList;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Manages all elements of the software.
 * Generates persistent UI including header bar and navigation bar.
 * Handles tab switching to access all features of the system.
 * @author jjrf2, eh443
 * @version 2021.03.30
 */
public class Main
{
	// Database management
	public boolean loggedIn;
	public DBManager dbManager;
	
	// Swing components
	public JFrame frame;
	private Container pane;
	private JPanel systemBar;
	private JLabel userLabel;
	private JButton logoutButton;
	private LoginTab loginTab;
	private JPanel navBar;
	
	// Lists of navigational elements
	private ArrayList<Tab> navTabs;
	private ArrayList<JButton> navButtons;
	
	/**
	 * Initialises program.
	 * @param args
	 */
	public static void main(String[] args)
	{
		Main main = new Main();
		main.loggedIn = false;
	}
	
	/**
	 * Construct main, database manager, frame and persistent UI.
	 */
	public Main()
	{
		loggedIn = false;
		dbManager = new DBManager("jemstone");
		
		navTabs = new ArrayList<Tab>();
		navButtons = new ArrayList<JButton>();
		
		// Setup window
		frame = new JFrame("CO559 - Group 4F");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pane = frame.getContentPane();
		pane.setLayout(new GridBagLayout());
		
		// Persistent UI

		// Header
		systemBar = new JPanel(new GridBagLayout());
		systemBar.setBackground(Color.WHITE);
		setAllSize(systemBar, new Dimension(40, 40));
		pane.add(systemBar, generateConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0)));
		
		// System name
		JLabel label = new JLabel("GP System Software");
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setFont(new Font(label.getFont().getFontName(), Font.PLAIN, 16));
		systemBar.add(label, generateConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 8, 0, 8)));
		
		// Username
		userLabel = new JLabel("");
		systemBar.add(userLabel, generateConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.LINE_START, new Insets(0, 8, 0, 8)));
		userLabel.setVisible(false);
		
		// Logout button
		logoutButton = new JButton("Logout");
		systemBar.add(logoutButton, generateConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.LINE_END, new Insets(0, 8, 0, 8)));
		logoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Confirm choice
				int confirm = JOptionPane.showOptionDialog(frame, "Logout of system?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, new String[] {"Yes", "No"});
				if (confirm == 0) logout();
			}
		});
		logoutButton.setVisible(false);

		// Separator
		JSeparator separator = new JSeparator();
		separator.setForeground(new Color(166, 166, 166));
		pane.add(separator, generateConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0)));
		
		// Login form
		loginTab = new LoginTab(this);
		pane.add(loginTab, generateConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0)));

		// Separator
		separator = new JSeparator();
		separator.setForeground(new Color(166, 166, 166));
		pane.add(separator, generateConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0)));

		// Navigation bar
		navBar = new JPanel(new GridBagLayout());
		navBar.setBackground(Color.WHITE);
		setAllSize(navBar, new Dimension(40, 40));
		pane.add(navBar, generateConstraints(0, 4, 1, 1, 1, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0)));
		
		frame.setSize(960, 540);
		frame.setVisible(true);
	}
	
	/**
	 * Instantiate instance of GridBagConstraints with the given constraints.
	 * @param gridx		Column index
	 * @param gridy		Row index
	 * @param gridwidth	Width in columns
	 * @param gridheight	Height in rows
	 * @param weightx	Horizontal distribution value
	 * @param weighty	Vertical distribution value
	 * @param fill		Mode of resizing
	 * @param anchor		Mode of placement
	 * @param insets		External padding
	 * @return			Generated constraints object
	 */
	public static GridBagConstraints generateConstraints(int gridx, int gridy, int gridwidth, int gridheight, int weightx, int weighty, int fill, int anchor, Insets insets)
	{
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.gridwidth = gridwidth;
		constraints.gridheight = gridheight;
		constraints.weightx = weightx;
		constraints.weighty = weighty;
		constraints.fill = fill;
		constraints.anchor = anchor;
		constraints.insets = insets;
		return constraints;
	}
	
	/**
	 * Applies all necessary size constraints.
	 * @param component	UI element to apply to
	 * @param size		Dimensions to use
	 */
	public static void setAllSize(Component component, Dimension size)
	{
		component.setMinimumSize(size);
		component.setMaximumSize(size);
		component.setPreferredSize(size);
	}
	
	/**
	 * Setup tab within pane and log its presence in the tab listing.
	 * @param tab		Tab to be used
	 * @param visible	Whether tab is currently open
	 */
	private void addTab(Tab tab, boolean visible)
	{
		pane.add(tab, generateConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0)));
		tab.setVisible(visible);
		navTabs.add(tab);
	}
	
	/**
	 * Replace tab within tab listing and navigation bar
	 * @param sourceTab	Tab to be replaced
	 * @param newTab	Tab to be added
	 */
	public void replaceTab(Tab sourceTab, final Tab newTab)
	{
		if (navTabs.contains(sourceTab)) {
			pane.remove(sourceTab);
			pane.add(newTab, generateConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0)));
			newTab.setVisible(true);
			sourceTab.setVisible(false);
			navTabs.set(navTabs.indexOf(sourceTab), newTab);
			JButton button = navButtons.get(navTabs.indexOf(newTab));
			for (ActionListener event : button.getActionListeners()) button.removeActionListener(event);
			// Navigate to tab on press
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					switchTab(newTab);
				}
			});
			frame.repaint();
		}
	}
	
	/**
	 * Navigate to a tab of the program structure and display it.
	 * @param tab	Destination tab
	 */
	private void switchTab(Tab tab)
	{
		for (JButton b : navButtons) b.setEnabled(true);
		navButtons.get(navTabs.indexOf(tab)).setEnabled(false);
		for (Tab t : navTabs) t.setVisible(false);
		tab.setVisible(true);
		tab.refresh();
	}
	
	/**
	 * Login to system through database manager and if successful, setup the software UI
	 * @param username	User login name
	 * @param password	User login password
	 */
	public void login(String username, String password)
	{
		if (dbManager.connect()) {
			// Check for existence of user within database
			loggedIn = dbManager.login(username, password);
			if (loggedIn) {
				// Log system access
				dbManager.logAccess(1, null, null, null);
				
				// Close login form
				loginTab.clear();
				loginTab.setVisible(false);
				
				// Setup persistent UI values with user information
				userLabel.setText("Logged in as: " + dbManager.username + " (" + dbManager.role.toString() + ")");
				userLabel.setVisible(true);
				logoutButton.setVisible(true);
				
				// Create all feature tabs of system
				addTab(new MessageTab(this), true);
				addTab(new DoctorTab(this), false);
				addTab(new PatientTab(this), false);
				addTab(new BookingTab(this), false);
				addTab(new ActivityTab(this), false);
				
				// Create options to navigate to each tab within navigation bar
				for (final Tab t : navTabs) {
					JButton button = new JButton(t.tabName);
					navBar.add(button, generateConstraints(navTabs.indexOf(t), 0, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.CENTER, new Insets(4, 4, 4, 4)));
					// Navigate to tab on press
					button.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							switchTab(t);
						}
					});
					if (navTabs.indexOf(t) == 0) button.setEnabled(false);
					navButtons.add(button);
				}
			}
			else loginTab.failedLogin(); // Notify user of incorrect login details
		}
		else loginTab.failedConnection(); // Notify user of database connection failure
		
	}
	
	/**
	 * Clear login information and close UI
	 */
	public void logout()
	{
		// Log system access
		dbManager.logAccess(2, null, null, null);
		
		// Clear user info
		loggedIn = false;
		dbManager.logout();
		
		// Open login form
		loginTab.clear();
		loginTab.setVisible(true);
		
		// Clear persistent UI
		userLabel.setText("");
		userLabel.setVisible(false);
		logoutButton.setVisible(false);
		
		// Close tabs and navigation bar
		for (Tab t : navTabs) t.getParent().remove(t);
		navTabs.clear();
		for (JButton b : navButtons) b.getParent().remove(b);
		navButtons.clear();
		frame.repaint();
	}
}