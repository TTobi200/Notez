/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.notez.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.util.log.NotezLog;

/**
 * Aserver for handling the connection-requests to this notez-instance as well
 * as the connections to any other instance.
 */
public class NotezServer extends Thread implements NotezRemoteObjectListener
{
	/** The server running */
	private static NotezServer server;
	/** The socket of this server */
	private static ServerSocket serverSocket;
	/** The port of notez-applications */
	public static final int SERVER_PORT = 55555;

	/** all currently opened clients mapped by their inetaddress */
	protected static Map<InetAddress, NotezClientConnection> mapIpToClient = new HashMap<>();

	/** All listenrs added */
	private static List<NotezRemoteObjectListener> listener = new ArrayList<>();

	public static synchronized void initialize() throws IOException
	{
		if(Objects.isNull(server))
		{
			server = new NotezServer();
			addRemoteListener(server);
			server.setDaemon(true);
			server.start();
		}
	}

	private NotezServer() throws IOException
	{
		serverSocket = new ServerSocket(SERVER_PORT);
	}

	@SuppressWarnings("resource")
	@Override
	public void run()
	{
		try
		{
			Socket socket;
			while(true)
			{
				if(Objects.nonNull(socket = serverSocket.accept()))
				{
					new NotezClientConnection(socket);
				}
			}
		}
		catch(Exception e)
		{
			NotezLog.error("", e);
		}
	}

	public static void addRemoteListener(NotezRemoteObjectListener l)
	{
		if(Objects.nonNull(l))
		{
			listener.add(l);
		}
	}

	public static void removeRemoteListener(NotezRemoteObjectListener l)
	{
		if(Objects.nonNull(l))
		{
			listener.remove(l);
		}
	}

	protected static void fireRemoteEvent(NotezRemoteObjectEvent e)
	{
		listener.forEach(l -> l.remoteObjectReceived(e));
	}

	@Override
	public void remoteObjectReceived(NotezRemoteObjectEvent e)
	{
		// TODO default handling if possible
	}

	public static NotezClientConnection getConnection(String host)
		throws IOException
	{
		return getConnection(InetAddress.getByName(host));
	}

	public static NotezClientConnection getConnection(InetAddress address)
		throws IOException
	{
		if(mapIpToClient.containsKey(address))
		{
			return mapIpToClient.get(address);
		}
		else
		{
			return new NotezClientConnection(new Socket(address, SERVER_PORT));
		}
	}

}
