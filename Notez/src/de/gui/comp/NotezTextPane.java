package de.gui.comp;

import java.io.File;
import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import de.gui.NotezGui;
import de.util.NotezFileUtil;
import de.util.NotezSystemUtil;

public class NotezTextPane extends BorderPane implements NotezComponent
{
	public static final String FXML = "NotezTextPane.fxml";

	@FXML
	private TextArea txt;

	@FXML
	private Button btnPrevPage;

	@FXML
	private Button btnNextPage;

	@FXML
	private Label lblPage;

	public NotezTextPane() throws IOException
	{
		if(!NotezSystemUtil.isRunningInSceneBuilder())
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

	public TextArea getTxt()
	{
		return txt;
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

	@Override
	public void setListener()
	{
		lblPage.textProperty().bind(
			Bindings.concat(
				getNote().getData().getPageData().curPageIndexProperty().add(1),
				" / ",
				getNote().getData().getPageData().sizeProperty()));

		btnPrevPage.disableProperty().bind(getNote().getData().getPageData().curPageIndexProperty().isEqualTo(0));
		btnNextPage.disableProperty().bind(
			getNote().getData().getPageData().curPageIndexProperty().isEqualTo(
				getNote().getData().getPageData().sizeProperty()
				.subtract(1))
			.and(txt.textProperty().isEmpty()));

		txt.setText(getNote().getData().getPageData().getText());
		txt.textProperty().bindBidirectional(getNote().getData().getPageData().textProperty());
	}
}
