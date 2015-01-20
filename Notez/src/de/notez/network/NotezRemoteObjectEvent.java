package de.notez.network;

import java.util.Objects;

import javafx.event.EventType;
import de.notez.event.NotezEvent;

/**
 * Event when a remoteobject was received
 */
public class NotezRemoteObjectEvent extends NotezEvent
{
	private static final long serialVersionUID = 1L;
	
	public static final EventType<NotezRemoteObjectEvent> NOTEZ_REMOTE_EVENT_TYPE = new EventType<NotezRemoteObjectEvent>(NOTEZ_EVENT_TYPE, "NotezRemoteEvent");
	
	/** the remoteobject received */
	protected NotezRemoteObject remoteObject;
	/** The client who received the remoteobject */
	protected NotezClientConnection client;

	public NotezRemoteObjectEvent(Object source, NotezRemoteObject ro, NotezClientConnection client)
	{
		super(source, null, NOTEZ_REMOTE_EVENT_TYPE);
		
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
	
	@Override
	public String toString()
	{
		return "remoteevent - client:" + getClient() + ", object:" + getRemoteObject();
	}
}
