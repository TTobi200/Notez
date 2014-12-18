/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.gui;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.sun.javafx.application.PlatformImpl;

import de.gui.comp.NotezButtonBar;
import de.gui.comp.NotezSettingsPane;
import de.gui.comp.NotezTextPane;
import de.notez.data.NotezData;
import de.notez.data.NotezDataProperties;
import de.notez.data.base.BaseNotezDataProperties;
import de.util.NotezDataUtil;
import de.util.NotezFileUtil;

public class NotezNote
{
	public static final String ICON_LOGO = "include/icons/logo.png";
	public static final String FXML_PATH = "include/fxml/NotezGui.fxml";
	public static final String NOTEZ_LOGO = "include/icons/logo.png";

	private static ObservableList<NotezNote> notes = FXCollections.observableArrayList();

	public static ObservableList<NotezNote> notezList()
	{
		return FXCollections.unmodifiableObservableList(notes);
	}

	private Stage stage;

	private NotezDataProperties data;
	
	@FXML
	private NotezButtonBar btns;
	@FXML
	private NotezSettingsPane settings;
	@FXML
	private NotezTextPane text;
	
	@FXML
	private StackPane stack;

	public NotezNote(File file) throws IOException
	{
		FXMLLoader loader = new FXMLLoader(
			NotezFileUtil.getResourceURL(FXML_PATH));
		loader.setController(this);
		BorderPane root = loader.load();
		Scene scene = new Scene(root);
		
		PlatformImpl.runAndWait(() ->
		{
			stage = new Stage(StageStyle.UNDECORATED);
			
			
			// XXX Add this to gain drop shadow (1/2)
			// Group g = new Group();
			// Scene scene = new Scene(g);
			// scene.setFill(null);
			// root.setPadding(new Insets(10, 10, 10, 10));
			// root.setEffect(new DropShadow());

			stage.setScene(scene);
			// Fixed set height/width needed for dialogs (relative to)
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.getIcons().add(
				new Image(NotezFileUtil.getResourceStream(NOTEZ_LOGO)));
			// stage.show();

			// XXX Add this to gain drop shadow (2/2)
			// g.getChildren().add(root);
		});

		notes.add(this);
		
        data = new BaseNotezDataProperties(
            NotezFileUtil.removeEnding(file.getName()));
        
        Collection<NotezComponent> comps = new HashSet<>();
        comps.add(btns);
        comps.add(settings);
        comps.add(text);
        
        for(NotezComponent comp : comps)
        {
        	comp.setNote(this);
        	comp.setListener();
        }
        
        stack.getChildren().remove(1);
	}

	public String getTitle()
	{
		return getData().getTitle();
	}

	public void show()
	{
		getStage().show();
	}

	public void hide()
	{
		getStage().hide();
	}

	public void delete()
	{
		notes.remove(this);
	}

	public void loadData(NotezData data)
	{
		NotezDataUtil.equalize(data, getData());
	}

	public NotezDataProperties getData()
	{
		return data;
	}

	public Stage getStage()
	{
		return stage;
	}
}
