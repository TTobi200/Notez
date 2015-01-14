package de.notez.prop;

import java.util.Map;

import javafx.beans.property.Property;

/**
 * A notezproperties-implementation, that throws an illegalstateexception, if calling
 * {@link #put(String, Property)} on a key it already contains.
 *
 * @author ddd
 *
 */
public class NotezFinalProperties extends NotezProperties
{
	public NotezFinalProperties()
	{
		super();
	}

	public NotezFinalProperties(Map<String, Property<?>> map)
	{
		super(map);
	}

	@Override
	public final Property<?> put(String key, Property<?> value) throws IllegalStateException
	{
		System.out.println(key + " - " + value);
		if (containsKey(key))
		{
			throw new IllegalStateException(
					"The final properties already have a property saved for key " + key + " value: " + value);
		}
		return super.put(key, value);
	}
}
