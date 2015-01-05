package de.gui.comp;

import static de.notez.NotezProperties.NOTEZ_ALWAYS_SAVE_ON_EXIT;
import static de.notez.NotezProperties.NOTEZ_BTN_ADD;
import static de.notez.NotezProperties.NOTEZ_BTN_GROUP;
import static de.notez.NotezProperties.NOTEZ_BTN_PIN;
import static de.notez.NotezProperties.NOTEZ_BTN_PRINT;
import static de.notez.NotezProperties.NOTEZ_BTN_REMOVE;
import static de.notez.NotezProperties.NOTEZ_BTN_SAVE;
import static de.notez.NotezProperties.NOTEZ_BTN_SHARE;
import static de.notez.NotezProperties.NOTEZ_LET_RECEIVER_RUNNING;
import static de.notez.NotezProperties.NOTEZ_MAIL_HOST;
import static de.notez.NotezProperties.NOTEZ_MAIL_PORT;
import static de.notez.NotezProperties.NOTEZ_MAIL_USER;
import static de.notez.NotezProperties.NOTEZ_MAIL_USE_SSL;
import static de.notez.NotezProperties.NOTEZ_RECEIVER_ON_STARTUP;
import static de.notez.NotezProperties.NOTEZ_REMOTE_FOLDER;
import static de.notez.NotezProperties.NOTEZ_WORK_FOLDER;
import static de.notez.NotezProperties.get;

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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import de.gui.NotezDialog;
import de.gui.NotezGui;
import de.gui.NotezGui.NotezGuiBody;
import de.notez.NotezProperties;
import de.notez.NotezRemoteSync;
import de.notez.NotezRemoteSync.NotezRemoteUser;
import de.util.NotezFileUtil;
import de.util.NotezSystemUtil;
import de.util.log.NotezLog;

public class NotezSettingsPane extends BorderPane implements NotezComponent
{
	public static final String FXML = "NotezSettingsPane.fxml";

	@FXML
	private CheckBox cbUseSSL;

	@FXML
	private TableView<NotezRemoteUser> tableRemoteuser;

	@FXML
	private TitledPane tPaneFolder;

	@FXML
	private TitledPane tPaneButton;

	@FXML
	private TitledPane tPaneSync;

	@FXML
	private TitledPane tPaneShareUser;

	@FXML
	private TableColumn<NotezRemoteUser, String> colShare;

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
	private TableColumn<NotezRemoteUser, String> colUsername;

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
		if (!NotezSystemUtil.isRunningInSceneBuilder())
		{
			FXMLLoader loader = new FXMLLoader(
					NotezFileUtil.getResourceURL(NotezFileUtil.FXML_FOLDER + File.separator + FXML));
			loader.setRoot(this);
			loader.setController(this);

			loader.load();
		}
	}

	@FXML
	private void saveSettings()
	{
		NotezProperties.set(NotezProperties.NOTEZ_WORK_FOLDER, txtPropNotezWorkFold.getText());
		NotezProperties.set(NotezProperties.NOTEZ_REMOTE_FOLDER, txtPropNotezRemoteFold.getText());

		NotezProperties.set(NotezProperties.NOTEZ_MAIL_USER, txtEmail.getText());
		NotezProperties.set(NotezProperties.NOTEZ_MAIL_HOST, txtHost.getText());
		NotezProperties.set(NotezProperties.NOTEZ_MAIL_PORT, txtPort.getText());

		// TODO only switch if valid?
		getGui().switchToBody(NotezGuiBody.TEXT);
	}

	@FXML
	private void cancelSettings()
	{
		// Restore default values
		updateSettings();
		tableRemoteuser.setItems(NotezRemoteSync.getAllUsers());

		getGui().switchToBody(NotezGuiBody.TEXT);
	}

	@FXML
	private void deleteUser()
	{
		NotezRemoteUser user = tableRemoteuser.getSelectionModel().getSelectedItem();
		if (user != null)
		{
			try
			{
				switch(NotezDialog.showQuestionDialog(getGui(), "Delete User",
					"Do you really want to delete user " + user.getUsername() + " ?"))
				{
					case NO:
					case CLOSE:
					case CANCEL:
					default:
						break;

					case OK:
					case YES:
						NotezRemoteSync.removeUser(user);
						break;
				}
			}
			catch(IOException | InterruptedException e)
			{
				NotezLog.error("error while asking the user for deleting a remote user", e);
			}
		}
	}

	@FXML
	private void addNewUser()
	{
		NotezRemoteUser user = null;
		try
		{
			user = NotezDialog.showAddUserDialog(getGui());
		}
		catch(IOException | InterruptedException e)
		{
			NotezLog.error("couls not add a new user", e);
		}

		if (user != null && user.getUsername() != null && user.getShare() != null)
		{
			NotezRemoteSync.addUser(user);
		}
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
		tableRemoteuser.setItems(NotezRemoteSync.getAllUsers());
		colUsername.setCellValueFactory(new PropertyValueFactory<NotezRemoteUser, String>(
				"username"));
		colShare.setCellValueFactory(new PropertyValueFactory<NotezRemoteUser, String>("share"));
	}

	public void updateSettings()
	{
		txtPropNotezWorkFold.setText(get(NOTEZ_WORK_FOLDER));
		txtPropNotezRemoteFold.setText(get(NOTEZ_REMOTE_FOLDER));

		txtEmail.setText(get(NOTEZ_MAIL_USER));
		txtHost.setText(get(NOTEZ_MAIL_HOST));
		txtPort.setText(get(NOTEZ_MAIL_PORT));

		// Button invisibility
		// cbAddNotez.selectedProperty().bindBidirectional(btnAdd.visibleProperty());
		// cbPinNotez.selectedProperty().bindBidirectional(btnPin.visibleProperty());
		// cbRemoveNotez.selectedProperty().bindBidirectional(btnDelete.visibleProperty());
		// cbSaveNotez.selectedProperty().bindBidirectional(btnSave.visibleProperty());
		// cbShareNotez.selectedProperty().bindBidirectional(btnShare.visibleProperty());
		// cbGroupNotez.selectedProperty().bindBidirectional(pickNote.visibleProperty());
		// cbPrintNotez.selectedProperty().bindBidirectional(btnPrint.visibleProperty());

		// Bin check boxes <-> settings
		NotezProperties.bindBoolean(NOTEZ_RECEIVER_ON_STARTUP,
			cbStartRecOnStartup.selectedProperty());
		NotezProperties.bindBoolean(NOTEZ_LET_RECEIVER_RUNNING,
			cbStartRecKeepRun.selectedProperty());
		NotezProperties
				.bindBoolean(NOTEZ_ALWAYS_SAVE_ON_EXIT, cbAlwaysAskToSave.selectedProperty());
		NotezProperties.bindBoolean(NOTEZ_BTN_PIN, cbPinNotez.selectedProperty());
		NotezProperties.bindBoolean(NOTEZ_BTN_GROUP, cbGroupNotez.selectedProperty());
		NotezProperties.bindBoolean(NOTEZ_BTN_SHARE, cbShareNotez.selectedProperty());
		NotezProperties.bindBoolean(NOTEZ_BTN_ADD, cbAddNotez.selectedProperty());
		NotezProperties.bindBoolean(NOTEZ_BTN_SAVE, cbSaveNotez.selectedProperty());
		NotezProperties.bindBoolean(NOTEZ_BTN_REMOVE, cbRemoveNotez.selectedProperty());
		NotezProperties.bindBoolean(NOTEZ_BTN_PRINT, cbPrintNotez.selectedProperty());
		NotezProperties.bindBoolean(NOTEZ_MAIL_USE_SSL, cbUseSSL.selectedProperty());
		NotezProperties.bindBoolean(NOTEZ_BTN_PIN, cbPinNotez.selectedProperty());
		NotezProperties.bindBoolean(NOTEZ_BTN_GROUP, cbGroupNotez.selectedProperty());
		NotezProperties.bindBoolean(NOTEZ_BTN_SHARE, cbShareNotez.selectedProperty());
		NotezProperties.bindBoolean(NOTEZ_BTN_ADD, cbAddNotez.selectedProperty());
		NotezProperties.bindBoolean(NOTEZ_BTN_SAVE, cbSaveNotez.selectedProperty());
		NotezProperties.bindBoolean(NOTEZ_BTN_REMOVE, cbRemoveNotez.selectedProperty());
		NotezProperties.bindBoolean(NOTEZ_BTN_PRINT, cbPrintNotez.selectedProperty());
	}

	public void switchToTab(NotezSettingsPaneTab tab)
	{
		getGui().switchToBody(NotezGuiBody.SETTINGS);
		
		switch(tab)
		{
			case LOCAL:
			{
				tabSettings.getSelectionModel().select(tabLocal);
				break;
			}
			case REMOTE:
			{
				tabSettings.getSelectionModel().select(tabRemote);
				break;
			}
		}
	}

	public void switchToPane(NotezSettingsPaneTabPane pane)
	{
		switchToTab(pane.tab);

		tPaneEMail.setExpanded(false);
		tPaneShareUser.setExpanded(false);
		tPaneSync.setExpanded(false);
		tPaneButton.setExpanded(false);
		tPaneFolder.setExpanded(false);
		
		switch(pane)
		{
			case EMAIL:
			{
				tPaneEMail.setExpanded(true);
				break;
			}
			case SHARE:
			{
				tPaneShareUser.setExpanded(true);
				break;
			}
			case SYNC:
			{
				tPaneSync.setExpanded(true);
				break;
			}
			case BUTTON:
			{
				tPaneButton.setExpanded(true);
				break;
			}
			case FOLDER:
			{
				tPaneFolder.setExpanded(true);
				break;
			}
		}
	}

	public static enum NotezSettingsPaneTab
	{
		LOCAL, REMOTE;
	}

	public static enum NotezSettingsPaneTabPane
	{
		FOLDER(NotezSettingsPaneTab.LOCAL), BUTTON(NotezSettingsPaneTab.LOCAL), SYNC(
				NotezSettingsPaneTab.REMOTE), EMAIL(NotezSettingsPaneTab.REMOTE), SHARE(
				NotezSettingsPaneTab.REMOTE);

		public final NotezSettingsPaneTab tab;

		private NotezSettingsPaneTabPane(NotezSettingsPaneTab tab)
		{
			this.tab = tab;
		}
	}
}
