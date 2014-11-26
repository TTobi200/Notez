/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.util.share;

import java.io.File;
import java.io.IOException;

import de.gui.controller.NotezController;
import de.util.share.NotezShareBase.NotezShareResult;

public interface NotezShare
{
    public default NotezShareResult shareNotez(NotezController ctrl,
                    String notez) throws IOException
    {
        return shareNotez(ctrl, new File(notez));
    }

    public NotezShareResult shareNotez(NotezController ctrl, File notez)
        throws IOException;
}
