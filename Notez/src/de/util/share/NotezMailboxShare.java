/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.util.share;

import java.io.File;
import java.io.IOException;

import de.gui.NotezController;
import de.util.notez.NotezParsers;

public class NotezMailboxShare implements NotezShare
{
    private File remoteFolder;

    public NotezMailboxShare(File remoteFolder)
    {
        this.remoteFolder = remoteFolder;
    }

    @Override
    public void shareNotez(NotezController ctrl, File notez) throws IOException
    {
        if(remoteFolder != null && remoteFolder.exists()
           && remoteFolder.canWrite())
        {
            NotezParsers.save(ctrl,
                new File(remoteFolder.getAbsolutePath() + File.separator
                         + notez.getName()));
        }
    }
}