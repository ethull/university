package com.sd.group4f;

import java.awt.*;

import javax.swing.*;

/**
 * Empty base for all tabs of the system.
 * @author jjrf2
 * @version 2021.02.24
 */
public class Tab extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	// Tab name to display in navigation bar
	public String tabName = "";
	
	/**
	 * Class constructor.
	 */
	public Tab()
	{
		super(new GridBagLayout());
	}
	
	/**
	 * Empty method.
	 */
	public void refresh()
	{
		
	}
}