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
			// FORTEST when running executable jar
			NotezLoggerUtil.logSystemOut(LOGGING_FOLDER, DAYS_TO_SAVE_LOGS, REORG_LOGS);
		}
		catch(IOException e)
		{
			System.err.println("could not initialize logging to basic outputstreams.");
			e.printStackTrace();
			System.exit(1);
		}

		if (!DEBUG)
		{
			if (NotezPreferences.isNotezAlreadyRunning())
			{
				System.err.println("ERROR: Notez already running");
				System.exit(1);
			}

			NotezPreferences.setNotezRunning(true);

			Runtime.getRuntime().addShutdownHook(
				new Thread(() -> NotezPreferences.setNotezRunning(false)));

		}

		Application.launch(NotezLoadSplash.class, args);
		// Application.launch(NotezFrame.class, args);
	}
}