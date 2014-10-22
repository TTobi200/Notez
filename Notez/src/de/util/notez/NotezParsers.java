/*
 * Copyright � 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.util.notez;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.gui.NotezController;

public class NotezParsers
{
	private static Map<String, NotezParser> mapParser;
	private static final NotezParser latestParser;

	static
	{
		mapParser = new HashMap<>();
		mapParser.put("0.1", new NotezParserV01());
		mapParser.put(NotezParserV02.VERSION, new NotezParserV02());
		mapParser.put("0", mapParser.get("0.1"));
		
		LinkedList<String> d = new LinkedList<>(mapParser.keySet());
		d.sort(null);
		
		latestParser = mapParser.get(d.getLast());
	}

	public static NotezData parseFile(String path) throws IOException
	{
		return parseFile(new File(path));
	}

	public static NotezData parseFile(File file) throws IOException
	{
		return getParserForVersion(getVersionOfFile(file)).parse(file);
	}
	
	public static NotezParser getParserForVersion(String version)
	{
		NotezParser ret = mapParser.get(version);
		
		if(ret == null)
		{
			return getParserForVersion(version.substring(0, version.lastIndexOf('.')));
		}
		
		return ret;
	}

	public static String getVersionOfFile(File file) throws IOException
	{
		try (BufferedReader r = new BufferedReader(new FileReader(file)))
		{
			String line = r.readLine();

			if(line == null)
			{
				return "0";
			}
			
			return line;
		}
	}
	
	public static void save(NotezController controller, String path) throws IOException
	{
		save(controller, new File(path));
	}
	
	public static void save(NotezController controller, File file) throws IOException
	{
		save(controller, file, null);
	}
	
	public static void save(NotezController controller, String path, String version) throws IOException
	{
		save(controller, new File(path), version);
	}
	
	public static void save(NotezController controller, File file, String version) throws IOException
	{
		NotezParser parser = mapParser.get(version);
		if(parser == null)
		{
			parser = latestParser;
		}
		
		parser.save(controller, file);
	}
}