package de.util.param;

import java.util.LinkedList;

public class Parameters
{
	/**
	 * @param args the parameters to parse
	 * @return a {@link ParameterList} with the parsed parameters
	 */
	public static ParameterList<Parameter> parse(String[] args)
	{
		LinkedList<Parameter> params = new LinkedList<>();
		LinkedList<Parameter> keyParams = new LinkedList<>();

		for(int i = args.length - 1; i > -1; i--)
		{
			Parameter param = parse(args[i], keyParams);

			if(param.isKeyParameter())
			{
				keyParams.clear();
			}
			else
			{
				keyParams.addFirst(param);
			}

			params.addFirst(param);
		}

		return new ParameterListBase<>(params);
	}

	/**
	 * @param arg the string to parse to a parameter
	 * @param keyParams the parameters to add, if the arg is a key string
	 * @return the parsed parameter
	 */
	public static Parameter parse(String arg, LinkedList<Parameter> keyParams)
	{
		Parameter ret;
		if(KeyParameter.isKey(arg))
		{
			ret = new KeyParameterBase(arg, new ParameterListBase<>(keyParams));
		}
		else
		{
			ret = new ParameterBase(arg);
		}

		return ret;
	}
}
