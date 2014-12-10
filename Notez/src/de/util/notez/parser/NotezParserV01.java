/*
 * $Header$
 * 
 * $Log$ Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.util.notez.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Arrays;

import de.gui.controller.NotezControllerBase;
import de.util.NotezFileUtil;
import de.util.notez.data.NotezData;
import de.util.notez.data.base.BaseNotezData;
import de.util.notez.data.base.BaseNotezPagedData;
import de.util.notez.data.base.BaseNotezTextData;

public class NotezParserV01 extends NotezParserBase
{

    @Override
    public void save(NotezControllerBase<?> controller, File file)
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
        return new BaseNotezData(NotezFileUtil.removeEnding(file.getName()),
            null,
            new BaseNotezPagedData(Arrays.asList(new BaseNotezTextData(
                new String(Files
                    .readAllBytes(file.toPath()))))));
    }
}
