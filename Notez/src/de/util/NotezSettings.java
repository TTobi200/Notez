/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import de.gui.NotezDialog;
import de.gui.NotezFrame;

public class NotezSettings
{
	private static HashMap<String, Setting<Object>> settings = new HashMap<>();

	public static void load(File f) throws IOException, InterruptedException
	{
		if(NotezFileUtil.fileCanBeLoad(f))
		{
			try (BufferedReader in = new BufferedReader(new FileReader(f)))
			{
				String line = "";
				while((line = in.readLine()) != null)
				{
					putSetting(line);
				}
			}
		}
		else
		{
			NotezDialog.showErrorDialog(null, "Error - Settings",
				"Could not load Settings! " + NotezFrame.SETTINGS_FILE);
		}
	}

	public static void store(File f) throws IOException
	{
		if(NotezFileUtil.fileCanBeSaved(f))
		{
			try (FileWriter out = new FileWriter(f))
			{
				for(Setting<Object> s : settings.values())
				{
					out.append(s.toString());
				}
			}
		}
	}

	public static void putSetting(String line)
	{
		String setting = line.split(String.valueOf(Setting.DELIMITER))[0];
		String value = line.split(String.valueOf(Setting.DELIMITER))[1];
		String defaultValue = line.split(String.valueOf(Setting.DELIMITER))[2];

		settings.put(setting, new Setting<Object>(setting, value, defaultValue));
	}

	public static Boolean getBoolean(String setting)
	{
		return getSetting(setting, Boolean.class);
	}

	public static Number getNumber(String setting)
	{
		return getSetting(setting, Number.class);
	}

	public static String getString(String setting)
	{
		return getSetting(setting, String.class);
	}

	public static <T extends Object> T getSetting(String setting, Class<T> type)
	{
		return type.cast(settings.get(setting).getValue());
	}

	public static <T extends Setting<Object>> void setSetting(String setting,
					T value)
	{
		settings.put(setting, value);
	}

	public static class Setting<T>
	{
		public static final char DELIMITER = '\t';

		private String name;
		private T value;
		private T defaultValue;

		public Setting(String name, T value)
		{
			this(name, value, value);
		}

		public Setting(String name, T value, T defaultValue)
		{
			this.name = name;
			this.value = value;
			this.defaultValue = defaultValue;
		}

		@Override
		public String toString()
		{
			return new StringBuilder(name).
				append(DELIMITER).
				append(value).
				append(DELIMITER).
				append(defaultValue).toString();
		}

		public T getValue()
		{
			return value;
		}

		public void setValue(T value)
		{
			this.value = value;
		}

		public T getDefaultValue()
		{
			return defaultValue;
		}

		public void setDefaultValue(T defaultValue)
		{
			this.defaultValue = defaultValue;
		}

		public String getName()
		{
			return name;
		}
	}

	public static HashMap<String, Setting<Object>> getAll()
	{
		return settings;
	}
}