/*
 * $Header$
 *
 * $Log$
 * Copyright � 2014 T.Ohm . All Rights Reserved.
 */
package de.notez.parser;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import de.notez.NotezNote;
import de.notez.data.NotezData;

public class NotezParsers
{
    private static Map<String, NotezParser> mapParser;
    private static final NotezParser latestParser;

    static
    {
        mapParser = new HashMap<>();
        mapParser.put("0.1", new NotezParserV01());
        mapParser.put(NotezParserV02.VERSION, new NotezParserV02());
        mapParser.put(BaseNotezXmlParser.VERSION, new BaseNotezXmlParser());
        mapParser.put("0", mapParser.get("0.1"));

        LinkedList<String> d = new LinkedList<>(mapParser.keySet());
        d.sort(null);

        latestParser = mapParser.get(d.getLast());
    }

    public static NotezData parseFile(String path)
        throws UnsupportedVersionException, IOException
    {
        return parseFile(new File(path));
    }

    public static NotezData parseFile(File file)
        throws UnsupportedVersionException, IOException
    {
        return getParserForVersion(getVersionOfFile(file)).parse(file);
    }

    public static NotezParser getParserForVersion(String version)
        throws UnsupportedVersionException
    {
        NotezParser ret = mapParser.get(version);

        if(ret == null && version.contains("."))
        {
            ret = getParserForVersion(version.substring(0,
                version.lastIndexOf('.')));
        }

        if(ret == null)
        {
            throw new UnsupportedVersionException(version);
        }

        return ret;
    }

    public static String getVersionOfFile(File file) throws IOException
    {
        try (BufferedReader r = new BufferedReader(new FileReader(file)))
        {
            String line = r.readLine();

			if (line != null)
			{
				if (line.startsWith(NotezParser.STRING_VERSION))
				{
					line = line.substring(NotezParser.STRING_VERSION.length()).trim();
				}
				else if(line.startsWith(NotezXmlParserBase.NOTEZ_XML_FILE_START))
				{
					// TODO could be very inperformant on huge files.
					try
					{
						DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
						DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
						Document doc = dBuilder.parse(file);

						Element notezElement = doc.getDocumentElement();

						if(notezElement.getNodeName().equals(NotezXmlParserBase.NOTEZ_XML_ROOT_ELEMENT))
						{
							line = notezElement.getAttribute(NotezParserBase.STRING_VERSION);
						}
					}
					catch(Exception e)
					{
						line = null;
					}
				}
			}

            return (line == null || line.isEmpty()) ? "0" : line;
        }
    }

    public static void save(NotezNote note, String path)
        throws IOException
    {
        save(note, new File(path));
    }

    public static void save(NotezNote note, File file)
        throws IOException
    {
        save(note, file, null);
    }

    public static void save(NotezNote note, String path,
                    String version) throws IOException
    {
        save(note, new File(path), version);
    }

    public static void save(NotezNote note, File file,
                    String version) throws IOException
    {
        NotezParser parser = mapParser.get(version);
        if(parser == null)
        {
            parser = latestParser;
        }

        parser.save(note, file);
    }
}
