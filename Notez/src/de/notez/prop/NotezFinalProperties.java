package de.notez.prop;

import javafx.beans.property.Property;

/**
 * A notezproperties-implementation, that throws an illegalstateexception, if calling
 * {@link #put(String, Property)} on a key it already contains.
 *
 * @author ddd
 *
 */
public class NotezFinalProperties extends NotezPropertiesImp
{
	public NotezFinalProperties()
	{
		super();
	}

	@Override
	public final Property<?> put(String key, Property<?> value) throws IllegalStateException
	{
		if (containsKey(key))
		{
			throw new IllegalStateException(
					"The final properties already have a property sved for key " + key);
		}
		return super.put(key, value);
	}
}
