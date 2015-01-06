/*
 * $Header$
 *
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.notez.share;

import java.io.File;
import java.io.IOException;

import de.notez.NotezNote;
import de.util.NotezRegexUtil;

public abstract class NotezShareBase implements NotezShare
{
    public enum NotezShareResult
    {
        SHARED(null), OFFLINE(null), BLOCKED(null), NOT_SUPPORTED(null), CANCELD_BY_USER(
                        null);

        private String detailMsg;

        private NotezShareResult(String detailMsg)
        {
            this.detailMsg = detailMsg;
        }

        public String getDetailMsg()
        {
            return detailMsg;
        }

        public NotezShareResult setDetailMsg(String detailMsg)
        {
            this.detailMsg = detailMsg;
            return this;
        }

        public boolean hasDetailMsg()
        {
            return detailMsg != null;
        }
    }

    public static NotezShareResult shareNotez(NotezNote note, File notez,
                    Object obj) throws IOException
    {
        if(isMailbox(obj))
        {
            return shareMailbox(note, notez,
                new File(String.valueOf(obj)));
        }
        else if(NotezRegexUtil.isIp(obj))
        {
            return shareTcpIp(note, notez, (String)obj);
        }
        else if(NotezRegexUtil.isMailAdress(obj))
        {
            return shareMail(note, notez, (String)obj);
        }

        return NotezShareResult.NOT_SUPPORTED;
    }

    public static boolean isMailbox(Object obj)
    {
        return obj != null && new File(String.valueOf(obj)).exists()
               && new File(String.valueOf(obj)).isDirectory();
    }

    public static NotezShareResult shareMailbox(NotezNote note,
                    File notez, File mailBox) throws IOException
    {
        return new NotezMailboxShare(mailBox).
            shareNotez(note, notez);
    }

    public static NotezShareResult shareTcpIp(NotezNote note,
                    File notez, String ip) throws IOException
    {
        return new NotezTcpIpShare(ip).
            shareNotez(note, notez);
    }

    public static NotezShareResult shareMail(NotezNote note,
                    File notez, String mailAdress) throws IOException
    {
        return new NotezMailShare(mailAdress).
            shareNotez(note, notez);
    }
}