package de.util;

import javafx.application.Platform;
import de.notez.NotezRemoteSync;
import de.notez.prop.NotezSystemProperties;
import de.util.pref.NotezPreferences;

public class NotezSystemUtil
{
	/** int symbolizing a a normal ending */
	public static final int NORMAL = 0;
	/** int symbolizing a a unnormal ending */
	public static final int FATAL = 1;
	
	private static NotezSystemProperties systemProperties = NotezSystemProperties.getSystemProperties();

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
		NotezRemoteSync.stopAll();
		Platform.exit();
		NotezPreferences.setNotezRunning(false);
		
		if(status != NORMAL)
		{
			System.exit(status);
		}
	}

	public static boolean isRunningInSceneBuilder()
	{
		return System.getProperty("app.preferences.id", "").contains(
						"scenebuilder");
	}

	public static NotezSystemProperties getSystemProperties()
	{
		return systemProperties;
	}
}