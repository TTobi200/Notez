package de.util.pref;

import java.util.prefs.Preferences;

import de.gui.controller.NotezController;

public class NotezPreferences
{
	public static final Preferences NOTEZ_PREFERENCES = Preferences
			.userNodeForPackage(NotezController.class);
	public static final String PREFERENCES_NODE_PREFIX = "Notez";
	public static final String PREFERENCE_NODE_RUNNING = PREFERENCES_NODE_PREFIX + ".Running";

    public static boolean isNotezAlreadyRunning()
    {
        return NOTEZ_PREFERENCES.getBoolean(PREFERENCE_NODE_RUNNING, false);
    }

    public static void setNotezRunning(boolean running)
    {
        NOTEZ_PREFERENCES.putBoolean(PREFERENCE_NODE_RUNNING, running);
    }
}
