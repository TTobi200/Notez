/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.util.pref;

import java.util.prefs.Preferences;

import de.gui.controller.NotezController;

/**
 * Preferences of and from Notez
 *
 * @author ddd
 */
public class NotezPreferences
{
	/** The root preference for Notez */
	public static final Preferences NOTEZ_PREFERENCES = Preferences
			.userNodeForPackage(NotezController.class);
	/** Prefix for all Notez-related Preferences */
	public static final String PREFERENCES_NODE_PREFIX = "Notez";
	/** Name of the preference showing whether notez is currently running */
	public static final String PREFERENCE_NODE_RUNNING = PREFERENCES_NODE_PREFIX + ".Running";

	/**
	 * @return true if notez is currently running in this or another vm
	 */
	public static boolean isNotezAlreadyRunning()
	{
		return NOTEZ_PREFERENCES.getBoolean(PREFERENCE_NODE_RUNNING, false);
	}

	/**
	 * @param running
	 *            Set the value of the preference indicating whether notez is currently running
	 */
	public static void setNotezRunning(boolean running)
	{
		NOTEZ_PREFERENCES.putBoolean(PREFERENCE_NODE_RUNNING, running);
	}
}
