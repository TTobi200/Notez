package de.notez.event;

import javafx.event.*;

/**
 * superclass for all special events thrown by notez
 * 
 * @author ddd
 */
public class NotezEvent extends Event
{
	private static final long serialVersionUID = 1L;
	
	/** Eventtype for all events of notez */
	public static final EventType<NotezEvent> NOTEZ_EVENT_TYPE = new EventType<NotezEvent>(Event.ANY, "NotezEvent");
	
	public NotezEvent(EventType<? extends NotezEvent> eventType)
	{
		super(eventType);
	}

	public NotezEvent(Object source, EventTarget target, EventType<? extends Event> eventType)
	{
		super(source, target, eventType);
	}

}
