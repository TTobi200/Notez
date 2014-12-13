package de.util.param;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple list of parameters
 *
 * @author ddd
 *
 * @param <E>
 */
public interface ParameterList<E extends Parameter> extends List<E>
{
	/**
	 * @return all keyparameters contained by this list
	 */
	public List<KeyParameter> getKeyParameters();

	/**
	 * @param key
	 *            the key to check
	 * @return whether the given key is present as parameterkey in this list.
	 */
	public default boolean isKeyPresent(String key)
	{
		String kkey = KeyParameter.asKey(key);

		return getKeyParameters().stream().map(Parameter::getName).anyMatch(k -> k.equals(kkey));
	}

	/**
	 * @param key
	 *            the key to check.
	 * @return whether the given key is present multiple times in this list.
	 */
	public default boolean isKeyMultiplePresent(String key)
	{
		String kkey = KeyParameter.asKey(key);

		return getKeyParameters().stream().map(Parameter::getName).filter(k -> k.equals(kkey))
				.count() > 1l;
	}

	/**
	 * @param key
	 *            the key to get the keyparameter of.
	 * @return the first keyparameter describing the given key.
	 */
	public default KeyParameter getKeyParameter(String key)
	{
		String kkey = KeyParameter.asKey(key);

		if (!isKeyPresent(kkey))
		{
			return null;
		}

		for(KeyParameter param : getKeyParameters())
		{
			if (param.getName().equals(key))
			{
				return param;
			}
		}

		return null;
	}

	/**
	 * @param key the key to check
	 * @return a list with all keyparameters showing the givne key
	 */
	public default List<KeyParameter> getKeyParameters(String key)
	{
		String kkey = KeyParameter.asKey(key);

		if (!isKeyPresent(kkey))
		{
			return Collections.emptyList();
		}
		else if (!isKeyMultiplePresent(kkey))
		{
			return Arrays.asList(getKeyParameter(kkey));
		}
		else
		{
			return getKeyParameters().stream().filter(p -> p.getName().equals(kkey))
					.collect(Collectors.toList());
		}
	}
}
