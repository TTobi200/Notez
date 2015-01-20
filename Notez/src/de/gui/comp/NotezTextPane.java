package de.gui.comp;

import java.io.*;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.HTMLEditor;
import de.gui.NotezGui;
import de.notez.NotezSystem;
import de.util.NotezFileUtil;

public class NotezTextPane extends BorderPane implements NotezComponent
{
	public static final String FXML = "NotezTextPane.fxml";

	@FXML
	private HTMLEditor txt;

	@FXML
	private Button btnPrevPage;

	@FXML
	private Button btnNextPage;

	@FXML
	private Label lblPage;

	public NotezTextPane() throws IOException
	{
		if(!NotezSystem.isRunningInSceneBuilder())
		{
			FXMLLoader loader = new FXMLLoader(
				NotezFileUtil.getResourceURL(NotezFileUtil.FXML_FOLDER
												+ File.separator + FXML));
			loader.setRoot(this);
			loader.setController(this);

			loader.load();
		}
	}

	@FXML
	private void prevPage()
	{
		getNote().getData().getPageData().prevPage();
	}

	@FXML
	private void nextPage()
	{
		getNote().getData().getPageData().nextPage();
	}

	protected NotezGui gui;

	@Override
	public void setGui(NotezGui gui)
	{
		this.gui = gui;
	}

	@Override
	public NotezGui getGui()
	{
		return gui;
	}

	private StringProperty textProp;

	@Override
	public void setListener()
	{
		textProp = new SimpleStringProperty(txt.getHtmlText());
		textProp.addListener((p, o, n) -> Platform.runLater(() -> txt.setHtmlText(n)));
		// XXX only a workaround
		txt.setOnMouseExited(event -> textProp.set(txt.getHtmlText()));
		// XXX works but the htmlview looses its focus
		// txt.setOnKeyReleased(event -> textProp.set(txt.getHtmlText()));

		lblPage.textProperty().bind(
			Bindings.concat(getNote().getData()
				.getPageData()
				.curPageIndexProperty()
				.add(1), " / ",
				getNote().getData().getPageData().sizeProperty()));

		btnPrevPage.disableProperty().bind(
			getNote().getData()
				.getPageData()
				.curPageIndexProperty()
				.isEqualTo(0));
		btnNextPage.disableProperty().bind(
			getNote().getData()
				.getPageData()
				.curPageIndexProperty()
				.isEqualTo(
					getNote().getData()
						.getPageData()
						.sizeProperty()
						.subtract(1))
				.and(textProp.isEmpty()));

		txt.setHtmlText(getNote().getData().getPageData().getText());
		textProp.bindBidirectional(getNote().getData()
			.getPageData()
			.textProperty());
	}
}
