/*
 * $Header$
 *
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.notez.share;

import java.io.*;

import de.notez.NotezNote;
import de.notez.share.NotezShareBase.NotezShareResult;

public interface NotezShare
{
    public default NotezShareResult shareNotez(NotezNote note,
                    String notez) throws IOException
    {
        return shareNotez(note, new File(notez));
    }

    public NotezShareResult shareNotez(NotezNote note, File notez)
        throws IOException;
}
