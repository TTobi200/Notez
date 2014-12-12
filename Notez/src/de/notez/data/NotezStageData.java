package de.notez.data;

/**
 * An object representing data about a stage in notez
 *
 * @author ddd
 */
public interface NotezStageData
{
	/**
	 * @return the x-translation of the stage
	 */
	public double getStageX();
	/**
	 * @param stageX The new x-translation of the stage
	 */
	public void setStageX(double stageX);
	/**
	 * @return the y-translation of the stage
	 */
	public double getStageY();
	/**
	 * @param stageY the new y-translation of the stage
	 */
	public void setStageY(double stageY);
	/**
	 * @return the width of the stage
	 */
	public double getStageWidth();
	/**
	 * @param stageWidth the new width of the stage
	 */
	public void setStageWidth(double stageWidth);
	/**
	 * @return the height of the stage
	 */
	public double getStageHeight();
	/**
	 * @param stageHeight the new width of the stage
	 */
	public void setStageHeight(double stageHeight);
}
