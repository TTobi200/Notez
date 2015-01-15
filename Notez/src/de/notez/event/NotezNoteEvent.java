/*
 * Copyright © 2015 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.notez.event;

import java.util.Objects;

import javafx.event.*;
import de.notez.NotezNote;

/**
 * A special notezevent, that belongs to a note
 * 
 * @author ddd
 */
public class NotezNoteEvent extends NotezEvent
{
	private static final long serialVersionUID = 1L;
	
	/** super-eventtype of all notezevents */
	public static final EventType<NotezNoteEvent> NOTEZ_NOTE_EVENT_TYPE = new EventType<NotezNoteEvent>(NOTEZ_EVENT_TYPE, "NotezNoteEvent");
	public static final EventType<NotezNoteEvent> NOTEZ_NOTE_SAVED_EVENT_TYPE = new EventType<NotezNoteEvent>(NOTEZ_NOTE_EVENT_TYPE, "NotezNoteSavedEvent");
	public static final EventType<NotezNoteEvent> NOTEZ_NOTE_LOADED_EVENT_TYPE = new EventType<NotezNoteEvent>(NOTEZ_NOTE_EVENT_TYPE, "NotezNoteLoadedEvent");
	
	/** the note, this event is related to */
	protected NotezNote note;
	
	public NotezNoteEvent(NotezNote note)
	{
		this(note, NOTEZ_NOTE_EVENT_TYPE);
	}
	
	public NotezNoteEvent(NotezNote note, EventType<? extends NotezNoteEvent> eventType)
	{
		this(note, null, null, eventType);
	}

	protected NotezNoteEvent(NotezNote note, Object source, EventTarget target, EventType<? extends Event> eventType)
	{
		super(source, target, eventType);
		
		this.note = Objects.requireNonNull(note);
	}

	public NotezNote getNote()
	{
		return note;
	}
}
