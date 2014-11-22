/*
 * $Header$
 *
 * $Log$ Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de;

import java.io.FileNotFoundException;
import java.util.prefs.Preferences;

import javafx.application.Application;
import de.gui.NotezLoadSplash;
import de.gui.controller.NotezController;

public class Startup
{
	public static final boolean		DEBUG					= true;

	public static final Preferences	NOTEZ_PREFERENCES		= Preferences
																	.userNodeForPackage(NotezController.class);
	public static final String		PREFERENCES_NODE_PREFIX	= "Notez";
	public static final String		PREFERENCE_NODE_RUNNING	= PREFERENCES_NODE_PREFIX + ".Running";

	public static void main(String[] args) throws FileNotFoundException
	{
		if (!DEBUG)
		{
			if (checkNotezAlreadyRunning())
			{
				System.err.println("ERROR: Notez already running");
				System.exit(1);
			}

			setNotezRunning(true);

			Runtime.getRuntime().addShutdownHook(new Thread(() -> setNotezRunning(false)));

		}
		// FORTEST when running executable jar
		// System.setErr(new PrintStream("./err.txt"));
		Application.launch(NotezLoadSplash.class, args);
		// Application.launch(NotezFrame.class, args);
	}

	private static boolean checkNotezAlreadyRunning()
	{
		return NOTEZ_PREFERENCES.getBoolean(PREFERENCE_NODE_RUNNING, false);
	}

	private static void setNotezRunning(boolean running)
	{
		NOTEZ_PREFERENCES.putBoolean(PREFERENCE_NODE_RUNNING, running);
	}
}