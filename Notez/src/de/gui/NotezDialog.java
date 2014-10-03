/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.gui;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class NotezDialog
{
	public static final String FXML_PATH = "./include/fxml/NotezDialog.fxml";
	public static final double DEF_WIDTH = 299d;
	public static final double DEF_HEIGTH = 212d;

	private static File fxmlFile;
	private static FXMLLoader loader;

	public static NotezOption option;
	private static Parent root;
	private static Stage stage;

	public enum NotezOption
	{
		YES, NO, CANCEL, CLOSE;
	}

	static
	{
		try
		{
			stage = new Stage();
			fxmlFile = new File(NotezDialog.class.getClassLoader().getResource(
				FXML_PATH).getFile());

			loader = new FXMLLoader(
				new File(fxmlFile.getAbsolutePath()
				).toURI().toURL());
			loader.setController(new NotezDialogController(stage));
			root = loader.<Parent> load();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public static NotezDialog showErrorDialog(Stage parent) throws IOException
	{
		Scene scene = new Scene(root);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.initOwner(parent);
		stage.setScene(scene);
		stage.setHeight(DEF_HEIGTH);
		stage.setWidth(DEF_WIDTH);
		stage.show();

		return null;
	}

	public static NotezOption getOption()
	{
		return option;
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

		private Stage stage;

		public NotezDialogController(Stage stage)
		{
			this.stage = stage;
		}

		@FXML
		public void initialized()
		{
			loadIcons();
		}

		private void loadIcons()
		{
			btnClose.setGraphic(new ImageView(new Image(
				getClass().getResourceAsStream(NotezController.ICON_CLOSE))));
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
	}
}