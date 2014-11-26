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
    public static final String PROP_NOTEZ_FOLDER =
                    PROP + ".NotezFolder";

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
        if(NotezFileUtil.fileCanBeSaved(propFile))
        {
            try
            {
                properties.store(new FileWriter(propFile),
                    COMMENT);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}