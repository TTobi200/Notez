package de.notez.network;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

/**
 * Aconenction to a client
 */
public class NotezClientConnection implements Closeable
{
	/** the socket of the connection of this client */
	protected Socket socket;
	/** whether to keep this connection runnig */
	protected boolean keepRunning;

	/** The outputstream of this connection for writing */
	protected ObjectOutputStream out;

	public NotezClientConnection(Socket socket) throws IOException
	{
		this.socket = Objects.requireNonNull(socket);

		if(!socket.isConnected())
		{
			throw new IOException("socket given to client is not connected");
		}
		
		keepRunning = true;
		startSender();
		startReceiver();
		
		NotezServer.mapIpToClient.put(socket.getInetAddress(), this);
	}

	private void startSender() throws IOException
	{
		out = new ObjectOutputStream(
			socket.getOutputStream());
	}

	private void startReceiver() throws IOException
	{
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

		Thread t = new Thread(() ->
		{
			while(keepRunning && !socket.isInputShutdown()
				  && !socket.isOutputShutdown() && !socket.isClosed()
				  && !socket.isClosed())
			{
				try
				{
					fireRemoteEvent((NotezRemoteObject)in.readObject());
				}
				catch(ClassNotFoundException e)
				{
					e.printStackTrace();
				}
				catch(IOException e)
				{
					keepRunning = false;
					// e.printStackTrace();
        		}
        		catch(RuntimeException e)
        		{
        			e.printStackTrace();
        		}
        	}
        
        	try
        	{
        		close();
        	}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        	}
        
		});

		t.setDaemon(true);
		t.start();
	}

	protected void fireRemoteEvent(NotezRemoteObject ro)
	{
		NotezServer.fireRemoteEvent(new NotezRemoteObjectEvent(socket, ro, this));
	}

	/**
	 * @param ro The object to send
	 * @throws IOException
	 */
	public void send(NotezRemoteObject ro) throws IOException
	{
		if(Objects.nonNull(ro))
		{
			out.writeObject(ro);
		}
	}

	@Override
	public void close() throws IOException
	{
		keepRunning = false;
		NotezServer.mapIpToClient.remove(socket.getInetAddress());
		socket.close();
	}
}
