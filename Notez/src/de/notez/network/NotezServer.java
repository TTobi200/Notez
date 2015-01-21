package de.notez.network;

import java.io.IOException;
import java.net.*;
import java.util.*;

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
	
	protected static Thread getRemoteActionThread()
	{
		return server.remoteActionThread;
	}
	
	/** Thread for doing all {@link NotezRemoteAction}s */
	protected Thread remoteActionThread;
	/** all {@link NotezRemoteAction} to be done */
	protected LinkedList<NotezRemoteAction> actions;

	private NotezServer() throws IOException
	{
		serverSocket = new ServerSocket(SERVER_PORT);
		actions = new LinkedList<>();
		remoteActionThread = new Thread(() ->
		{
			while(true)
			{
				try
				{
					if(!actions.isEmpty())
					{
						actions.poll().exec();
					}
					Thread.sleep(1000);
				}
				catch(SecurityException e)
				{
					NotezLog.error("A remoteAction tried to do an action that was denied by the securitymanager", e);
				}
				catch(InterruptedException e)
				{
					NotezLog.warn("RemoteActionThread was interrupted while sleeping", e);
				}
			}
		},
		"RemoteActionThread");
		
		remoteActionThread.setDaemon(true);
	}

	@SuppressWarnings("resource")
	@Override
	public void run()
	{
		NotezSecurityManager.installSecurityManager();
		remoteActionThread.start();
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
		NotezLog.debug("Received remoteevent: ");
		if(e.getRemoteObject() instanceof NotezRemoteAction)
		{
			actions.addLast((NotezRemoteAction)e.getRemoteObject());
		}
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
