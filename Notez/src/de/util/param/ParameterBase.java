package de.util.param;

import java.util.Objects;

public class ParameterBase implements Parameter
{
	/** the keyparameter this parameter belongs to */
	protected KeyParameter keyParameter;
	/** the string represented by this parameter */
	protected String name;

	public ParameterBase(String name)
	{
		this(name, null);
	}

	public ParameterBase(String name, KeyParameter keyParameter)
	{
		this.name = Objects.requireNonNull(name);
		this.keyParameter = keyParameter;

		if(name.isEmpty())
		{
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public KeyParameter getKeyParameter()
	{
		return keyParameter;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
		{
			return false;
		}

		if(obj instanceof Parameter)
		{
			return ((Parameter)obj).getName().equals(getName());
		}

		return false;
	}
}
