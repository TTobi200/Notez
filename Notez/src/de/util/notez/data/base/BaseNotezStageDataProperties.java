package de.util.notez.data.base;

import javafx.beans.property.DoubleProperty;
import javafx.stage.Stage;
import de.util.notez.data.NotezStageData;
import de.util.notez.data.NotezStageDataProperties;

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
		// TODO Auto-generated constructor stub
	}

	@Override
	public DoubleProperty stageXProperty()
	{
		return stageX;
	}

	@Override
	public DoubleProperty stageYProperty()
	{
		return stageY;
	}

	@Override
	public DoubleProperty stageWidthProperty()
	{
		return stageWidth;
	}

	@Override
	public DoubleProperty stageHeightProperty()
	{
		return stageHeight;
	}

}
