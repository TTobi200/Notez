package de.util.param;

/**
 * A special parameter that begins with {@link #KEY_PREFIX} and can have other parameters belonging
 * to it.
 *
 * @author ddd
 */
public interface KeyParameter extends Parameter
{
	/** the prefic indiccating, the given String is a key. */
	public static final String KEY_PREFIX = "-";

	// public static final String KEY_PREFIX = "/";

	/**
	 * Use the given key as key-String as indicated by this class
	 *
	 * @param key the String to use as key
	 * @return The String as a key-string
	 */
	public static String asKey(String key)
	{
		return isKey(key) ? key : KEY_PREFIX + key;
	}

	/**
	 * @param key the string to check
	 * @returnwhether the givne key is a keystring
	 */
	public static boolean isKey(String key)
	{
		return key.startsWith(KEY_PREFIX);
	}

	/**
	 * @return aparameterlist wiht the parameters belonging to this key.
	 */
	public ParameterList<Parameter> getParameter();

	@Override
	public default boolean isKeyParameter()
	{
		return true;
	}
}
