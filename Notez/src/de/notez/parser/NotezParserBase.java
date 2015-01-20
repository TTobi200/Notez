/*
 * $Header$
 *
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.notez.parser;

import java.io.*;

import de.notez.data.NotezData;

public abstract class NotezParserBase implements NotezParser
{
    protected File file;

    protected NotezParserBase()
    {
        file = null;
    }

    @Override
    public NotezData parse(File file) throws IOException
    {
        NotezData data = parseImpl(file);

        this.file = file;

        return data;
    }

    protected abstract NotezData parseImpl(File file) throws IOException;

    public File getFile()
    {
        return file;
    }
}