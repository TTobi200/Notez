package de.notez.network;

import java.util.EventObject;
import java.util.Objects;

/**
 * Event when a remoteobject was received
 */
public class NotezRemoteObjectEvent extends EventObject
{
	private static final long serialVersionUID = 1L;
	
	/** the remoteobject received */
	protected NotezRemoteObject remoteObject;
	/** The client who received the remoteobject */
	protected NotezClientConnection client;

	public NotezRemoteObjectEvent(Object source, NotezRemoteObject ro, NotezClientConnection client)
	{
		super(source);
		
		remoteObject = Objects.requireNonNull(ro);
		this.client = Objects.requireNonNull(client);
	}
	
	public NotezRemoteObject getRemoteObject()
	{
		return remoteObject;
	}
	
	public NotezClientConnection getClient()
	{
		return client;
	}
}
