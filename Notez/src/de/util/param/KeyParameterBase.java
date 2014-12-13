package de.util.param;

public class KeyParameterBase extends ParameterBase implements KeyParameter
{
	/** the parameter belonging to this key */
	protected ParameterList<Parameter> parameter;

	public KeyParameterBase(String name)
	{
		this(name, null);
	}

	public KeyParameterBase(String name, ParameterList<Parameter> parameter)
	{
		super(name);

		this.parameter = parameter;
	}

	@Override
	public ParameterList<Parameter> getParameter()
	{
		return parameter;
	}

	@Override
	public final boolean isKeyParameter()
	{
		return true;
	}

}
