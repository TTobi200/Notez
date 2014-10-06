/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.gui;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import de.util.NotezFileUtil;

public class NotezDialog
{
	public static final String FXML_PATH = "include/fxml/NotezDialog.fxml";
	private static final String ICON_ERROR = "include/icons/dialog-error.png";
	private static final String ICON_WARNING = "include/icons/dialog-warning.png";
	private static final String ICON_INFO = "include/icons/dialog-info.png";

	private static NotezOption option;

	public enum NotezOption
	{
		YES, NO, CANCEL, CLOSE;
	}

	public static NotezOption showInfoDialog(Stage parent, String title,
					String msg) throws IOException, InterruptedException
	{

		return showDialog(parent, title, msg, ICON_INFO);
	}

	public static NotezOption showWarningDialog(Stage parent, String title,
					String msg) throws IOException, InterruptedException
	{

		return showDialog(parent, title, msg, ICON_WARNING);
	}

	public static NotezOption showErrorDialog(Stage parent, String title,
					String msg) throws IOException, InterruptedException
	{
		return showDialog(parent, title, msg, ICON_ERROR);
	}

	public static NotezOption showDialog(Stage parent, String title,
					String msg, String icon) throws IOException,
		InterruptedException
	{
		Stage stage = new Stage();
		FXMLLoader loader = new FXMLLoader(
			NotezFileUtil.getResourceURL(NotezDialog.FXML_PATH));

		loader.setController(new NotezDialogController(stage, title,
			msg, new Image(NotezFileUtil.getResourceStream(icon))));
		stage.setScene(new Scene(loader.load()));
		setModality(stage, parent);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.showAndWait();

		return option;
	}

	private static void setModality(Stage stage, Stage parent)
	{
		if(parent != null && parent.isShowing())
		{
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(parent.getScene().getWindow());
			relativeToOwner(stage, parent);
		}
	}

	private static void relativeToOwner(Stage stage, Stage owner)
	{
		// TODO set default width and height

		// stage.setX(owner.getX());
		// stage.setY((owner.getY() + (owner.getHeight() / 2)));
		//
		// System.out.println(stage.getHeight());
		// System.out.println(owner.getHeight());
	}

	static class NotezDialogController
	{
		@FXML
		private ToolBar toolBar;
		@FXML
		private Button btnClose;
		@FXML
		private Label lblTitle;
		@FXML
		private Label lblMsg;
		@FXML
		private ImageView icon;

		private Stage stage;
		private Image imgIcon;
		private String title;
		private String msg;
		private double initialX;
		private double initialY;

		public NotezDialogController(Stage stage, String title,
										String msg, Image imgIcon)
		{
			this.stage = stage;
			this.imgIcon = imgIcon;
			this.title = title;
			this.msg = msg;
		}

		@FXML
		public void initialize()
		{
			loadIcons();
			addDraggableNode(toolBar);
			lblTitle.setText(title);
			lblMsg.setText(msg);
		}

		private void loadIcons()
		{
			btnClose.setGraphic(new ImageView(new Image(
				NotezFileUtil.getResourceStream(NotezController.ICON_CLOSE))));
			// icon.setImage(imgIcon);
			lblMsg.setGraphic(new ImageView(imgIcon));
		}

		@FXML
		public void close()
		{
			NotezDialog.option = NotezOption.CLOSE;
			stage.close();
		}

		@FXML
		public void yes()
		{
			NotezDialog.option = NotezOption.YES;
			stage.close();
		}

		@FXML
		public void no()
		{

			NotezDialog.option = NotezOption.NO;
			stage.close();
		}

		@FXML
		public void cancel()
		{

			NotezDialog.option = NotezOption.CANCEL;
			stage.close();
		}

		private void addDraggableNode(final Node node)
		{
			node.setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent me)
				{
					if(me.getButton() != MouseButton.MIDDLE)
					{
						initialX = me.getSceneX();
						initialY = me.getSceneY();
					}
				}
			});

			node.setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent me)
				{
					if(me.getButton() != MouseButton.MIDDLE)
					{
						node.getScene()
							.getWindow()
							.setX(me.getScreenX() - initialX);
						node.getScene()
							.getWindow()
							.setY(me.getScreenY() - initialY);
					}
				}
			});
		}
	}
}