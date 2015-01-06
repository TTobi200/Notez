package de.util;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NotezListenerUtil
{
	/**
	 * Add listeners so that the given node acts as a relocator for the given stage
	 * 
	 * @param node The new relocator
	 * @param stage The stage to be relocated
	 */
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

	/**
	 * Add listeners so that the given node can be used to resize the given stage.
	 * 
	 * @param node The new resizer
	 * @param stage The stage to be resized
	 */
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
	
	public static void displayNodes(Node[] nodes, boolean visible)
    {
        Timeline line = new Timeline();
        final Duration DUR = Duration.seconds(1d);
        for(Node n : nodes)
        {
            line.getKeyFrames().add(
                new KeyFrame(DUR, new KeyValue(n.opacityProperty(),
                    visible ? 1d : 0d)));
        }
        line.play();
    }
	
	public static void addVisibleNodeHider(Tooltip tT, Node... itms)
    {
        tT.setOnShowing(s -> displayNodes(itms, true));

        tT.setOnHiding(s -> displayNodes(itms, true));
    }

    public static void addVisibleNodeHider(final Node node, Node... itms)
    {
        node.setOnMouseEntered(me -> displayNodes(itms, true));

        node.setOnMouseExited(me -> displayNodes(itms, false));
    }
}
