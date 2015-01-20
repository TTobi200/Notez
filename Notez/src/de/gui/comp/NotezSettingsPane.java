package de.gui.comp;

import static de.notez.prop.NotezProperties.*;

import java.io.*;
import java.util.*;

import javafx.beans.property.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import de.gui.*;
import de.gui.NotezGui.NotezGuiBody;
import de.notez.*;
import de.notez.NotezRemoteSync.NotezRemoteUser;
import de.notez.prop.NotezProperties;
import de.util.*;
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

	private List<PropertyBinding<?, ?>> propertyBindings;

	public NotezSettingsPane() throws IOException
	{
		if(!NotezSystemUtil.isRunningInSceneBuilder())
		{
			FXMLLoader loader = new FXMLLoader(
				NotezFileUtil.getResourceURL(NotezFileUtil.FXML_FOLDER + File.separator + FXML));
			loader.setRoot(this);
			loader.setController(this);

			loader.load();

			propertyBindings = new ArrayList<>();
		}
	}

	@FXML
	private void saveSettings()
	{
		NotezSystemUtil.getSystemProperties().putString(NotezProperties.NOTEZ_WORK_FOLDER,
			txtPropNotezWorkFold.getText());
		NotezSystemUtil.getSystemProperties().putString(NotezProperties.NOTEZ_REMOTE_FOLDER,
			txtPropNotezRemoteFold.getText());

		NotezSystemUtil.getSystemProperties().putString(NotezProperties.NOTEZ_MAIL_USER,
			txtEmail.getText());
		NotezSystemUtil.getSystemProperties().putString(NotezProperties.NOTEZ_MAIL_HOST,
			txtHost.getText());
		NotezSystemUtil.getSystemProperties().putString(NotezProperties.NOTEZ_MAIL_PORT,
			txtPort.getText());

		propertyBindings.forEach(PropertyBinding::onSave);
		
		NotezSystemUtil.getSystemProperties().save();
		getGui().switchToBody(NotezGuiBody.TEXT);
	}

	@FXML
	private void cancelSettings()
	{
		// Restore default values
		updateSettings();

		getGui().switchToBody(NotezGuiBody.TEXT);
	}

	@FXML
	private void deleteUser()
	{
		NotezRemoteUser user = tableRemoteuser.getSelectionModel().getSelectedItem();
		if(user != null)
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

		if(user != null && user.getUsername() != null && user.getShare() != null)
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

		propertyBindings.add(new StringPropertyBinding(NOTEZ_WORK_FOLDER,
			txtPropNotezWorkFold.textProperty()));
		propertyBindings.add(new StringPropertyBinding(NOTEZ_REMOTE_FOLDER,
			txtPropNotezRemoteFold.textProperty()));

		propertyBindings.add(new StringPropertyBinding(NOTEZ_MAIL_USER, txtEmail.textProperty()));
		propertyBindings.add(new StringPropertyBinding(NOTEZ_MAIL_HOST, txtHost.textProperty()));
		propertyBindings.add(new StringPropertyBinding(NOTEZ_MAIL_PORT, txtPort.textProperty()));

		propertyBindings.add(new BooleanPropertyBinding(NOTEZ_RECEIVER_ON_STARTUP,
			cbStartRecOnStartup.selectedProperty()));
		propertyBindings.add(new BooleanPropertyBinding(NOTEZ_LET_RECEIVER_RUNNING,
			cbStartRecKeepRun.selectedProperty()));
		propertyBindings.add(new BooleanPropertyBinding(NOTEZ_ALWAYS_SAVE_ON_EXIT,
			cbAlwaysAskToSave.selectedProperty()));

		propertyBindings.add(new BooleanPropertyBinding(NOTEZ_BTN_PIN,
			cbPinNotez.selectedProperty()));
		propertyBindings.add(new BooleanPropertyBinding(NOTEZ_BTN_GROUP,
			cbGroupNotez.selectedProperty(), false));
		propertyBindings.add(new BooleanPropertyBinding(NOTEZ_BTN_SHARE,
			cbShareNotez.selectedProperty()));
		propertyBindings.add(new BooleanPropertyBinding(NOTEZ_BTN_ADD,
			cbAddNotez.selectedProperty()));
		propertyBindings.add(new BooleanPropertyBinding(NOTEZ_BTN_SAVE,
			cbSaveNotez.selectedProperty()));
		propertyBindings.add(new BooleanPropertyBinding(NOTEZ_BTN_REMOVE,
			cbRemoveNotez.selectedProperty()));
		propertyBindings.add(new BooleanPropertyBinding(NOTEZ_BTN_PRINT,
			cbPrintNotez.selectedProperty()));
		propertyBindings.add(new BooleanPropertyBinding(NOTEZ_MAIL_USE_SSL,
			cbUseSSL.selectedProperty(), false));
	}

	public void updateSettings()
	{
		propertyBindings.forEach(PropertyBinding::onUpdate);

		tableRemoteuser.setItems(NotezRemoteSync.getAllUsers());

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
		switchToTab(pane.TAB);

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

	/**
	 * enum representing the different tabs of a settings pane
	 * 
	 * @author ddd
	 */
	public static enum NotezSettingsPaneTab
	{
		/** Tab showing all local/general settings of a notezgui */
		LOCAL,
		/** Tab for showing all sharing-related settings of a notezgui */
		REMOTE;
	}

	/**
	 * enum representing the different pane possibly showing in a settings pane
	 * 
	 * @author ddd
	 */
	public static enum NotezSettingsPaneTabPane
	{
		/**
		 * Pane showing folder-related settings in the
		 * {@link NotezSettingsPaneTab#LOCAL local}-tab
		 */
		FOLDER(NotezSettingsPaneTab.LOCAL),
		/**
		 * Pane showing button-related settings in the
		 * {@link NotezSettingsPaneTab#LOCAL local}-tab
		 */
		BUTTON(NotezSettingsPaneTab.LOCAL),
		/**
		 * Pane showing synchronization-related settings in the
		 * {@link NotezSettingsPaneTab#REMOTE remote}-tab
		 */
		SYNC(NotezSettingsPaneTab.REMOTE),
		/**
		 * Pane showing eMail-related settings in the
		 * {@link NotezSettingsPaneTab#REMOTE remote}-tab
		 */
		EMAIL(NotezSettingsPaneTab.REMOTE),
		/**
		 * Pane showing sharing-related settings in the
		 * {@link NotezSettingsPaneTab#REMOTE remote}-tab
		 */
		SHARE(NotezSettingsPaneTab.REMOTE);

		/** The tab this pane is positioned in */
		public final NotezSettingsPaneTab TAB;

		private NotezSettingsPaneTabPane(NotezSettingsPaneTab tab)
		{
			this.TAB = tab;
		}
	}

	/**
	 * An internal binding of a property in this pane to a given systemproperty
	 * 
	 * @param <T> The type of the property used
	 */
	private abstract class PropertyBinding<T, P extends Property<T>>
	{
		/**  The default parameter for the property */
		protected T def;
		/** the property showing the systemproperty */
		protected P prop;
		/** The string representing the systemproperty */
		protected String propertyString;

		public PropertyBinding(String propertyString, P prop, T def)
		{
			this.propertyString = propertyString;
			this.prop = prop;
			this.def = def;
		}

		/**
		 * Save the value stored in the given property in the systemproperty of the given String
		 */
		public abstract void onSave();

		/**
		 * Load the value of the given systemproperty into the given property.
		 */
		public abstract void onUpdate();
	}

	/**
	 * A special propertybinding for Strings
	 */
	private class StringPropertyBinding extends PropertyBinding<String, StringProperty>
	{
		public StringPropertyBinding(String propertyString, StringProperty prop)
		{
			this(propertyString, prop, "");
		}
		
		public StringPropertyBinding(String propertyString, StringProperty prop, String def)
		{
			super(propertyString, prop, def);
		}

		@Override
		public void onSave()
		{
			NotezSystemUtil.getSystemProperties().putString(propertyString, prop.get());
		}

		@Override
		public void onUpdate()
		{
			prop.set(NotezSystemUtil.getSystemProperties().getString(propertyString, ""));
		}
	}

	/**
	 * A special propertybinding for boolean
	 */
	private class BooleanPropertyBinding extends PropertyBinding<Boolean, BooleanProperty>
	{
		public BooleanPropertyBinding(String propertyString, BooleanProperty prop)
		{
			this(propertyString, prop, true);
		}

		public BooleanPropertyBinding(String propertyString, BooleanProperty prop, boolean def)
		{
			super(propertyString, prop, def);
		}

		@Override
		public void onSave()
		{
			NotezSystemUtil.getSystemProperties().putBoolean(propertyString, prop.get());
		}

		@Override
		public void onUpdate()
		{
			prop.set(NotezSystemUtil.getSystemProperties().getBoolean(propertyString, def));
		}
	}
}
