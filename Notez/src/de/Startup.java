/*
 * $Header$
 * 
 * $Log$ Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de;

import static de.notez.prop.NotezProperties.NOTEZ_REMOTE_FOLDER;

import java.io.*;
import java.net.BindException;

import javafx.application.Application;
import de.gui.NotezLoadSplash;
import de.notez.*;
import de.notez.network.NotezServer;
import de.util.*;
import de.util.log.NotezLog;
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
			NotezLoggerUtil.initLogging(LOGGING_FOLDER, DAYS_TO_SAVE_LOGS, REORG_LOGS);
		}
		catch(IOException e)
		{
			System.err.println("Could not initialize logging to basic outputstreams.");
			e.printStackTrace();
			NotezSystem.exit(NotezSystem.FATAL);
		}

		if(!DEBUG)
		{
			if(NotezPreferences.isNotezAlreadyRunning())
			{
				System.err.println("ERROR: Notez already running");
				NotezSystem.exit(NotezSystem.FATAL);
			}

			NotezPreferences.setNotezRunning(true);
		}

		try
		{
			NotezServer.initialize();
		}
		catch(BindException e)
		{
			NotezLog.fatal("The socket for notez is already in use, may notez is already running?"
						   + System.lineSeparator() + "Shutting down notez", e);
			NotezSystem.exit(NotezSystem.SERVER_ERROR);
		}
		catch(IOException e)
		{
			NotezLog.error("error while initializing the server", e);
		}

		NotezPlatformUtil.initialize();

		NotezRemoteSync.initialize(new File(NotezSystem.getSystemProperties().getString(
			NOTEZ_REMOTE_FOLDER)));

		Application.launch(NotezLoadSplash.class, args);
	}
}