package de.notez.data.base;

import de.notez.data.NotezStageData;
import de.util.NotezDataUtil;

public class BaseNotezStageData implements NotezStageData
{
	/** The x-translation of the stage */
	protected double stageX;
	/** The y-translation of the stage */
	protected double stageY;
	/** The width of the stage */
	protected double stageWidth;
	/** The height of the stage */
	protected double stageHeight;

	public BaseNotezStageData()
	{
		this(0d, 0d);
	}

	public BaseNotezStageData(double stageWidth, double stageHeight)
	{
		this(0d, 0d, stageWidth, stageHeight);
	}

	public BaseNotezStageData(double stageX, double stageY, double stageWidth, double stageHeight)
	{
		super();
		setStageX(stageX);
		setStageY(stageY);
		setStageWidth(stageWidth);
		setStageHeight(stageHeight);
	}

	@Override
	public double getStageX()
	{
		return stageX;
	}

	@Override
	public void setStageX(double stageX)
	{
		this.stageX = stageX > 0d ? stageX : 0d;
	}

	@Override
	public double getStageY()
	{
		return stageY;
	}

	@Override
	public void setStageY(double stageY)
	{
		this.stageY = stageY > 0d ? stageY : 0d;
	}

	@Override
	public double getStageWidth()
	{
		return stageWidth;
	}

	@Override
	public void setStageWidth(double stageWidth)
	{
		this.stageWidth = stageWidth > 0d ? stageWidth : 0d;
	}

	@Override
	public double getStageHeight()
	{
		return stageHeight;
	}

	@Override
	public void setStageHeight(double stageHeight)
	{
		this.stageHeight = stageHeight > 0d ? stageHeight : 0d;
	}
	
	@Override
	public String toString()
	{
		return NotezDataUtil.toString(this);
	}
}
