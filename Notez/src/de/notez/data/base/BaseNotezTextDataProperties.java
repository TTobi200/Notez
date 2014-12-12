package de.notez.data.base;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import de.notez.data.NotezTextData;
import de.notez.data.NotezTextDataProperties;
import de.util.NotezNonNullStringProperty;

/**
 * Basic implementation of the {@link NotezTextDataProperties}-interface
 *
 * @author ddd
 */
public class BaseNotezTextDataProperties implements NotezTextDataProperties
{
	/** Property holding the text of this data-object */
	protected StringProperty text;

	public BaseNotezTextDataProperties(String text)
	{
		this.text = new NotezNonNullStringProperty(text);
	}

	public BaseNotezTextDataProperties()
	{
		this((String)null);
	}

	public BaseNotezTextDataProperties(NotezTextData textData)
	{
		this(textData.getText());
	}

	public BaseNotezTextDataProperties(ObservableStringValue text)
	{
		this(text.get());
		bind(text);
	}

	@Override
	public StringProperty textProperty()
	{
		return text;
	}
}
