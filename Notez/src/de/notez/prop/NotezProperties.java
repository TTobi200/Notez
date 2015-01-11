package de.notez.prop;

import java.util.HashMap;
import java.util.Map;
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

import com.sun.javafx.collections.ObservableMapWrapper;

public class NotezProperties extends ObservableMapWrapper<String, Property<?>> implements
		ObservableMap<String, Property<?>>
{
	public static final String PROP_FILE = "./Notez.properties";

	public static final String DEF_LOCAL_NOTEZ_FOLDER = ".";
	public static final String DEF_REMOTE_NOTEZ_FOLDER = "./remote";

	public static final String NOTEZ_XML_FILE_START = "<?xml";

	public static final String NOTEZ_XML_ROOT_ELEMENT = "Notez-Properties";

	public static final String NOTEZ_XML_FOLDER_ELEMENT = "Folder";
	public static final String NOTEZ_XML_BUTTONS_ELEMENT = "Button";
	public static final String NOTEZ_XML_SYNC_ELEMENT = "Synchronisation";
	public static final String NOTEZ_XML_SHARE_USER_ELEMENT = "ShareUser";
	public static final String NOTEZ_XML_EMAIL_SETTINGS_ELEMENT = "EMailSettings";

	public static final String NOTEZ_XML_USER_ELEMENT = "User";

	public static final String NOTEZ_REMOTE_FOLDER = "NotezRemoteFolder";
	public static final String NOTEZ_WORK_FOLDER = "NotezWorkFolder";
	public static final String NOTEZ_ALWAYS_SAVE_ON_EXIT = "AlwaysSaveOnExit";

	public static final String NOTEZ_OPEN_RECEIVED_NOTEZ_DIRECTLY = "OpenReceivedNotezDirectly";
	public static final String NOTEZ_SHOW_MESSAGE_ON_NEW_NOTEZ = "ShowMessageOnNewNotez";
	public static final String NOTEZ_LET_RECEIVER_RUNNING = "LetReceiverRunning";
	public static final String NOTEZ_RECEIVER_ON_STARTUP = "ReceiverOnStartup";

	public static final String NOTEZ_BTN_GROUP = "Group";
	public static final String NOTEZ_BTN_SAVE = "Save";
	public static final String NOTEZ_BTN_REMOVE = "Remove";
	public static final String NOTEZ_BTN_SHARE = "Share";
	public static final String NOTEZ_BTN_ADD = "Add";
	public static final String NOTEZ_BTN_PRINT = "Print";
	public static final String NOTEZ_BTN_PIN = "Pin";

	public static final String NOTEZ_REMOTE_USERNAME = "Username";
	public static final String NOTEZ_REMOTE_SHARE_ATTRIBUTE = "Share";

	public static final String NOTEZ_MAIL_HOST = "Hostname";
	public static final String NOTEZ_MAIL_USER = "Username";
	public static final String NOTEZ_MAIL_PORT = "Port";
	public static final String NOTEZ_MAIL_USE_SSL = "UseSSL";

	public NotezProperties()
	{
		this(new HashMap<>());
	}

	public NotezProperties(Map<String, Property<?>> map)
	{
		super(map);
	}

	/**
	 * @param key
	 *            key of the property to get
	 * @return The wished property or null if not contained
	 */
	@SuppressWarnings("unchecked")
	public <P extends Property<?>> P getProperty(String key) throws ClassCastException
	{
		return (P)get(key);
	}

	/**
	 * @param key The key the property is assigned to
	 * @param def The value to use if the key is not contained at the moment
	 * @return The value assigned to the given kea or the def if not contained
	 * @throws ClassCastException
	 */
	@SuppressWarnings("unchecked")
	public <P extends Property<?>> P getProperty(String key, Object def) throws ClassCastException
	{
		if (!containsKey(key))
		{
			putObject(key, def);
		}

		return (P)get(key);
	}

	public boolean getBoolean(String key) throws ClassCastException
	{
		return getBooleanProperty(key).get();
	}

	public boolean getBoolean(String key, boolean def) throws ClassCastException
	{
		return getBooleanProperty(key, def).get();
	}

	public long getLong(String key) throws ClassCastException
	{
		return getLongProperty(key).get();
	}

	public long getLong(String key, long def) throws ClassCastException
	{
		return getLongProperty(key, def).get();
	}

	public int getInt(String key) throws ClassCastException
	{
		return getIntProperty(key).get();
	}

	public int getInt(String key, int def) throws ClassCastException
	{
		return getIntProperty(key, def).get();
	}

	public float getFloat(String key) throws ClassCastException
	{
		return getFloatProperty(key).get();
	}

	public float getFloat(String key, float def) throws ClassCastException
	{
		return getFloatProperty(key, def).get();
	}

	public double getDouble(String key) throws ClassCastException
	{
		return getDoubleProperty(key).get();
	}

	public double getDouble(String key, double def) throws ClassCastException
	{
		return getDoubleProperty(key, def).get();
	}

	public String getString(String key) throws ClassCastException
	{
		return getStringProperty(key).get();
	}

	public String getString(String key, String def) throws ClassCastException
	{
		return getStringProperty(key, def).get();
	}

	public <O> O getObject(String key) throws ClassCastException
	{
		return this.<O> getObjectProperty(key).get();
	}

	public <O> O getObject(String key, Object def) throws ClassCastException
	{
		return this.<O>getObjectProperty(key, def).get();
	}

	public BooleanProperty getBooleanProperty(String key) throws ClassCastException
	{
		return this.<BooleanProperty> getProperty(key);
	}

	public BooleanProperty getBooleanProperty(String key, boolean def) throws ClassCastException
	{
		if(getType(key) != NotezPropertyType.BOOL)
		{
			putBoolean(key, def);
		}
		return getBooleanProperty(key);
	}

	public LongProperty getLongProperty(String key) throws ClassCastException
	{
		return this.<LongProperty> getProperty(key);
	}

	public LongProperty getLongProperty(String key, long def) throws ClassCastException
	{
		if(getType(key) != NotezPropertyType.LONG)
		{
			putLong(key, def);
		}
		return getLongProperty(key);
	}

	public IntegerProperty getIntProperty(String key) throws ClassCastException
	{
		return this.<IntegerProperty> getProperty(key);
	}

	public IntegerProperty getIntProperty(String key, int def) throws ClassCastException
	{
		if(getType(key) != NotezPropertyType.INT)
		{
			putInt(key, def);
		}
		return getIntProperty(key);
	}

	public FloatProperty getFloatProperty(String key) throws ClassCastException
	{
		return this.<FloatProperty> getProperty(key);
	}

	public FloatProperty getFloatProperty(String key, float def) throws ClassCastException
	{
		if(getType(key) != NotezPropertyType.FLOAT)
		{
			putFloat(key, def);
		}
		return getFloatProperty(key);
	}

	public DoubleProperty getDoubleProperty(String key) throws ClassCastException
	{
		return this.<DoubleProperty> getProperty(key);
	}

	public DoubleProperty getDoubleProperty(String key, double def) throws ClassCastException
	{
		if(getType(key) != NotezPropertyType.DOUBLE)
		{
			putDouble(key, def);
		}
		return getDoubleProperty(key);
	}

	public StringProperty getStringProperty(String key) throws ClassCastException
	{
		return this.<StringProperty> getProperty(key);
	}

	public StringProperty getStringProperty(String key, String def) throws ClassCastException
	{
		if(getType(key) != NotezPropertyType.STRING)
		{
			putString(key, def);
		}
		return getStringProperty(key);
	}

	public <O> ObjectProperty<O> getObjectProperty(String key) throws ClassCastException
	{
		return this.<ObjectProperty<O>> getProperty(key);
	}

	public <O> ObjectProperty<O> getObjectProperty(String key, Object def) throws ClassCastException
	{
		if(getType(key) != NotezPropertyType.OBJECT)
		{
			putObject(key, def);
		}
		return getObjectProperty(key);
	}

	public Property<?> putBoolean(String key, boolean value)
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

	public Property<?> putLong(String key, long value)
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

	public Property<?> putInt(String key, int value)
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

	public Property<?> putFloat(String key, float value)
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

	public Property<?> putDouble(String key, double value)
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

	public Property<?> putString(String key, String value)
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
	public Property<?> putObject(String key, Object value)
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

	public NotezPropertyType getType(String key)
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
