package de.util;

import javafx.application.Platform;
import de.util.pref.NotezPreferences;

public class NotezSystemUtil
{
	/** int symbolizing a a normal ending */
	public static final int NORMAL = 0;
	/** int symbolizing a a unnormal ending */
	public static final int FATAL = 1;

	/**
	 * Normal ending of this application
	 */
	public static void exit()
	{
		exit(NORMAL);
	}
	
	/**
	 * Ending of this application with the given status
	 * 
	 * @param status the return value
	 */
	public static void exit(int status)
	{
		NotezPreferences.setNotezRunning(false);
		Platform.exit();
		System.exit(status);
	}
}