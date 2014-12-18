/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.util;

import java.util.Objects;

import javafx.beans.property.SimpleStringProperty;

/**
 * A {@link SimpleStringProperty}-implentation that always contains a non-null String
 *
 * @author ddd
 */
public class NotezNonNullStringProperty extends SimpleStringProperty
{
	/** The String to use instead of null */
	public static final String NULL_STRING = "";

	public NotezNonNullStringProperty()
	{
		this(null);
	}

	public NotezNonNullStringProperty(Object bean, String name, String initialValue)
	{
		super(bean, name, initialValue);
		set(initialValue);
	}

	public NotezNonNullStringProperty(Object bean, String name)
	{
		super(bean, name);
		set(null);
	}

	public NotezNonNullStringProperty(String initialValue)
	{
		super(initialValue);
		set(initialValue);
	}

	@Override
	public void set(String newValue)
	{
		super.set(Objects.isNull(newValue) ? NULL_STRING : newValue);
	}
}
