/*
 * $Header$
 * 
 * $Log$
 * Copyright � 2014 T.Ohm . All Rights Reserved.
 */
package de.notez.share;

import java.io.File;
import java.io.IOException;

import de.gui.controller.NotezController;
import de.notez.network.NotezServer;

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
		try
		{
			NotezServer.getConnection(ip).send(
				ctrl.getData().asSerializableData());
			return NotezShareResult.SHARED.setDetailMsg("Notez sucsessfull shared with "
														+ ip);
		}
		catch(IOException e)
		{
			return NotezShareResult.OFFLINE.setDetailMsg("Can't connect to "
														 + ip);
		}

		// try (Socket socket = new Socket(ip, NotezServer.SERVER_PORT))
		// {
		// ObjectOutputStream out = new ObjectOutputStream(
		// socket.getOutputStream());
		//
		// NotezData data = ctrl.getData();
		//
		// if(!socket.isConnected())
		// {
		// return NotezShareResult.OFFLINE.setDetailMsg("Can't connect to "
		// + ip);
		// }
		//
		// out.writeObject(data.asSerializableData());
		// out.flush();
		//
		// socket.close();
		//
		// return
		// NotezShareResult.SHARED.setDetailMsg("Notez sucsessfull shared with "
		// + ip);
		// }
	}
}