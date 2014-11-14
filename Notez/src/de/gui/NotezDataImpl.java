package de.gui;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NotezDataImpl implements NotezData
{
	private StringProperty savedText;
	private StringProperty curText;
	private ReadOnlyBooleanProperty textChanged;

	public NotezDataImpl()
	{
		savedText = new SimpleStringProperty("");
		curText = new SimpleStringProperty("");
	}

	@Override
	public StringProperty savedTextProperty()
	{
		return savedText;
	}

	@Override
	public StringProperty curTextProperty()
	{
		return curText;
	}

	@Override
	public ReadOnlyBooleanProperty textChangedProperty()
	{
		if(textChanged == null)
		{
			textChanged = NotezData.super.textChangedProperty();
		}

		return textChanged;
	}
}
