package de.notez.prop;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.Property;

import com.sun.javafx.collections.ObservableMapWrapper;

public class NotezPropertiesImp extends ObservableMapWrapper<String, Property<?>> implements
		NotezProperties
{
	public NotezPropertiesImp()
	{
		this(new HashMap<>());
	}

	protected NotezPropertiesImp(Map<String, Property<?>> map)
	{
		super(map);
	}

}
