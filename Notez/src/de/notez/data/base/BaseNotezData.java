package de.notez.data.base;

import de.notez.data.NotezData;
import de.notez.data.NotezPagedData;
import de.notez.data.NotezStageData;
import de.util.NotezDataUtil;

/**
 * A basic implementation of the {@link NotezData}-interface.<br>
 * This class also provides the ability to transform any notezdata into a
 * serializable pendant via {@link #asSerializableData(NotezData)}
 *
 * @author ddd
 */
public class BaseNotezData implements NotezData
{
    /** The title of this data */
    protected String title;
    /** The stagedata of this data */
    protected NotezStageData stageData;
    /** The pagedata of this data */
    protected NotezPagedData pageData;

    public BaseNotezData()
    {
    }

    public BaseNotezData(String title, NotezStageData stageData,
                         NotezPagedData pageData)
    {
        super();

        this.title = title;
        this.stageData = stageData == null ? new BaseNotezStageData()
                        : stageData;
        this.pageData = pageData == null ? new BaseNotezPagedData() : pageData;
    }

    @Override
    public String getTitle()
    {
        return title;
    }

    @Override
    public void setTitle(String title)
    {
        this.title = title == null ? "" : title;
    }

    @Override
    public NotezStageData getStageData()
    {
        return stageData;
    }

    @Override
    public NotezPagedData getPageData()
    {
        return pageData;
    }
    
    @Override
	public String toString()
	{
		return NotezDataUtil.toString(this);
	}
}