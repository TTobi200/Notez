/*
 * $Header$
 *
 * $Log$
 * Copyright � 2014 T.Ohm . All Rights Reserved.
 */
package de.notez.parser;

import java.io.*;
import java.util.Arrays;

import de.notez.NotezNote;
import de.notez.data.NotezData;
import de.notez.data.base.*;
import de.util.NotezFileUtil;

public class NotezParserV02 extends NotezParserBase
{
    public static final String VERSION = "0.2";

    @Override
    public void save(NotezNote note, File file)
        throws IOException
    {
        try (Writer w = new BufferedWriter(new FileWriter(file)))
        {
            w.write(new StringBuilder(STRING_VERSION).append(' ')
                .append(VERSION)
                .append(System.lineSeparator())

                .append(note.getGui().getX())
                .append(' ')
                .append(note.getGui().getY())
                .append(' ')
                .append(note.getGui().getWidth())
                .append(' ')
                .append(note.getGui().getHeight())
                .append(System.lineSeparator())

                .append(note.getData().getPageData().getText())
                .toString());
        }
    }

    @Override
    protected NotezData parseImpl(File file) throws IOException
    {
        try (BufferedReader r = new BufferedReader(new FileReader(file)))
        {
            String version = r.readLine()
                .substring(STRING_VERSION.length() + 1);
            if(!version.trim().equals(VERSION))
            {
                throw new IOException(
                    "Version-nr. not supported by this parser\r\nparser-version: "
                                    + VERSION + "\r\nfile-version: " + version);
            }

            String[] position = r.readLine().split(" ");
            double x;
            double y;
            double w = Double.NaN;
            double h = Double.NaN;
            switch(position.length)
            {
                case 4:
                {
                    w = Double.parseDouble(position[2]);
                    h = Double.parseDouble(position[3]);
                }
                case 2:
                {
                    x = Double.parseDouble(position[0]);
                    y = Double.parseDouble(position[1]);
                    break;
                }
                default:
                {
                    throw new IOException();
                }
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while((line = r.readLine()) != null)
            {
                sb.append(line);
                sb.append("\n");
            }

            return new BaseNotezData(
                NotezFileUtil.removeEnding(file.getName()),
                new BaseNotezStageData(x, y, w, h),
                new BaseNotezPagedData(Arrays.asList(new BaseNotezTextData(
                    sb.toString()))));
        }
    }
}
