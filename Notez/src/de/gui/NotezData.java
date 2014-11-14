package de.gui;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.StringProperty;

public interface NotezData
{
	public StringProperty savedTextProperty();

	public StringProperty curTextProperty();

	public default ReadOnlyBooleanProperty textChangedProperty()
	{
		ReadOnlyBooleanWrapper b = new ReadOnlyBooleanWrapper();
		b.bind(curTextProperty().isNotEqualTo(savedTextProperty()));
		return b.getReadOnlyProperty();
	}

	public default void saveText()
	{
		savedTextProperty().set(curTextProperty().get());
	}
}
