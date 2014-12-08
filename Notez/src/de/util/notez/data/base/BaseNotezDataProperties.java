package de.util.notez.data.base;

import java.util.Objects;

import javafx.beans.property.ReadOnlyObjectWrapper;
import de.util.notez.data.NotezData;
import de.util.notez.data.NotezDataProperties;
import de.util.notez.data.NotezPagedData;
import de.util.notez.data.NotezPagedDataProperties;
import de.util.notez.data.NotezStageData;
import de.util.notez.data.NotezStageDataProperties;

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
			pageData = new ReadOnlyObjectWrapper<>();
		}

		return pageData;
	}

	@Override
	protected ReadOnlyObjectWrapper<NotezStageDataProperties> stageDataPropertyModifiable()
	{
		if(Objects.isNull(stageData))
		{
			stageData = new ReadOnlyObjectWrapper<>();
		}

		return stageData;
	}

}
