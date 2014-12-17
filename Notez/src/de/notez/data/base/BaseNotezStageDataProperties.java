package de.notez.data.base;

import java.util.Objects;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.stage.Stage;
import de.notez.data.NotezStageData;
import de.notez.data.NotezStageDataProperties;

public class BaseNotezStageDataProperties implements NotezStageDataProperties
{
	/** Property representing the x-translation of the stage */
	protected DoubleProperty stageX;
	/** Property representing the y-translation of the stage */
	protected DoubleProperty stageY;
	/** Property representing the width of the stage */
	protected DoubleProperty stageWidth;
	/** Property representing the height of the stage */
	protected DoubleProperty stageHeight;

	public BaseNotezStageDataProperties()
	{
		this(0d, 0d);
	}

	public BaseNotezStageDataProperties(NotezStageData stageData)
	{
		this(stageData.getStageX(), stageData.getStageY(), stageData.getStageWidth(), stageData
				.getStageHeight());
	}

	public BaseNotezStageDataProperties(Stage stage)
	{
		this(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());

		bind(stage);
	}

	public BaseNotezStageDataProperties(double stageWidth, double stageHeight)
	{
		this(0d, 0d, stageWidth, stageHeight);
	}

	public BaseNotezStageDataProperties(double stageX, double stageY, double stageWidth,
			double stageHeight)
	{
		super();
		
		setStageX(stageX);
		setStageY(stageY);
		setStageWidth(stageWidth);
		setStageHeight(stageHeight);
	}

	@Override
	public DoubleProperty stageXProperty()
	{
		if(Objects.isNull(stageX))
		{
			stageX = new SimpleDoubleProperty(0d);
		}
		return stageX;
	}

	@Override
	public DoubleProperty stageYProperty()
	{
		if(Objects.isNull(stageY))
		{
			stageY = new SimpleDoubleProperty(0d);
		}
		return stageY;
	}

	@Override
	public DoubleProperty stageWidthProperty()
	{
		if(Objects.isNull(stageWidth))
		{
			stageWidth = new SimpleDoubleProperty(0d);
		}
		return stageWidth;
	}

	@Override
	public DoubleProperty stageHeightProperty()
	{
		if(Objects.isNull(stageHeight))
		{
			stageHeight = new SimpleDoubleProperty(0d);
		}
		return stageHeight;
	}

}
