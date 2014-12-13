package de.util.param;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParameterListBase<E extends Parameter> extends AbstractList<E> implements
		ParameterList<E>
{
	/** the content of this lsit */
	protected List<E> data;
	/** the key hold int his list */
	protected List<KeyParameter> keys;

	protected ParameterListBase()
	{
		data = new ArrayList<>();
	}

	public ParameterListBase(List<E> data)
	{
		this();
		this.data.addAll(data);
		keys = data.stream().filter(p -> p.isKeyParameter()).map(p -> (KeyParameter)p)
				.collect(Collectors.toList());
	}

	@Override
	public E get(int index)
	{
		return index < size() ? data.get(index) : null;
	}

	@Override
	public int size()
	{
		return data.size();
	}

	@Override
	public List<KeyParameter> getKeyParameters()
	{
		return data.stream().filter(p -> p.isKeyParameter()).map(p -> (KeyParameter)p)
				.collect(Collectors.toList());
	}
}
