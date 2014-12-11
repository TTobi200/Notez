/*
 * $Header$
 * 
 * $Log$ Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.Preferences;

import javafx.application.Application;
import de.gui.NotezLoadSplash;
import de.gui.controller.NotezController;

public class Startup
{
    public static final boolean DEBUG = true;

    public static final Preferences NOTEZ_PREFERENCES = Preferences
        .userNodeForPackage(NotezController.class);
    public static final String PREFERENCES_NODE_PREFIX = "Notez";
    public static final String PREFERENCE_NODE_RUNNING = PREFERENCES_NODE_PREFIX
                                                         + ".Running";

    public static final boolean REORG_LOGS = true;
    public static final int DAYS_TO_SAVE_LOGS = 7;
    public static final String ERR_FILE_EXT = ".error";
    public static final String DEBUG_FILE_EXT = ".debug";
    public static final String LOGGING_FOLDER = "./logging";

    public static void main(String[] args) throws IOException,
        InterruptedException
    {
        if(!DEBUG)
        {
            if(checkNotezAlreadyRunning())
            {
                System.err.println("ERROR: Notez already running");
                System.exit(1);
            }

            setNotezRunning(true);

            Runtime.getRuntime().addShutdownHook(
                new Thread(() -> setNotezRunning(false)));

        }
        // FORTEST when running executable jar
        // logSystemOut(LOGGING_FOLDER, DAYS_TO_SAVE_LOGS, REORG_LOGS);

        Application.launch(NotezLoadSplash.class, args);
        // Application.launch(NotezFrame.class, args);
    }

    public static void logSystemOut(String folder, int daysToLog, boolean reorg)
        throws IOException
    {
        File logFolder = new File(folder);
        logFolder.mkdir();

        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
        Date date = new Date();

        System.setErr(new PrintStream(logFolder.getAbsolutePath()
                                      + File.separator
                                      + dateFormat.format(date) + ERR_FILE_EXT));
        System.setOut(new PrintStream(logFolder.getAbsolutePath()
                                      + File.separator
                                      + dateFormat.format(date)
                                      + DEBUG_FILE_EXT));

        File[] logfiles = logFolder.listFiles();
        if(reorg)
        {
            if(logfiles.length > (daysToLog * 2))
            {
                FileTime oldest = FileTime.fromMillis(System.currentTimeMillis());
                String oldestFileName = "";
                for(File log : logfiles)
                {
                    BasicFileAttributes attr = Files.readAttributes(
                        log.toPath(), BasicFileAttributes.class);

                    if(oldest.compareTo(attr.creationTime()) > 0)
                    {
                        oldest = attr.creationTime();
                        oldestFileName = log.getName()
                            .replace(ERR_FILE_EXT, "")
                            .replace(DEBUG_FILE_EXT, "");
                    }
                }

                new File(logFolder.getAbsolutePath() + File.separator
                         + oldestFileName + ERR_FILE_EXT).delete();
                new File(logFolder.getAbsolutePath() + File.separator
                         + oldestFileName + DEBUG_FILE_EXT).delete();
            }
        }
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