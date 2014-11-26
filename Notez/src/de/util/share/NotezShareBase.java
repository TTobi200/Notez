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
import de.util.NotezRegexUtil;

public abstract class NotezShareBase implements NotezShare
{
    public enum NotezShareResult
    {
        SHARED, OFFLINE, BLOCKED, NOT_SUPPORTED
    }

    public static NotezShareResult shareNotez(NotezController ctrl, File notez,
                    Object obj) throws IOException
    {
        if(isMailbox(obj))
        {
            return shareMailbox(ctrl, notez,
                new File(String.valueOf(obj)));
        }
        else if(NotezRegexUtil.isIp(obj))
        {
            return shareTcpIp(ctrl, notez, (String)obj);
        }
        else if(NotezRegexUtil.isMailAdress(obj))
        {
            return shareMail(ctrl, notez, (String)obj);
        }

        return NotezShareResult.NOT_SUPPORTED;
    }

    public static boolean isMailbox(Object obj)
    {
        return obj != null && new File(String.valueOf(obj)).exists()
               && new File(String.valueOf(obj)).isDirectory();
    }

    public static NotezShareResult shareMailbox(NotezController ctrl,
                    File notez, File mailBox) throws IOException
    {
        return new NotezMailboxShare(mailBox).
            shareNotez(ctrl, notez);
    }

    public static NotezShareResult shareTcpIp(NotezController ctrl,
                    File notez, String ip) throws IOException
    {
        return new NotezTcpIpShare(ip).
            shareNotez(ctrl, notez);
    }

    public static NotezShareResult shareMail(NotezController ctrl,
                    File notez, String mailAdress) throws IOException
    {
        return new NotezMailShare(mailAdress).
            shareNotez(ctrl, notez);
    }
}