/*
 * $Header$
 *
 * $Log$ Copyright � 2014 T.Ohm . All Rights Reserved.
 */
package de.notez.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Arrays;

import de.notez.NotezNote;
import de.notez.data.NotezData;
import de.notez.data.base.BaseNotezData;
import de.notez.data.base.BaseNotezPagedData;
import de.notez.data.base.BaseNotezTextData;
import de.util.NotezFileUtil;

public class NotezParserV01 extends NotezParserBase
{

    @Override
    public void save(NotezNote note, File file)
        throws IOException
    {
        try (Writer w = new BufferedWriter(new FileWriter(file)))
        {
            w.write(note.getData().getPageData().getText());
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
