/*
 * $Header$
 *
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.notez.parser;

import java.io.*;

import de.notez.NotezNote;
import de.notez.data.NotezData;

public interface NotezParser
{
    public static final String STRING_VERSION = "Version";

    public default NotezData parse(String path) throws IOException
    {
        return parse(new File(path));
    }

    public NotezData parse(File file) throws IOException;

    public default void save(NotezNote note)
        throws IOException
    {
        save(note, note.getNoteFile());
    }

    public default void save(NotezNote note, String path)
        throws IOException
    {
        save(note, new File(path));
    }

    public void save(NotezNote note, File file)
        throws IOException;
}
