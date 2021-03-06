/*
 * $Header$
 * 
 * $Log$
 * Copyright � 2014 T.Ohm . All Rights Reserved.
 */
package de.util;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

public class NotezObservablesUtil
{
	/**
	 * @param list The list to extract the size of
	 * @return A Property describing the size of the given list
	 */
	public static ReadOnlyIntegerProperty sizePropertyForList(ObservableList<?> list)
	{
		ReadOnlyIntegerWrapper size = new ReadOnlyIntegerWrapper(list.size());

		list.addListener((InvalidationListener)observable -> size.set(list.size()));

		return size.getReadOnlyProperty();
	}
}
