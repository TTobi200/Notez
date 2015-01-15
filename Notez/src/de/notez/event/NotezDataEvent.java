/*
 * Copyright © 2015 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.notez.event;

import java.util.Objects;

import javafx.event.*;
import de.notez.data.NotezData;

/**
 * baseclass for all data-related events.
 * 
 * @author ddd
 */
public class NotezDataEvent extends NotezEvent
{
	private static final long serialVersionUID = 1L;

	/** Evennttype for all data-related events */
	public static final EventType<NotezDataEvent> NOTEZ_DATA_EVENT_TYPE = new EventType<NotezDataEvent>(NOTEZ_EVENT_TYPE, "NotezDataEvent");
	/** eventtype indicating, the given data was saved. */
	public static final EventType<NotezDataEvent> NOTEZ_DATA_SAVE_EVENT_TYPE = new EventType<NotezDataEvent>(NOTEZ_DATA_EVENT_TYPE, "NotezDataSaveEvent");
	/** eventtype indicating, the given data was load. */
	public static final EventType<NotezDataEvent> NOTEZ_DATA_LOAD_EVENT_TYPE = new EventType<NotezDataEvent>(NOTEZ_DATA_EVENT_TYPE, "NotezDataLoadEvent");
	
	/** the data this event is related to */
	protected NotezData data;
	
	public NotezDataEvent(NotezData data)
	{
		this(data, NOTEZ_DATA_EVENT_TYPE);
	}
	
	public NotezDataEvent(NotezData data, Object source)
	{
		this(data, source, NOTEZ_DATA_EVENT_TYPE);
	}
	
	public NotezDataEvent(NotezData data, Object source, EventType<? extends NotezDataEvent> eventType)
	{
		this(data, source, null, eventType);
	}
	
	public NotezDataEvent(NotezData data, EventType<? extends NotezDataEvent> eventType)
	{
		this(data, null, null, eventType);
	}

	public NotezDataEvent(NotezData data, Object source, EventTarget target, EventType<? extends NotezDataEvent> eventType)
	{
		super(source, target, eventType);
		
		this.data = Objects.requireNonNull(data);
	}
	
	public NotezData getData()
	{
		return data;
	}
}
