package de.util.param;

import java.util.Objects;

/**
 * A paramter given to a main-method<br>
 * a parameter can either be
 * <ul>
 * <li>a keyparameter as indicated in {@link KeyParameter}
 * <li>a pramter belonging toa keyparameter
 * <li>a simple non-belonging parameter.
 * </ul>
 *
 * @author ddd
 */
public interface Parameter
{
	/** the keyparameter, this parameter belongs to. */
	public KeyParameter getKeyParameter();
	/** The name represented by this parameter */
	public String getName();

	/**
	 * @return whether this is a keyparameter
	 */
	public default boolean isKeyParameter()
	{
		return false;
	}

	/**
	 * @return whether this parameter belongs to a key parameter.
	 */
	public default boolean isFromKeyParameter()
	{
		return Objects.nonNull(getKeyParameter());
	}
}
