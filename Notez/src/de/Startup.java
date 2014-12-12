/*
 * $Header$
 *
 * $Log$ Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de;

import java.io.IOException;

import javafx.application.Application;
import de.gui.NotezLoadSplash;
import de.util.NotezLoggerUtil;
import de.util.NotezSystemUtil;
import de.util.pref.NotezPreferences;

public class Startup
{
	public static final boolean DEBUG = true;

	public static final boolean REORG_LOGS = true;
	public static final int DAYS_TO_SAVE_LOGS = 7;
	public static final String LOGGING_FOLDER = "./logging";

	public static void main(String[] args)
	{
		try
		{
			NotezLoggerUtil.logSystemOut(LOGGING_FOLDER,
				DAYS_TO_SAVE_LOGS, REORG_LOGS);
		}
		catch(IOException e)
		{
			System.err.println("Could not initialize logging to basic outputstreams.");
			e.printStackTrace();
			NotezSystemUtil.exit(NotezSystemUtil.FATAL);
		}

		if (!DEBUG)
		{
			if (NotezPreferences.isNotezAlreadyRunning())
			{
				System.err.println("ERROR: Notez already running");
				NotezSystemUtil.exit(NotezSystemUtil.FATAL);
			}

			NotezPreferences.setNotezRunning(true);

			Runtime.getRuntime().addShutdownHook(
				new Thread(() -> NotezPreferences.setNotezRunning(false)));

		}

		Application.launch(NotezLoadSplash.class, args);
		// Application.launch(NotezFrame.class, args);
	}
}