package com.sd.group4f;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

/**
 * Booking tab to display list of bookings, their details and actions to perform on them.
 * @author jjrf2, mb2021
 * @version 2021.04.02
 */
public class BookingTab extends Tab
{
	// Stores the days of the week
	private static final String[] DAYS = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
	// Stores all the possible times of a booking.
	private static final String[] TIMES = {"09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30",
			"13:00", "13:30", "14:00", "14:30"};

	/**
	 *	Renders the row names of the JTable.
	 */
	private class RowNumberTable extends JTable
			implements ChangeListener, PropertyChangeListener, TableModelListener
	{
		private final JTable main;

		/**
		 * Constructor for the RowNumberTable class
		 * @param table Parent table.
		 */
		public RowNumberTable(JTable table)
		{
			main = table;
			main.addPropertyChangeListener(this);
			main.getModel().addTableModelListener(this);

			setFocusable(false);
			setAutoCreateColumnsFromModel(false);
			setSelectionModel(main.getSelectionModel());

			TableColumn column = new TableColumn();
			column.setHeaderValue(" ");
			addColumn(column);
			column.setCellRenderer(new RowNumberRenderer());

			getColumnModel().getColumn(0).setPreferredWidth(100);
			setPreferredScrollableViewportSize(getPreferredSize());
		}

		/**
		 * Keep the scrolling of the row headers in sync with the main table.
		 */
		@Override
		public void addNotify()
		{
			super.addNotify();
			Component component = getParent();

			if (component instanceof JViewport) {
				JViewport viewport = (JViewport) component;
				viewport.addChangeListener(this);
			}
		}

		/**
		 *  Delegate method to main table
		 */
		@Override
		public int getRowCount()
		{
			return main.getRowCount();
		}

		@Override
		public int getRowHeight(int row)
		{
			int rowHeight = main.getRowHeight(row);
			if (rowHeight != super.getRowHeight(row)) super.setRowHeight(row, rowHeight);
			return rowHeight;
		}

		/**
		 *  Make the opening times of the hospital the values of each row cell.
		 */
		@Override
		public Object getValueAt(int row, int column)
		{
			return rowTableDates.get(row);
		}

		/**
		 *  Don't edit data in the main TableModel by mistake
		 */
		@Override
		public boolean isCellEditable(int row, int column)
		{
			return false;
		}

		/**
		 *  Do nothing since the table ignores the model.
		 */
		@Override
		public void setValueAt(Object value, int row, int column)
		{}

		/**
		 * Keep the scrolling of the row headers in sync with the main table.
		 * @param e ChangeEvent.
		 */
		public void stateChanged(ChangeEvent e)
		{
			JViewport viewport = (JViewport) e.getSource();
			JScrollPane scrollPane = (JScrollPane) viewport.getParent();
			scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
		}

		/**
		 * Implement the PropertyChangeListener
		 * @param e PropertyChangeEvent
		 */
		public void propertyChange(PropertyChangeEvent e)
		{
			//  Keep the row table in sync with the main table
			if ("selectionModel".equals(e.getPropertyName())) {
				setSelectionModel(main.getSelectionModel());
			}
			if ("rowHeight".equals(e.getPropertyName())) {
				repaint();
			}
			if ("model".equals(e.getPropertyName())) {
				main.getModel().addTableModelListener(this);
				revalidate();
			}
		}

		/**
		 * Implement the TableModelListener
		 * @param e TableModelEvent
		 */
		@Override
		public void tableChanged(TableModelEvent e)
		{
			revalidate();
		}

		/**
		 *  Attempt to mimic the table header renderer
		 */
		private class RowNumberRenderer extends DefaultTableCellRenderer
		{
			public RowNumberRenderer()
			{
				setHorizontalAlignment(JLabel.CENTER);
			}

			public Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				if (table != null) {
					JTableHeader header = table.getTableHeader();
					if (header != null) {
						setForeground(header.getForeground());
						setBackground(header.getBackground());
						setFont(header.getFont());
					}
				}

				if (isSelected) setFont(getFont().deriveFont(Font.BOLD));

				setText((value == null) ? "" : value.toString());
				setBorder(UIManager.getBorder("TableHeader.cellBorder"));

				return this;
			}
		}
	}

	private static final long serialVersionUID = 1L;

	private final Main main;							// Main class pointer

	private int numDaysInMonth;							// Stores the amount of days in the selected month.
	private ArrayList<String> rowTableDates;			// Stores the names of each row in the table.

	private final HashMap<Pair<Integer, Integer>,
			List<String[]>> bookingDetails; 			// Stores the details of each booking

	private final JTextPane textPane;					// Text pane of the details panel
	private JComboBox<String> docFilter;				// Drop down box for doctors
	private JComboBox<String> patFilter;				// Drop down box for patients
	private JSpinner dateFilter;						// Box for selecting month and year

	private final JTable bookingsTable;					// JTable of bookings

	private final DefaultTableModel model;				// Models the bookings table

	private final JPanel detailsPanel;					// Panel to store contents of details
	private final JLabel detailsLabel;
	private JLabel rescheduleLabel;						// Tells user to select a booked appointment, notifies user they are in reschedule mode.
	private JButton addBookingButton;					// Button to add booking
	private JButton removeBookingButton;				// Button to remove booking
	private JButton updateBookingButton;				// Button to reschedule booking

	private int bookingSlotIndex;						// Keeps track of which booking is selected in the slot.

	private MouseListener normalML, rescheduleML; 		// Two mouse listeners for the booking GUI table.

	private String[] rescheduleDetails;					// The new booking slot of the rescheduling.
	
	/**
	 * Construct UI.
	 * @param main	Instance of main class to reference
	 */
	public BookingTab(Main main)
	{
		// Setup variables
		super();
		this.main = main;
		tabName = "Manage Bookings";

		// Titled border of booking table
		// Swing components
		JPanel bookingsPanel = new JPanel(new GridBagLayout());
		bookingsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Bookings"));
		this.add(bookingsPanel, Main.generateConstraints(0, 0, 1, 2, 2, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(8, 8, 8, 4)));


		// Setup booking table data
		bookingDetails = new HashMap<>();
		resetBookings();
		// Bookings table data
		String[][] data = new String[numDaysInMonth][TIMES.length];

		// Table of bookings
		model = new DefaultTableModel(data, TIMES);
		bookingsTable = new JTable(model);
		bookingsTable.setDefaultEditor(Object.class, null);
		bookingsTable.getTableHeader().setReorderingAllowed(false);
		// Make the rows bigger
		bookingsTable.setRowHeight(25);
		bookingsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JTable rowTable = new RowNumberTable(bookingsTable);

		setInitialDates();

		colourCellsInTable();

		createFilters(bookingsPanel);

		// Add scroll pane to the table
		JScrollPane scrollPane = new JScrollPane(bookingsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setRowHeaderView(rowTable);
		scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, rowTable.getTableHeader());
		bookingsPanel.add(scrollPane, Main.generateConstraints(0, 3, 4, 1, 1, 20,
				GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(0, 8, 8, 8)));


		// Titled border of booking details
		detailsPanel = new JPanel(new GridBagLayout());
		detailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Details"));
		this.add(detailsPanel, Main.generateConstraints(1, 0, 1, 1, 1, 2, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(8, 4, 4, 8)));

		// No booking selected notice
		detailsLabel = new JLabel("Please select a booked appointment");
		detailsLabel.setForeground(new Color(166, 166, 166));
		detailsPanel.add(detailsLabel);

		// Booking information display
		textPane = new JTextPane();
		textPane.setBorder(null);
		textPane.setBackground(new Color(238, 238, 238));
		textPane.setContentType("text/html");
		textPane.setEditable(false);
		detailsPanel.add(textPane, Main.generateConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(8, 8, 8, 8)));


		// Titled border of actions panel
		JPanel actionsPanel = new JPanel(new GridBagLayout());
		actionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Actions"));
		this.add(actionsPanel, Main.generateConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, new Insets(4, 4, 8, 8)));

		createBookingButtons(actionsPanel, rowTable);

		// No booking selected notice
		JLabel label = new JLabel("Please select a booking");
		label.setForeground(new Color(166, 166, 166));
		actionsPanel.add(label);

		// Add the mouse listeners
		createMouseListeners(label, rowTable);
		bookingsTable.addMouseListener(normalML);
		bookingSlotIndex = 0;
	}

	/**
	 * Appropriately colours the cells in the bookings JTable.
	 */
	private void colourCellsInTable()
	{
		bookingsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable jtable,
														   Object value, boolean isSelected, boolean hasFocus, int row, int col) {

				super.getTableCellRendererComponent(jtable, value, isSelected, hasFocus, row, col);

				String status = (String) jtable.getModel().getValueAt(row, col);
				String day = rowTableDates.get(row).substring(12, 15);

				// If day is saturday or sunday, colour row grey.
				if (day.equals("Sun") || day.equals("Sat")) {
					setBackground(Color.LIGHT_GRAY);
				}
				else if (status != null) {
					// Colour the cell red if cell is booked.
					if (status.equals("BOOKED")) setBackground(Color.RED);
				}
				else {
					// Create a normal empty white row
					setBackground(jtable.getBackground());
					setForeground(jtable.getForeground());
				}
				return this;
			}
		});
	}

	/**
	 * Adds the filters to the bookings panel.
	 * @param bookingsPanel The bookings panel.
	 */
	private void createFilters(JPanel bookingsPanel)
	{
		// Setup drop down box of doctors.
		docFilter = new JComboBox<>();
		for (String[] doctor : main.dbManager.fetchDoctors()) {
			docFilter.addItem(doctor[1] + "." + doctor[2]);
		}
		bookingsPanel.add(new JLabel("Select Doctor:"), Main.generateConstraints(0, 0, 1, 1, 1, 0,
				GridBagConstraints.NONE, GridBagConstraints.PAGE_START, new Insets(0, 8, 8, 8)));
		bookingsPanel.add(docFilter, Main.generateConstraints(0, 1, 1, 1, 1, 0,
				GridBagConstraints.NONE, GridBagConstraints.PAGE_START, new Insets(8, 8, 8, 0)));

		// Filter button for doctors drop down box.
		JButton filter = new JButton("Filter");
		bookingsPanel.add(filter, Main.generateConstraints(0, 2, 1, 1, 1, 0,
				GridBagConstraints.NONE, GridBagConstraints.PAGE_START, new Insets(0, 8, 0, 0)));
		filter.addActionListener(e -> refreshDocFilter());

		// Setup drop down box of patients.
		patFilter = new JComboBox<>();
		for (String[] patient : main.dbManager.fetchPatients()) {
			patFilter.addItem(patient[1] + " " + patient[2]);
		}
		bookingsPanel.add(new JLabel("Select Patient:"), Main.generateConstraints(1, 0, 1, 1, 1, 0,
				GridBagConstraints.NONE, GridBagConstraints.PAGE_START, new Insets(0, 8, 8, 8)));
		bookingsPanel.add(patFilter, Main.generateConstraints(1, 1, 1, 1, 1, 0,
				GridBagConstraints.NONE, GridBagConstraints.PAGE_START, new Insets(8, 8, 8, 0)));

		// Filter button for patients drop down box.
		filter = new JButton("Filter");
		bookingsPanel.add(filter, Main.generateConstraints(1, 2, 1, 1, 1, 0,
				GridBagConstraints.NONE, GridBagConstraints.PAGE_START, new Insets(0, 8, 8, 0)));
		filter.addActionListener(e -> refreshPatFilter());

		// Setup JSpinner box for month and year.
		dateFilter = new JSpinner(new SpinnerDateModel());
		dateFilter.setEditor(new JSpinner.DateEditor(dateFilter, "MM/yyyy"));
		bookingsPanel.add(new JLabel("Select Month and Year:"), Main.generateConstraints(2, 0, 1, 1, 1, 0,
				GridBagConstraints.NONE, GridBagConstraints.PAGE_START, new Insets(0, 8, 8, 8)));
		bookingsPanel.add(dateFilter, Main.generateConstraints(2, 1, 1, 1, 1, 0,
				GridBagConstraints.NONE, GridBagConstraints.PAGE_START, new Insets(8, 8, 8, 0)));

		// Filter button for monthYear
		filter = new JButton("Filter");
		bookingsPanel.add(filter, Main.generateConstraints(2, 2, 1, 1, 1, 0,
				GridBagConstraints.NONE, GridBagConstraints.PAGE_START, new Insets(0, 8, 8, 0)));
		filter.addActionListener(e -> refreshDateFilter());
	}

	/**
	 * Adds the booking buttons to the actions panel.
	 * @param actionsPanel The actions panel.
	 * @param rowTable Row part of the table.
	 */
	private void createBookingButtons(JPanel actionsPanel, JTable rowTable)
	{
		// Button for adding new booking
		addBookingButton = new JButton("Add new booking");
		addBookingButton.addActionListener(e -> {
			Triplet<String, String, List<String[]>> values = addButtonListenerContent(rowTable);
			addBookingPopup(values.getValue0().split(" ")[0], values.getValue1(), values.getValue2());
		});
		// Button for removing booking
		removeBookingButton = new JButton("Remove booking");
		removeBookingButton.addActionListener(e -> {
			Triplet<String, String, List<String[]>> values = addButtonListenerContent(rowTable);
			removeBookingPopup(values.getValue2().get(bookingSlotIndex));
		});
		rescheduleLabel = new JLabel("Please select the new slot for the booking");
		// Button for rescheduling booking
		updateBookingButton = new JButton("Reschedule booking");
		updateBookingButton.addActionListener(e -> {
			swapMouseListeners(normalML);
			Triplet<String, String, List<String[]>> values = addButtonListenerContent(rowTable);
			rescheduleDetails = values.getValue2().get(bookingSlotIndex);
			rescheduleLabel.setVisible(true);
		});
		addBookingButton.setVisible(false);
		removeBookingButton.setVisible(false);
		updateBookingButton.setVisible(false);
		rescheduleLabel.setVisible(false);
		actionsPanel.add(addBookingButton, Main.generateConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, new Insets(4, 4, 8, 8)));
		actionsPanel.add(removeBookingButton, Main.generateConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, new Insets(4, 4, 8, 8)));
		actionsPanel.add(updateBookingButton, Main.generateConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, new Insets(4, 4, 8, 8)));
		actionsPanel.add(rescheduleLabel, Main.generateConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, new Insets(4, 4, 8, 8)));
	}

	/**
	 * Adds the results of the sql query to the JTable GUI.
	 * @param results Results of the sql query.
	 */
	private void refreshBookings(ArrayList<String[]> results)
	{
		resetTable();
		resetBookings();
		for (String[] result : results) {
			String date = result[4];
			String time = result[5].substring(0, result[5].length() - 3);
			// Put booking at the appropriate time and date.
			for (int row = 0; row < rowTableDates.size(); row++) {
				if (date.equals(rowTableDates.get(row).substring(0, 10))) {
					for (int col = 0; col < TIMES.length; col++) {
						if (time.equals(TIMES[col])) {
							addBookingToMap(row, col, result);
							model.setValueAt("BOOKED", row, col);
							break;
						}
					}
					break;
				}
			}
		}
	}

	/**
	 * Adds the new booking to the HashMap.
	 * @param row Row index of the booking
	 * @param col Column index of the booking.
	 * @param result The actual booking details.
	 */
	private void addBookingToMap(int row, int col, String[] result)
	{
		Pair<Integer, Integer> pair = Pair.with(row, col);
		if (!bookingDetails.containsKey(pair)) {
			List<String[]> list = new LinkedList<>();
			list.add(result);
			bookingDetails.put(pair, list);
		}
		else bookingDetails.get(pair).add(result);
	}

	/**
	 * Resets the table GUI by removing all the bookings.
	 */
	private void resetTable()
	{
		model.setRowCount(0);
		model.setRowCount(numDaysInMonth);
	}

	/**
	 * Resets the values in the bookings field.
	 */
	private void resetBookings()
	{
		bookingDetails.clear();
	}

	/**
	 * Writes the details of the selected booking (cell) on the details panel.
	 * @param row Row number of the cell
	 * @param col Columns number of the cell.
	 */
	private void refreshDetails(int row, int col)
	{
		// Fetch booking details.
		LinkedList<String[]> booking = (LinkedList<String[]>) bookingDetails.get(Pair.with(row, col));

		// Check whether booking slot has no bookings, 1 booking or more than 1 booking.
		if (booking != null && booking.size() > 1) {
			// Remove buttons after another booking is clicked
			removeButtons();
			bookingSlotIndex = 0;
			String[] first = booking.getFirst();
			displayDetails(first, bookingSlotIndex + 1, booking.size());

			// If there is more than one appointment in the time slot, create buttons to navigate
			if (booking.size() > 1) {
				addNextPrevButtons(booking);
			}
		}
		else if (booking != null && booking.size() == 1) {
			bookingSlotIndex = 0;
			displayDetails(booking.getFirst(), 1, 1);
			removeButtons();
		}
		else {
			if (!detailsLabel.isVisible()) {
				// Refresh text pane
				textPane.setContentType("text/plain");
				textPane.setContentType("text/html");
				removeButtons();
				detailsLabel.setVisible(true);
			}
		}
	}

	/**
	 * Creates and adds the next and previous buttons to the details panel.
	 * @param booking The list of bookings in the slot.
	 */
	private void addNextPrevButtons(List<String[]> booking)
	{
		JButton next = new JButton("❯");
		JButton prev = new JButton("❮");
		prev.setVisible(false);
		next.addActionListener(e -> {
			bookingSlotIndex++;
			String[] book = booking.get(bookingSlotIndex);
			displayDetails(book, bookingSlotIndex + 1, booking.size());
			// If the booking is the last element of the list
			if (bookingSlotIndex == booking.size() - 1)
				next.setVisible(false);
			prev.setVisible(true);
		});
		prev.addActionListener(e -> {
			bookingSlotIndex--;
			String[] book = booking.get(bookingSlotIndex);
			displayDetails(book, bookingSlotIndex + 1, booking.size());
			// If the booking is the first element of the list
			if (bookingSlotIndex == 0)
				prev.setVisible(false);
			next.setVisible(true);
		});
		detailsPanel.add(prev, Main.generateConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST, new Insets(4, 4, 8, 8)));
		detailsPanel.add(next, Main.generateConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.EAST, new Insets(4, 4, 8, 8)));
	}

	/**
	 * Removes all the buttons in the details panel
	 */
	private void removeButtons()
	{
		while (detailsPanel.getComponentCount() > 2) detailsPanel.remove(2);
	}

	/**
	 * Displays the details of the booking in the details panel.
	 * @param booking Details of the booking.
	 * @param count The index of the booking in the slot.
	 * @param size How many bookings are in the slot.
	 */
	private void displayDetails(String[] booking, int count, int size)
	{
		// Refresh text pane
		textPane.setContentType("text/plain");
		textPane.setContentType("text/html");
		String details = "<style> body { font-family: 'Courier New'; background-color: #EEEEEE; } " +
				"div { border-top: 1px dashed #A6A6A6; margin-top: 14px; } i { color: #696969; } </style>" +
				"<i>" + count +
				"/" + size +
				"</i><br/>" +
				"Doctor: <b>" + booking[0] + "." + booking[1] + "</b><br/>" +
				"Patient: <b>" + booking[2] + " " + booking[3] + "</b><br/>" +
				"<div></div>" +
				"Date: " + booking[4] + "<br/>" +
				"Start Time: " + booking[5] + "<br/>";
		textPane.setText(details);
		// Show no booking selected notice if required
		detailsLabel.setVisible(false);

		// Log system access
		main.dbManager.logAccess(12, Integer.parseInt(booking[6]), Integer.parseInt(booking[7]), "Booking at " + booking[5].substring(0, 5) + " on " + booking[4].replace("-", "/"));
	}

	/**
	 * Filters the bookings of the table based on the selected doctor.
	 */
	private void refreshDocFilter()
	{
		// Gets the doctor from the combo box and splits it into initial and surname.
		String[] docDetails = ((String) Objects.requireNonNull(docFilter.getSelectedItem())).split("\\.");

		// Fetch results from the query.
		ArrayList<String[]> results = main.dbManager.fetchBookings(
				"WHERE initial='" + docDetails[0] + "' " +
						"AND d.surname='" + docDetails[1] + "' ");
		refreshBookings(results);
	}

	/**
	 * Filters the bookings of the table based on the selected patient.
	 */
	private void refreshPatFilter()
	{
		// Gets the patient from the combo box and splits it into forename and surname.
		String[] patDetails = ((String) Objects.requireNonNull(patFilter.getSelectedItem())).split(" ");

		// Fetch results from the query.
		ArrayList<String[]> results = main.dbManager.fetchBookings(
				"WHERE forename='" + patDetails[0] + "' " +
						"AND p.surname='" + patDetails[1] + "' ");

		refreshBookings(results);
	}

	/**
	 * Sets up the initial dates of each row.
	 * Initial month and year is 2021-03.
	 */
	private void setInitialDates()
	{
		ArrayList<String[]> results = main.dbManager.fetchBookings(
				"WHERE date LIKE '2021-03-%' ");
		changeDates(true, "2021", "03");
		refreshBookings(results);
	}

	/**
	 * Filters the bookings of the table based on the selected month and year.
	 */
	private void refreshDateFilter()
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
		Date date = (Date) dateFilter.getValue();
		String strDate = simpleDateFormat.format(date);

		ArrayList<String[]> results = main.dbManager.fetchBookings("WHERE date LIKE '" + strDate + "-%' ");

		String[] splitDate = strDate.split("-");
		changeDates(false, splitDate[0], splitDate[1]);
		refreshBookings(results);
	}

	/**
	 * Changes the dates of each row according to the selected month and year.
	 * @param isInitial Checks whether these are the starting initial dates.
	 * @param month Selected month.
	 * @param year  Selected year.
	 */
	private void changeDates(boolean isInitial, String year, String month)
	{
		if (isInitial) rowTableDates = new ArrayList<>();
		else rowTableDates.clear();

		Calendar calendar = Calendar.getInstance();

		// Set the date of the calender
		try {
			calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(year + "-" + month + "-01"));
		}
		catch (ParseException e) {
			e.printStackTrace();
		}

		// Amount of days in the month
		numDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		// Add all the days of the given month to the table
		int dayNameIndex = calendar.get(Calendar.DAY_OF_WEEK);
		for (int dayNum = 0; dayNum < numDaysInMonth; dayNum++) {
			StringBuilder sb = new StringBuilder(year + "-" + month + "-" + (dayNum + 1) + " (" + DAYS[(dayNameIndex - 1) % 7] + ")");
			if ((dayNum + 1) < 10) rowTableDates.add(sb.insert(8, "0").toString());
			else rowTableDates.add(sb.toString());
			dayNameIndex++;
		}
	}

	/**
	 * Popup window with form to enter details of new booking.
	 * @param date The date of slot that was clicked.
	 * @param time The time of slot that was clicked.
	 * @param bookedNames List of bookings in the slot.
	 */
	private void addBookingPopup(String date, String time, List<String[]> bookedNames)
	{
		ArrayList<String[]> patients = main.dbManager.fetchPatients();

		// User input fields. Date and time is determined by the program
		JComboBox<String> patField = new JComboBox<>();
		for (String[] patient : patients) {
			patField.addItem(patient[1] + " " + patient[2]);
		}
		JTextField dateField = new JTextField(date);
		dateField.setEditable(false);
		JTextField timeField = new JTextField(time);
		timeField.setEditable(false);

		// Panel to hold swing text inputs
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(new JLabel("Patient"));
		panel.add(patField);
		panel.add(new JLabel("Date"));
		panel.add(dateField);
		panel.add(new JLabel("Time"));
		panel.add(timeField);

		// Ensure user wants to actually add the booking
		int result = JOptionPane.showConfirmDialog(null, panel, "Add a new booking",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			if (bookedNames != null) {
				// Check whether patient has already been booked.
				for (String[] details : bookedNames) {
					String fullNameBooking = details[2] + " " + details[3];
					if (fullNameBooking.equals(patField.getSelectedItem())) {
						// Create error message telling user it is not allowed to add a booking if a patient already has one
						JOptionPane.showMessageDialog(
								null,
								"Patient already has booking in this slot.\n" +
										"Please pick another booking slot.",
								"Error",
								JOptionPane.WARNING_MESSAGE
						);
						return;
					}
				}
			}

			// Add the booking and send confirmation messages
			String[] name = ((String) Objects.requireNonNull(patField.getSelectedItem())).split(" ");
			// Call db mtd to add the doctor with values extracted from the text inputs
			String[] ids = main.dbManager.addBooking(name[0], name[1], date, time);
			// Log system access
			main.dbManager.logAccess(13, Integer.parseInt(ids[0]), Integer.parseInt(ids[1]), "Booking at " + time.substring(0, 5) + " on " + date.replace("-", "/"));

			String[] pat = patients.get(patField.getSelectedIndex());
			String[] usernames = main.dbManager.fetchUsernames(pat[1], pat[2]);
			// Confirmation message for patient
			sendConfirmationMsg(
					"Notice of new booking",
					"You have been scheduled for a new meeting with your doctor on " +
					date + " at " + time + ".",
					usernames[0]
			);
			// Confirmation message for doctor
			sendConfirmationMsg(
					"Notice of new booking",
					"You have been scheduled for a new meeting with your patient " +
					patField.getSelectedItem() + " on " + date + " at " + time + ".",
					usernames[1]
			);

			// Reload the table by fetching the bookings and adding the bookings
			ArrayList<String[]> results = main.dbManager.fetchBookings("");
			refreshBookings(results);
		}
	}

	/**
	 * Popup window with form to enter details of new booking.
	 * @param selected Details of selected booking.
	 */
	private void removeBookingPopup(String[] selected)
	{
		// Ensure user wants to actually remove the booking
		int result = JOptionPane.showConfirmDialog(
				null,
				"Are you sure you want to remove the booking \n" +
				 "(" + selected[0] + "." + selected[1] + "," + selected[2] + " " + selected[3] + "," +
				selected[4] + "," + selected[5] + ") ?",
				"Remove booking",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE
		);

		if (result == JOptionPane.YES_OPTION) {
			String date = selected[4];
			String time = selected[5];

			// Remove booking from the database
			String[] ids = main.dbManager.removeBooking(selected[2], selected[3], date, time);
			// Log system access
			main.dbManager.logAccess(14, Integer.parseInt(ids[0]), Integer.parseInt(ids[1]), "Booking at " + time.substring(0, 5) + " on " + date.replace("-", "/"));

			String[] usernames = main.dbManager.fetchUsernames(selected[2], selected[3]);
			// Confirmation message for patient
			sendConfirmationMsg(
					"Notice of cancelled booking",
					"Your booking with your doctor on " +
							date + " at " + time + " has been cancelled.",
					usernames[0]
			);
			// Confirmation message for doctor
			sendConfirmationMsg(
					"Notice of cancelled booking",
					"Your booking with your patient " +
							selected[2] + " " + selected[3] + " on " + date + " at " + time +
							" has been cancelled.",
					usernames[1]
			);

			// Reload the table by fetching the bookings and adding the bookings
			ArrayList<String[]> results = main.dbManager.fetchBookings("");
			refreshBookings(results);
		}
	}


	/**
	 * Reschedules a booking to another slot depending on user input.
	 * @param newDate Date of new slot.
	 * @param newTime Time of new slot.
	 * @param selected Booking to be rescheduled.
	 */
	private void rescheduleBookingPopup(String newDate, String newTime, String[] selected)
	{
		String fullName = selected[2] + " " + selected[3];
		String currentDate = selected[4];
		String currentTime = selected[5];

		// Ensure user wants to actually reschedule the booking
		int result = JOptionPane.showConfirmDialog(
				null,
				"Are you sure you want to reschedule this booking?",
				"Reschedule booking",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE
		);

		// Check whether user is trying to reschedule booking to weekend
		try {
			SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			c.setTime(formatDate.parse(newDate));
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
			// If the current day of the week is either sunday or saturday
			if (dayOfWeek == 0 || dayOfWeek == 6) {
				JOptionPane.showMessageDialog(
						null,
						"You cannot reschedule a booking to the weekend.\nPlease try again.",
						"Warning",
						JOptionPane.WARNING_MESSAGE
				);
				return;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		if (result == JOptionPane.YES_OPTION) {
			int col = getColumnFromTime(newTime);
			int row = getRowFromDate(newDate);
			List<String[]> newBookings = bookingDetails.get(Pair.with(row, col));
			if (newBookings != null) {
				// Check whether patient has already been booked in new slot.
				for (String[] details : newBookings) {
					String fullNameBooking = details[2] + " " + details[3];
					if (fullNameBooking.equals(fullName)) {
						// Create error message telling user it is not allowed to reschedule booking if a patient already has one in that slot
						JOptionPane.showMessageDialog(
								null,
								"Patient already has booking in this slot.\n" +
										"Please pick another booking slot.",
								"Error",
								JOptionPane.WARNING_MESSAGE
						);
						return;
					}
				}
			}

			// Get the name of the patient split into their forename and surname
			// Call db mtd to add the doctor with values extracted from the text inputs
			String[] name = fullName.split(" ");
			String[] ids = main.dbManager.updateBooking(name[0], name[1], currentDate, currentTime, newDate, newTime + ":00");
			// Log system access
			main.dbManager.logAccess(15, Integer.parseInt(ids[0]), Integer.parseInt(ids[1]), "Booking was at " + currentTime.substring(0, 5) + " on " + newDate.replace("-", "/") + ", is now at at " + newTime.substring(0, 5) + " on " + newDate.replace("-", "/"));

			String[] usernames = main.dbManager.fetchUsernames(name[0], name[1]);
			// Confirmation message for patient
			sendConfirmationMsg(
					"Notice of rescheduled booking",
					"Your meeting with your doctor on " +
							currentDate + " at " + currentTime + " has been rescheduled to " +
							newDate + " at " + newTime + ".",
					usernames[0]
			);
			// Confirmation message for doctor
			sendConfirmationMsg(
					"Notice of rescheduled booking",
					"Your meeting with your patient " +
							name[0] + " " + name[1] + " on " + currentDate + " at " + currentTime +
							" has been rescheduled to " + newDate + " at " + newTime + ".",
					usernames[1]
			);

			// Reload the table by fetching the bookings and adding the bookings
			ArrayList<String[]> results = main.dbManager.fetchBookings("");
			refreshBookings(results);
		}
		// Put table listener back to normal with refreshing details
		swapMouseListeners(rescheduleML);
		rescheduleLabel.setVisible(false);
	}

	/**
	 * Send a confirmation message to a user
	 * @param msgHead  The header of the message
	 * @param msgBody  The body of the message to send
	 * @param username The user to send the message to
	 */
	private void sendConfirmationMsg(String msgHead, String msgBody, String username)
	{
		// Get current date and time
		long millis = System.currentTimeMillis();
		String time = new Time(millis).toString();
		String date = new java.sql.Date(millis).toString();
		// Call db mtd to send messages
		main.dbManager.sendMessage(time, date, msgHead, msgBody, username);
	}

	/**
	 * Get the column number of the given time.
	 * @param time String in the time format "hh:mm:ss"
	 * @return The column number of the time.
	 */
	private int getColumnFromTime(String time)
	{
		String[] t = time.split(":");
		float hour = Float.parseFloat(t[0]);
		float min = t[1].equals("30") ? 0.5f : 0.0f;
		float num = hour + min;
		return (int) ((2 * num) - 17 - 1);
	}

	/**
	 * Get the row number of the given date in the table.
	 * @param date String in the format "yyyy-mm-dd"
	 * @return The row number.
	 */
	private int getRowFromDate(String date)
	{
		return Integer.parseInt(date.substring(8,10)) - 1;
	}

	/**
	 * Instantiates the two mouse listeners for the bookings table.
	 * @param label JLabel.
	 * @param rowTable Row part of the bookings table.
	 */
	private void createMouseListeners(JLabel label, JTable rowTable)
	{
		// Used for normal operations like finding the details of a booking.
		normalML = new MouseAdapter() {
			public void mousePressed(MouseEvent arg0) {
				if (arg0.getButton() == MouseEvent.BUTTON1) {
					int row = bookingsTable.getSelectedRow();
					int col = bookingsTable.getSelectedColumn();
					refreshDetails(row, col);
					label.setVisible(false);

					String rowVal = ((String) rowTable.getValueAt(row, 0)).split(" ")[1];
					String booked = (String) model.getValueAt(row, col);
					boolean show = !rowVal.equals("(Sun)") && !rowVal.equals("(Sat)");
					addBookingButton.setVisible(show);
					removeBookingButton.setVisible(show && booked != null);
					updateBookingButton.setVisible(show && booked != null);
				}
			}
		};

		// Used to reschedule a booking by clicking a booking slot
		rescheduleML = new MouseAdapter() {
			public void mousePressed(MouseEvent arg0) {
				if (arg0.getButton() == MouseEvent.BUTTON1) {
					Triplet<String, String, List<String[]>> values = addButtonListenerContent(rowTable);
					rescheduleBookingPopup(values.getValue0().split(" ")[0], values.getValue1(), rescheduleDetails);
				}
			}
		};
	}

	/**
	 * Swaps the mouse listener of the bookings table.
	 * @param mouseListener Currently used mouse listener.
	 */
	private void swapMouseListeners(MouseListener mouseListener)
	{
		if (mouseListener == rescheduleML) {
			bookingsTable.removeMouseListener(rescheduleML);
			bookingsTable.addMouseListener(normalML);
		}
		else {
			bookingsTable.removeMouseListener(normalML);
			bookingsTable.addMouseListener(rescheduleML);
		}
	}

	/**
	 * Gets the values needed for the three add, remove and reschedule buttons.
	 * @param rowTable Row part of the table.
	 * @return Triplet tuples with values time, date and list of bookings in slot.
	 */
	private Triplet<String, String, List<String[]>> addButtonListenerContent(JTable rowTable)
	{
		int row = bookingsTable.getSelectedRow();
		int col = bookingsTable.getSelectedColumn();
		String time = model.getColumnName(col);
		String date = (String) rowTable.getValueAt(row, 0);
		return Triplet.with(date, time, bookingDetails.get(Pair.with(row, col)));
	}
}