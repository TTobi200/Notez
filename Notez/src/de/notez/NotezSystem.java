package de.notez;

import javafx.application.Platform;
import de.notez.prop.NotezSystemProperties;
import de.util.pref.NotezPreferences;

public class NotezSystem
{
	/** int symbolizing a a normal ending */
	public static final int NORMAL = 0x0;
	/** int symbolizing a a unnormal ending */
	public static final int FATAL = 0x1;
	public static final int SERVER_ERROR = 0x10;

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
	 * @param status
	 *            the return value
	 */
	public static void exit(int status)
	{
		try
		{
			NotezRemoteSync.stopAll();
			Platform.exit();
			getSystemProperties().save();
			NotezPreferences.setNotezRunning(false);
		}
		catch(Throwable t)
		{
			// error while exiting, change the status to notify the user
			if(status == NORMAL)
			{
				status = FATAL;
			}
		}

		if(status != NORMAL)
		{
			System.exit(status);
		}
	}

	public static boolean isRunningInSceneBuilder()
	{
		return System.getProperty("app.preferences.id", "").contains("scenebuilder");
	}

	public static NotezSystemProperties getSystemProperties()
	{
		return systemProperties;
	}
}