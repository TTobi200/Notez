package de.notez.prop;

import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableMap;

public interface NotezProperties extends ObservableMap<String, Property<?>>
{
	/**
	 * get the property assigned to the given key.<br>
	 *
	 * @param key
	 *            key of the property to get
	 * @return The wished property or null if not contained
	 */
	@SuppressWarnings("unchecked")
	public default <P extends Property<?>> P getProperty(String key) throws ClassCastException
	{
		if (containsKey(key))
		{
			return (P)get(key);
		}

		return null;
	}

	public default boolean getBoolean(String key)
	{
		return getBooleanProperty(key).get();
	}

	public default long getLong(String key)
	{
		return getLongProperty(key).get();
	}

	public default int getInt(String key)
	{
		return getIntProperty(key).get();
	}

	public default float getFloat(String key)
	{
		return getFloatProperty(key).get();
	}

	public default double getDouble(String key)
	{
		return getDoubleProperty(key).get();
	}

	public default String getString(String key)
	{
		return getStringProperty(key).get();
	}

	public default <O> O getObject(String key)
	{
		return this.<O> getObjectProperty(key).get();
	}

	public default BooleanProperty getBooleanProperty(String key)
	{
		return this.<BooleanProperty> getProperty(key);
	}

	public default LongProperty getLongProperty(String key)
	{
		return this.<LongProperty> getProperty(key);
	}

	public default IntegerProperty getIntProperty(String key)
	{
		return this.<IntegerProperty> getProperty(key);
	}

	public default FloatProperty getFloatProperty(String key)
	{
		return this.<FloatProperty> getProperty(key);
	}

	public default DoubleProperty getDoubleProperty(String key)
	{
		return this.<DoubleProperty> getProperty(key);
	}

	public default StringProperty getStringProperty(String key)
	{
		return this.<StringProperty> getProperty(key);
	}

	public default <O> ObjectProperty<O> getObjectProperty(String key)
	{
		return this.<ObjectProperty<O>> getProperty(key);
	}

	public default Property<?> putBoolean(String key, boolean value)
	{
		if (getType(key) == NotezPropertyType.BOOL)
		{
			getBooleanProperty(key).set(value);
			return get(key);
		}
		else
		{
			return put(key, new SimpleBooleanProperty(value));
		}
	}

	public default Property<?> putLong(String key, long value)
	{
		if (getType(key) == NotezPropertyType.LONG)
		{
			getLongProperty(key).set(value);
			return get(key);
		}
		else
		{
			return put(key, new SimpleLongProperty(value));
		}
	}

	public default Property<?> putInt(String key, int value)
	{
		if (getType(key) == NotezPropertyType.INT)
		{
			getIntProperty(key).set(value);
			return get(key);
		}
		else
		{
			return put(key, new SimpleIntegerProperty(value));
		}
	}

	public default Property<?> putFloat(String key, float value)
	{
		if (getType(key) == NotezPropertyType.FLOAT)
		{
			getFloatProperty(key).set(value);
			return get(key);
		}
		else
		{
			return put(key, new SimpleFloatProperty(value));
		}
	}

	public default Property<?> putDouble(String key, double value)
	{
		if (getType(key) == NotezPropertyType.DOUBLE)
		{
			getDoubleProperty(key).set(value);
			return get(key);
		}
		else
		{
			return put(key, new SimpleDoubleProperty(value));
		}
	}

	public default Property<?> putString(String key, String value)
	{
		if (getType(key) == NotezPropertyType.STRING)
		{
			getStringProperty(key).set(value);
			return get(key);
		}
		else
		{
			return put(key, new SimpleStringProperty(value));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public default Property<?> putObject(String key, Object value)
	{
		if (getType(key) == NotezPropertyType.OBJECT)
		{
			getObjectProperty(key).set(value);
			return get(key);
		}
		else
		{
			return put(key, new SimpleObjectProperty(value));
		}
	}

	public default NotezPropertyType getType(String key)
	{
		Property<?> prop = get(key);

		if (Objects.isNull(prop))
		{
			return NotezPropertyType.NULL;
		}

		if (prop instanceof BooleanProperty)
		{
			return NotezPropertyType.BOOL;
		}
		else if (prop instanceof LongProperty)
		{
			return NotezPropertyType.LONG;
		}
		else if (prop instanceof IntegerProperty)
		{
			return NotezPropertyType.INT;
		}
		else if (prop instanceof FloatProperty)
		{
			return NotezPropertyType.FLOAT;
		}
		else if (prop instanceof DoubleProperty)
		{
			return NotezPropertyType.DOUBLE;
		}
		else if (prop instanceof StringProperty)
		{
			return NotezPropertyType.STRING;
		}

		return NotezPropertyType.OBJECT;
	}

	public static enum NotezPropertyType
	{
		BOOL, LONG, INT, FLOAT, DOUBLE, STRING, OBJECT, NULL;
	}
}
