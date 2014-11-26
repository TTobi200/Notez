/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.util.share;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import de.gui.controller.NotezController;
import de.util.NotezRemoteSync;

public class NotezTcpIpShare extends NotezShareBase
{
    protected String ip;

    public NotezTcpIpShare(String ip)
    {
        this.ip = ip;
    }

    @Override
    public NotezShareResult shareNotez(NotezController ctrl, File notez)
        throws IOException
    {
        try (Socket socket = new Socket(ip, NotezRemoteSync.SERVER_PORT))
        {
            PrintWriter printWriter =
                            new PrintWriter(
                                new OutputStreamWriter(
                                    socket.getOutputStream()));

            // TODO: Send notez file as string in xml format!
            // Or better use a protocol :D
            printWriter.print(ctrl.getNoteText());
            printWriter.flush();

            // close connection
            socket.close();

            return NotezShareResult.SHARED;
        }
    }
}