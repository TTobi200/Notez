/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.util.notez;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

import de.gui.controller.NotezControllerBase;

public class NotezParserV01 extends NotezParserBase
{

    @Override
    public void save(NotezControllerBase<?, ?> controller, File file)
        throws IOException
    {
        try (Writer w = new BufferedWriter(new FileWriter(file)))
        {
            w.write(controller.getNoteText());
        }
    }

    @Override
    protected NotezData parseImpl(File file) throws IOException
    {
        return new NotezData(file.getName(), new String(
            Files.readAllBytes(file.toPath())), null, null);
    }
}
