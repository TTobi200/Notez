package de.util;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class NotezGuiUtil
{
	/**
	 * @param node
	 *            The node to handle as a resizer
	 * @param stage
	 *            The stage to resize the node with
	 * @return The resizer handling the new action of the node
	 */
	public static Resizer setNodeAsResizeCompForStage(Node node, Stage stage)
	{
		return new Resizer(node, stage);
	}

	/**
	 * Utility-class for granting a node the ability to resize a Stage
	 */
	public static class Resizer
	{
		protected EventHandler<MouseEvent>	mousePressedHandler;
		protected EventHandler<MouseEvent>	mouseDraggedHandler;

		private double						curX;
		private double						curY;

		private Node						node;
		private Stage						stage;

		public Resizer()
		{
			mousePressedHandler = event -> {
				if (event.getButton() == MouseButton.PRIMARY)
				{
					curX = event.getSceneX();
					curY = event.getSceneY();
				}
			};

			mouseDraggedHandler = event -> {
				if (event.getButton() == MouseButton.PRIMARY)
				{
					double tempH = event.getSceneX() - curY;
					double tempW = event.getSceneY() - curX;
					double height = event.getSceneY() + event.getSceneY() - curX;
					double width = event.getSceneX() + event.getSceneX() - curY;

					if (height < stage.getMinHeight())
					{
						height = stage.getMinHeight();
					}
					if (width < stage.getMinWidth())
					{
						width = stage.getMinWidth();
					}

					stage.setWidth(width);
					stage.setHeight(height);
					curY += tempH;
					curX += tempW;
				}
			};
		}

		public Resizer(Node node, Stage stage)
		{
			this();

			bindNodeAsResizeCompForStage(node, stage);
		}

		/**
		 * Cut all bindings.<br>
		 * Nothing is done if this resizer is not bound
		 */
		public void unbind()
		{
			if (node == null)
			{
				return;
			}

			node.setCursor(Cursor.DEFAULT);

			node.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
			node.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler);

			node = null;
			stage = null;
		}

		/**
		 * @param node
		 *            The node to resize the given stage
		 * @param stage
		 *            The stage to be resized
		 * @throws IllegalStateException
		 *             if this resizer is already bound
		 */
		public void bindNodeAsResizeCompForStage(Node node, Stage stage)
			throws IllegalStateException
		{
			if (this.node != null)
			{
				throw new IllegalStateException("Cannot bind a bound resizer");
			}

			this.node = node;
			this.stage = stage;

			node.setCursor(Cursor.SE_RESIZE);

			node.setOnMousePressed(mousePressedHandler);
			node.setOnMouseDragged(mouseDraggedHandler);
		}
	}
}
