package de.notez.data;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;

/**
 * Textdata of a note hold with properties
 *
 * @author ddd
 */
public interface NotezTextDataProperties extends NotezTextData
{
	/**
	 * @return the property representing the text of this note
	 */
	public StringProperty textProperty();

	@Override
	public default String getText()
	{
		return textProperty().get();
	}

	@Override
	public default void setText(String text)
	{
		textProperty().set(text);
	}

	/**
	 * Bind the text of this data to the given text.
	 *
	 * @param text the observable string to bind to.
	 */
	public default void bind(ObservableStringValue text)
	{
		textProperty().bind(text);
	}
}
