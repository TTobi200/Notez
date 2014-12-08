package de.util.notez.data;

import javafx.beans.property.DoubleProperty;
import javafx.stage.Stage;

/**
 * {@link NotezStageData} holding its data with properties.
 *
 * @author ddd
 */
public interface NotezStageDataProperties extends NotezStageData
{
	/**
	 * @return The property representing the x-translation of the stage
	 */
	public DoubleProperty stageXProperty();

	/**
	 * @return The property representing the y-translation of the stage
	 */
	public DoubleProperty stageYProperty();

	/**
	 * @return The property representing the width of the stage
	 */
	public DoubleProperty stageWidthProperty();

	/**
	 * @return The property representing the height of the stage
	 */
	public DoubleProperty stageHeightProperty();

	@Override
	public default double getStageX()
	{
		return stageXProperty().get();
	}

	@Override
	public default void setStageX(double stageX)
	{
		stageXProperty().set(stageX);
	}

	@Override
	public default double getStageY()
	{
		return stageYProperty().get();
	}

	@Override
	public default void setStageY(double stageY)
	{
		stageYProperty().set(stageY);
	}

	@Override
	public default double getStageWidth()
	{
		return stageWidthProperty().get();
	}

	@Override
	public default void setStageWidth(double stageWidth)
	{
		stageWidthProperty().set(stageWidth);
	}

	@Override
	public default double getStageHeight()
	{
		return stageHeightProperty().get();
	}

	@Override
	public default void setStageHeight(double stageHeight)
	{
		stageHeightProperty().set(stageHeight);
	}

	/**
	 * Bind this data to the given stage, so that it always holds the actual data of it.
	 *
	 * @param stage
	 *            the stage to symbolize the data of.
	 */
	public default void bind(Stage stage)
	{
		stageXProperty().bind(stage.xProperty());
		stageYProperty().bind(stage.yProperty());
		stageWidthProperty().bind(stage.widthProperty());
		stageHeightProperty().bind(stage.heightProperty());
	}
}
