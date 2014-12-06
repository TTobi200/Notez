/*
 * $Header$
 * 
 * $Log$
 * Copyright � 2014 T.Ohm . All Rights Reserved.
 */
package de.util.share;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import de.gui.controller.NotezController;
import de.util.NotezRemoteSync;
import de.util.notez.data.NotezData;
import de.util.notez.data.base.BaseNotezData;

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
            ObjectOutputStream out = new ObjectOutputStream(
                socket.getOutputStream());

            NotezData data = ctrl.getData();

            if(!socket.isConnected())
            {
                return NotezShareResult.OFFLINE
                    .setDetailMsg("Can't connect to " + ip);
            }
            else if(!(data instanceof BaseNotezData))
            {
                return NotezShareResult.NOT_SUPPORTED
                    .setDetailMsg("The data used is not sharable. Please update!");
            }

            out.writeObject(((BaseNotezData)data)
                .asSerializableData());
            out.flush();

            socket.close();

            return NotezShareResult.SHARED
                .setDetailMsg("Notez sucsessfull shared with " + ip);
        }
    }
}