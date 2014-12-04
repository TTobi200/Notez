/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class NotezProperties
{
    public static final String COMMENT =
                    NotezProperties.class.getName();

    public static final String PROP_FILE = "./Notez.properties";

    public static final String PROP = "Prop";
    public static final String PROP_NOTEZ_WORK_FOLDER =
                    PROP + ".NotezWorkFolder";
    public static final String PROP_NOTEZ_REMOTE_FOLDER =
                    PROP + ".NotezRemoteFolder";
    public static final String PROP_START_RECEIVER =
                    PROP + ".StartReceiver";
    public static final String PROP_LET_RECEIVER_RUNNING =
                    PROP + ".LetReceiverRunning";
    public static final String PROP_RECEIVE_OPEN_DIRECT =
                    PROP + ".OpenReceivedNotezDirectly";
    public static final String PROP_RECEIVE_SHOW_MESSAGE =
                    PROP + ".ShowReceivedMessage";
    public static final String PROP_ALWAYS_SAVE_ON_EXIT =
                    PROP + ".AlwaysSaveOnExit";

    public static final String PROP_BTN_PIN_VISIBLE =
                    PROP + ".BtnPinVisible";
    public static final String PROP_BTN_GROUP_VISIBLE =
                    PROP + ".BtnGroupVisible";
    public static final String PROP_BTN_SHARE_VISIBLE =
                    PROP + ".BtnShareVisible";
    public static final String PROP_BTN_ADD_VISIBLE =
                    PROP + ".BtnAddVisible";
    public static final String PROP_BTN_SAVE_VISIBLE =
                    PROP + ".BtnSaveVisible";
    public static final String PROP_BTN_REMOVE_VISIBLE =
                    PROP + ".BtnRemoveVisible";
    public static final String PROP_BTN_PRINT_VISIBLE =
                    PROP + ".BtnPrintVisible";

    private static File propFile;
    private static Properties properties;

    static
    {
        properties = new Properties();
        propFile = new File(PROP_FILE);

        if(NotezFileUtil.fileCanBeLoad(propFile))
        {
            try
            {
                properties.load(new FileReader(propFile));
            }
            catch(FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static boolean contains(String key)
    {
        return properties.containsKey(key);
    }

    public static String get(String key)
    {
        return String.valueOf(properties.get(key));
    }

    public static String get(String key, String defaultValue)
    {
        return properties.getProperty(key, defaultValue);
    }

    public static void set(String key, String value)
    {
        if(value == null)
        {
            value = properties.getProperty(key);
        }
        properties.setProperty(key, value);
    }

    public static Properties getAll()
    {
        return properties;
    }

    public static void save()
    {
        try
        {
            if(!propFile.exists())
            {
                propFile.createNewFile();
            }

            if(NotezFileUtil.fileCanBeSaved(propFile))
            {
                properties.store(new FileWriter(propFile),
                    COMMENT);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}