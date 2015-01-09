package de.util;

import javafx.application.Platform;
import de.notez.prop.NotezFinalProperties;
import de.notez.prop.NotezProperties;
import de.util.pref.NotezPreferences;

public class NotezSystemUtil
{
	/** int symbolizing a a normal ending */
	public static final int NORMAL = 0;
	/** int symbolizing a a unnormal ending */
	public static final int FATAL = 1;

	private static NotezProperties systemProperties = new NotezFinalProperties();

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

	public static boolean isRunningInSceneBuilder()
	{
		return System.getProperty("app.preferences.id", "").contains(
						"scenebuilder");
	}

	public static NotezProperties getSystemProperties()
	{
		return systemProperties;
	}

	public static void setSystemProperties(NotezProperties systemProperties)
	{
		NotezSystemUtil.systemProperties = systemProperties;
	}
}