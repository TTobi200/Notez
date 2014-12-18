package de.gui.comp;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import de.gui.NotezComponent;
import de.gui.NotezNote;
import de.util.NotezFileUtil;
import de.util.NotezSystemUtil;

public class NotezSettingsPane extends BorderPane implements NotezComponent
{
	public static final String FXML = "NotezSettingsPane.fxml";

	@FXML
	private CheckBox cbUseSSL;

	@FXML
	private TableView<?> tableRemoteuser;

	@FXML
	private TitledPane tPaneShareUser;

	@FXML
	private TableColumn<?, ?> colShare;

	@FXML
	private CheckBox cbStartRecKeepRun;

	@FXML
	private TitledPane tPaneEMail;

	@FXML
	private CheckBox cbGroupNotez;

	@FXML
	private TabPane tabSettings;

	@FXML
	private Tab tabRemote;

	@FXML
	private TableColumn<?, ?> colUsername;

	@FXML
	private CheckBox cbRemoveNotez;

	@FXML
	private CheckBox cbSaveNotez;

	@FXML
	private CheckBox cbShareNotez;

	@FXML
	private TextField txtHost;

	@FXML
	private CheckBox cbPinNotez;

	@FXML
	private CheckBox cbStartRecOnStartup;

	@FXML
	private TextField txtEmail;

	@FXML
	private Button btnCancelSettings;

	@FXML
	private TextField txtPort;

	@FXML
	private TextField txtPropNotezWorkFold;

	@FXML
	private Tab tabLocal;

	@FXML
	private Button btnAddUser;

	@FXML
	private CheckBox cbAlwaysAskToSave;

	@FXML
	private CheckBox cbPrintNotez;

	@FXML
	private Button btnSaveSettings;

	@FXML
	private TextField txtPropNotezRemoteFold;

	@FXML
	private CheckBox cbAddNotez;

	@FXML
	private Button btnDeleteUser;

	public NotezSettingsPane() throws IOException
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
	void saveSettings()
	{

	}

	@FXML
	void cancelSettings()
	{

	}

	@FXML
	void deleteUser()
	{

	}

	@FXML
	void addNewUser()
	{

	}
	
	protected NotezNote note;

	@Override
	public void setNote(NotezNote note)
	{
		this.note = note;
	}

	@Override
	public NotezNote getNote()
	{
		return note;
	}
	
	@Override
	public void setListener()
	{
		// TODO Auto-generated method stub
		
	}
}
