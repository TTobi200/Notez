package de.util;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class NotezListenerUtil
{
	public static void setAsRelocateNode(Node node, Stage stage)
	{

		EventHandler<MouseEvent> lis = new EventHandler<MouseEvent>()
		{
			private double x;
			private double y;

			@Override
			public void handle(MouseEvent event)
			{
				if (event.getButton() == MouseButton.PRIMARY)
				{
					if (event.getEventType() == MouseEvent.MOUSE_PRESSED)
					{
						x = event.getSceneX();
						y = event.getSceneY();

					}
					else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED)
					{
						stage.setX(event.getScreenX() - x);
						stage.setY(event.getScreenY() - y);
					}
				}
			}
		};

		node.setOnMousePressed(lis);
		node.setOnMouseDragged(lis);
	}

	public static void setAsResizeNode(Node node, Stage stage)
	{
		EventHandler<MouseEvent> lis = new EventHandler<MouseEvent>()
		{
			private double x;
			private double y;

			@Override
			public void handle(MouseEvent event)
			{
				if (event.getButton() == MouseButton.PRIMARY)
				{
					if (event.getEventType() == MouseEvent.MOUSE_PRESSED)
					{
						x = event.getSceneX();
						y = event.getSceneY();
					}
					else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED)
					{
						double tempH = event.getSceneX() - y;
						double tempW = event.getSceneY() - x;
						double height = event.getSceneY() + event.getSceneY() - x;
						double width = event.getSceneX() + event.getSceneX() - y;

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
						y += tempH;
						x += tempW;
					}
				}
			}
		};

		node.setOnMousePressed(lis);
		node.setOnMouseDragged(lis);
	}
}
