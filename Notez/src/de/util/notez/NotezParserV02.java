/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.util.notez;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javafx.geometry.Point2D;
import de.gui.controller.NotezControllerBase;

public class NotezParserV02 extends NotezParserBase
{
    public static final String VERSION = "0.2";

    @Override
    public void save(NotezControllerBase<?, ?> controller, File file) throws IOException
    {
        try (Writer w = new BufferedWriter(new FileWriter(file)))
        {
            w.write(new StringBuilder(STRING_VERSION).append(' ')
                .append(VERSION)
                .append(System.lineSeparator())

                .append(controller.getStage().getX())
                .append(' ')
                .append(controller.getStage().getY())
                .append(' ')
                .append(controller.getStage().getWidth())
                .append(' ')
                .append(controller.getStage().getHeight())
                .append(System.lineSeparator())

                .append(controller.getNoteText())
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

            return new NotezData(file.getName(), sb.toString(), new Point2D(x,
                y), new Point2D(w, h));
        }
    }
}
