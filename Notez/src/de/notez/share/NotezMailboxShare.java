/*
 * $Header$
 *
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.notez.share;

import java.io.*;

import de.notez.NotezNote;
import de.notez.parser.NotezParsers;

public class NotezMailboxShare extends NotezShareBase
{
    protected File mailbox;

    public NotezMailboxShare(File mailbox)
    {
        this.mailbox = mailbox;
    }

    @Override
    public NotezShareResult shareNotez(NotezNote note, File notez)
        throws IOException
    {
        if(mailbox != null && mailbox.exists()
           && mailbox.canWrite())
        {
            NotezParsers.save(note,
                new File(mailbox.getAbsolutePath() + File.separator
                         + notez.getName()));
            return NotezShareResult.SHARED;
        }

        return NotezShareResult.OFFLINE;
    }
}