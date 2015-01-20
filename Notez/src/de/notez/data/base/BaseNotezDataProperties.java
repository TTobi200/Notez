package de.notez.data.base;

import java.util.Objects;

import javafx.beans.property.ReadOnlyObjectWrapper;
import de.notez.data.*;

/**
 * A basic implementation of the {@link NotezDataProperties}-interface
 *
 * @author ddd
 */
public class BaseNotezDataProperties extends NotezDataPropertiesBase
{
	/** property for the stagedata */
	protected ReadOnlyObjectWrapper<NotezStageDataProperties> stageData;
	/** property for the pagedata */
	protected ReadOnlyObjectWrapper<NotezPagedDataProperties> pageData;

	public BaseNotezDataProperties()
	{
		super();
	}

	public BaseNotezDataProperties(NotezData data)
	{
		super(data);
	}

	public BaseNotezDataProperties(String title, NotezStageData stageData, NotezPagedData pageData)
	{
		super(title, stageData, pageData);
	}

	public BaseNotezDataProperties(String title)
	{
		super(title);
	}

	@Override
	protected ReadOnlyObjectWrapper<NotezPagedDataProperties> pageDataPropertyModifiable()
	{
		if(Objects.isNull(pageData))
		{
			pageData = new ReadOnlyObjectWrapper<>(new BaseNotezPagedDataProperties());
		}

		return pageData;
	}

	@Override
	protected ReadOnlyObjectWrapper<NotezStageDataProperties> stageDataPropertyModifiable()
	{
		if(Objects.isNull(stageData))
		{
			stageData = new ReadOnlyObjectWrapper<>(new BaseNotezStageDataProperties());
		}

		return stageData;
	}

}
