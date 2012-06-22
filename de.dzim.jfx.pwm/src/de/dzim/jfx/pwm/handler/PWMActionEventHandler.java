package de.dzim.jfx.pwm.handler;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

import javax.xml.bind.JAXBException;

import de.dzim.jfx.pwm.model.container.PWMContainer;
import de.dzim.jfx.pwm.model.container.PWMContainerGroup;
import de.dzim.jfx.pwm.model.content.PWMGroup;
import de.dzim.jfx.pwm.model.content.PWMGroupEntry;
import de.dzim.jfx.pwm.ui.component.EditorWindow;
import de.dzim.jfx.pwm.ui.component.MainWindow;
import de.dzim.jfx.pwm.ui.dialog.EntryDialog;
import de.dzim.jfx.pwm.ui.dialog.GroupDialog;
import de.dzim.jfx.pwm.util.PWMUtils;
import de.dzim.jfx.ui.dialog.MessageDialog;
import de.dzim.jfx.util.InternalAdapter;

/**
 * Used for global actions (exit the app, open a file, ...).
 * 
 * @author dzimmermann
 * 
 */
public class PWMActionEventHandler implements EventHandler<ActionEvent> {

	public enum Type {
		// file
		NEW, EXIT, OPEN, SAVE, SAVE_AS,
		// group
		ADD_GROUP, ADD_SUB_GROUP, EDIT_GROUP, REMOVE_GROUP,
		// entry
		ADD_ENTRY, EDIT_ENTRY, REMOVE_ENTRY,
		// other
		OPEN_URL, CLIPBOARD_URL, CLIPBOARD_USER, CLIPBOARD_PASSWORD;
	}

	protected final Type type;

	protected InternalAdapter adapter;

	public PWMActionEventHandler(Type type) {
		this.type = type;
	}

	public PWMActionEventHandler(Type type, InternalAdapter adapter) {
		this(type);
		this.adapter = adapter;
	}

	public void setAdapter(InternalAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void handle(ActionEvent event) {
		switch (type) {
		case EXIT:
			handleExit(event);
			break;
		case NEW:
			handleNew(event);
			break;
		case OPEN:
			handleOpen(event);
			break;
		case SAVE:
			handleSave(event);
			break;
		case SAVE_AS:
			handleSaveAs(event);
			break;
		case ADD_GROUP:
			handleAddGroup(event);
			break;
		case ADD_SUB_GROUP:
			handleAddSubGroup(event);
			break;
		case EDIT_GROUP:
			handleEditGroup(event);
			break;
		case REMOVE_GROUP:
			handleRemoveGroup(event);
			break;
		case ADD_ENTRY:
			handleAddEntry(event);
			break;
		case EDIT_ENTRY:
			handleEditEntry(event);
			break;
		case REMOVE_ENTRY:
			handleRemoveEntry(event);
			break;
		case OPEN_URL:
			handleOpenURL(event);
			break;
		case CLIPBOARD_URL:
			handleClipboardURL(event);
			break;
		case CLIPBOARD_USER:
			handleClipboardUser(event);
			break;
		case CLIPBOARD_PASSWORD:
			handleClipboardPassword(event);
			break;
		}
	}

	private void handleExit(ActionEvent event) {
		Platform.exit();
	}

	private void handleNew(ActionEvent event) {

		File old_containerFile = (File) adapter.getInternalAdapter(File.class);
		PWMContainer old_container = (PWMContainer) adapter
				.getInternalAdapter(PWMContainer.class);

		Window owner = getOwner();

		Boolean dirty = (Boolean) adapter.getInternalAdapter(Boolean.class);
		if (dirty != null && dirty) {
			boolean confirm = MessageDialog
					.showQuestion(owner, "PWM - Save Changes?",
							"There are unfinished changes.\nDo you want to save them first?");
			if (confirm) {
				try {
					PWMUtils.savePWMContainer(old_containerFile, old_container);
					de.dzim.jfx.event.EventHandler.getInstance().fireEvent(
							old_containerFile, MainWindow.NOT_DIRTY);
					de.dzim.jfx.event.EventHandler.getInstance().fireEvent(
							old_containerFile, MainWindow.UPDATE_FILE);
				} catch (Exception e) {
					e.printStackTrace();
					String message = String
							.format("An error occured on saving a database:\n\n%s\n\nDo you want to continue?",
									e.getMessage());
					boolean _continue = MessageDialog.showQuestion(owner,
							"PWM - Error", message);
					if (!_continue)
						return;
				}
			}
		}

		FileChooser chooser = new FileChooser();
		chooser.setTitle("Open PWM Database File");
		chooser.getExtensionFilters().addAll(getExtensionFilter());
		if (old_containerFile != null)
			chooser.setInitialDirectory(old_containerFile.getParentFile());
		File containerFile = chooser.showSaveDialog(owner);

		if (containerFile == null)
			return;

		String t = containerFile.getAbsolutePath();
		if (t.toLowerCase().endsWith(PWMUtils.DEFAULT_EXTENSION_BACKUP))
			t = t.substring(0, t.lastIndexOf(PWMUtils.DEFAULT_EXTENSION_BACKUP));
		else if (t.toLowerCase().endsWith(PWMUtils.DEFAULT_EXTENSION))
			t = t.substring(0, t.lastIndexOf(PWMUtils.DEFAULT_EXTENSION));
		containerFile = new File(t + PWMUtils.DEFAULT_EXTENSION);

		try {
			PWMContainer container = new PWMContainer();
			PWMUtils.savePWMContainer(containerFile, container);
			de.dzim.jfx.event.EventHandler.getInstance().fireEvent(
					new Object[] { containerFile, container },
					EditorWindow.UPDATE_DATABASE);
			de.dzim.jfx.event.EventHandler.getInstance().fireEvent(
					containerFile, MainWindow.NOT_DIRTY);
			de.dzim.jfx.event.EventHandler.getInstance().fireEvent(
					containerFile, MainWindow.UPDATE_FILE);
		} catch (Exception e) {
			e.printStackTrace();
			String message = String.format(
					"An error occured on creating a new database:\n\n%s",
					e.getMessage());
			MessageDialog.showError(owner, "PWM - Error", message);
		}
	}

	private void handleOpen(ActionEvent event) {

		File old_containerFile = (File) adapter.getInternalAdapter(File.class);

		Window owner = getOwner();

		FileChooser chooser = new FileChooser();
		chooser.setTitle("Open PWM Database File");
		chooser.getExtensionFilters().addAll(getExtensionFilter());
		if (old_containerFile != null)
			chooser.setInitialDirectory(old_containerFile.getParentFile());
		File containerFile = chooser.showOpenDialog(owner);

		if (containerFile == null)
			return;

		try {
			PWMContainer container = PWMUtils.loadPWMContainer(containerFile);
			de.dzim.jfx.event.EventHandler.getInstance().fireEvent(
					new Object[] { containerFile, container },
					EditorWindow.UPDATE_DATABASE);
		} catch (Exception e) {
			e.printStackTrace();
			String message = String.format(
					"An error occured on opening a database:\n\n%s",
					e.getMessage());
			MessageDialog.showError(owner, "PWM - Error", message);
		}
	}

	private void handleSave(ActionEvent event) {

		// if (true)
		// return;

		File containerFile = (File) adapter.getInternalAdapter(File.class);
		PWMContainer container = (PWMContainer) adapter
				.getInternalAdapter(PWMContainer.class);

		if (containerFile == null || container == null)
			return;

		try {
			PWMUtils.savePWMContainer(containerFile, container);
			de.dzim.jfx.event.EventHandler.getInstance().fireEvent(
					containerFile, MainWindow.NOT_DIRTY);
			de.dzim.jfx.event.EventHandler.getInstance().fireEvent(
					containerFile, MainWindow.UPDATE_FILE);
		} catch (Exception e) {
			e.printStackTrace();
			Window owner = getOwner();
			String message = String.format(
					"An error occured on saving a database:\n\n%s",
					e.getMessage());
			MessageDialog.showError(owner, "PWM - Error", message);
		}
	}

	private void handleSaveAs(ActionEvent event) {

		File old_containerFile = (File) adapter.getInternalAdapter(File.class);
		PWMContainer container = (PWMContainer) adapter
				.getInternalAdapter(PWMContainer.class);

		Window owner = getOwner();

		FileChooser chooser = new FileChooser();
		chooser.setTitle("Save PWM Database File");
		chooser.getExtensionFilters().addAll(getExtensionFilter());
		if (old_containerFile != null)
			chooser.setInitialDirectory(old_containerFile.getParentFile());
		File containerFile = chooser.showSaveDialog(owner);

		if (containerFile == null || container == null)
			return;

		String t = containerFile.getAbsolutePath();
		if (t.toLowerCase().endsWith(PWMUtils.DEFAULT_EXTENSION_BACKUP))
			t = t.substring(0, t.lastIndexOf(PWMUtils.DEFAULT_EXTENSION_BACKUP));
		else if (t.toLowerCase().endsWith(PWMUtils.DEFAULT_EXTENSION))
			t = t.substring(0, t.lastIndexOf(PWMUtils.DEFAULT_EXTENSION));
		containerFile = new File(t + PWMUtils.DEFAULT_EXTENSION);

		try {
			PWMUtils.savePWMContainer(containerFile, container);
			de.dzim.jfx.event.EventHandler.getInstance().fireEvent(
					containerFile, MainWindow.NOT_DIRTY);
			de.dzim.jfx.event.EventHandler.getInstance().fireEvent(
					containerFile, MainWindow.UPDATE_FILE);
		} catch (Exception e) {
			e.printStackTrace();
			String message = String.format(
					"An error occured on saving a database:\n\n%s",
					e.getMessage());
			MessageDialog.showError(owner, "PWM - Error", message);
		}
	}

	private void handleAddGroup(ActionEvent event) {

		PWMContainer container = (PWMContainer) adapter
				.getInternalAdapter(PWMContainer.class);

		if (container == null)
			return;

		GroupDialog dialog = new GroupDialog(getOwner(), GroupDialog.Type.ADD,
				null);
		dialog.showDialog();

		if (dialog.getCloseAs() == null || !dialog.getCloseAs())
			return;

		String id = PWMUtils.getPWMID(dialog.getResult().getName(),
				Calendar.getInstance());

		dialog.getResult().setId(id);

		PWMGroup group = new PWMGroup();
		group.setId(id);
		group.setName(dialog.getResult().getName());

		try {
			PWMUtils.savePWMGroup(dialog.getResult(), group,
					dialog.getNewPassword());
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.showError(getOwner(), "PWM - Error",
					"Could not save the Group...");
			return;
		}

		container.addGroup(dialog.getResult());
	}

	private void handleAddSubGroup(ActionEvent event) {

		PWMContainerGroup containerGroup = (PWMContainerGroup) adapter
				.getInternalAdapter(PWMContainerGroup.class);

		if (containerGroup == null)
			return;

		GroupDialog dialog = new GroupDialog(getOwner(),
				GroupDialog.Type.ADD_SUB, containerGroup);
		dialog.showDialog();

		if (dialog.getCloseAs() == null || !dialog.getCloseAs())
			return;

		String id = PWMUtils.getPWMID(dialog.getResult().getName(),
				Calendar.getInstance());

		dialog.getResult().setId(id);

		PWMGroup group = new PWMGroup();
		group.setId(id);
		group.setName(dialog.getResult().getName());

		try {
			PWMUtils.savePWMGroup(dialog.getResult(), group,
					dialog.getNewPassword());
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.showError(getOwner(), "PWM - Error",
					"Could not save the Group...");
			return;
		}

		containerGroup.addGroup(dialog.getResult());
	}

	private void handleEditGroup(ActionEvent event) {

		PWMContainerGroup containerGroup = (PWMContainerGroup) adapter
				.getInternalAdapter(PWMContainerGroup.class);

		if (containerGroup == null)
			return;

		GroupDialog dialog = new GroupDialog(getOwner(), GroupDialog.Type.EDIT,
				containerGroup);
		dialog.showDialog();

		if (dialog.getCloseAs() == null || !dialog.getCloseAs())
			return;

		PWMGroup group = null;
		try {
			group = PWMUtils.loadPWMGroup(containerGroup,
					dialog.getOldPassword());
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		if (group == null) {
			MessageDialog.showError(getOwner(), "PWM - Error",
					"Could not load the original Group...\n"
							+ "Wrong password?");
		} else {
			if (!dialog.getResult().getName().equals(containerGroup.getName())) {
				containerGroup.setName(dialog.getResult().getName());
				group.setName(dialog.getResult().getName());
			}
			try {
				PWMUtils.savePWMGroup(containerGroup, group, dialog
						.getNewPassword() != null ? dialog.getNewPassword()
						: dialog.getOldPassword());
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.showError(getOwner(), "PWM - Error",
						"Could not save the Group with a new password...");
			}
		}
	}

	private void handleRemoveGroup(ActionEvent event) {

		PWMContainer container = (PWMContainer) adapter
				.getInternalAdapter(PWMContainer.class);
		PWMContainerGroup containerGroup = (PWMContainerGroup) adapter
				.getInternalAdapter(PWMContainerGroup.class);

		if (container == null || containerGroup == null)
			return;

		boolean confirm = MessageDialog
				.showQuestion(
						getOwner(),
						"PWM - Remove Group",
						String.format(
								"Are you sure, that you want to remove the Group '%s'?",
								containerGroup.getName()));

		if (!confirm)
			return;

		// TODO handle sub-group removal
		Object o = PWMUtils.findParentPWMObject(container, containerGroup);
		if (o instanceof PWMContainer)
			container.removeGroup(containerGroup);
		else if (o instanceof PWMContainerGroup)
			((PWMContainerGroup) o).removeGroup(containerGroup);
	}

	private void handleAddEntry(ActionEvent event) {

		PWMContainerGroup containerGroup = (PWMContainerGroup) adapter
				.getInternalAdapter(PWMContainerGroup.class);
		PWMGroup currentGroup = (PWMGroup) adapter
				.getInternalAdapter(PWMGroup.class);
		PWMGroupEntry currentEntry = (PWMGroupEntry) adapter
				.getInternalAdapter(PWMGroupEntry.class);

		if (currentGroup == null)
			return;

		EntryDialog dialog = new EntryDialog(getOwner(), EntryDialog.Type.ADD,
				currentEntry);
		dialog.showDialog();

		if (dialog.getCloseAs() == null || !dialog.getCloseAs())
			return;

		Calendar c = Calendar.getInstance();

		dialog.getResult().setId(
				PWMUtils.getPWMID(dialog.getResult().getName(), c));
		dialog.getResult().setDateAdded(c);
		dialog.getResult().setDateModified(c);

		currentGroup.addEntry(dialog.getResult());

		try {
			PWMUtils.savePWMGroup(containerGroup, currentGroup,
					(String) adapter.getInternalAdapter(String.class));
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.showError(
					getOwner(),
					"PWM - Error",
					"Could not create the new Entry.\n\nReason: "
							+ e.getMessage());
		}
	}

	private void handleEditEntry(ActionEvent event) {

		PWMContainerGroup containerGroup = (PWMContainerGroup) adapter
				.getInternalAdapter(PWMContainerGroup.class);
		PWMGroup currentGroup = (PWMGroup) adapter
				.getInternalAdapter(PWMGroup.class);
		PWMGroupEntry currentEntry = (PWMGroupEntry) adapter
				.getInternalAdapter(PWMGroupEntry.class);

		if (currentEntry == null)
			return;

		EntryDialog dialog = new EntryDialog(getOwner(), EntryDialog.Type.EDIT,
				currentEntry);
		dialog.showDialog();

		if (dialog.getCloseAs() == null || !dialog.getCloseAs())
			return;

		PWMGroupEntry result = dialog.getResult();

		boolean modified = false;

		if (result.getName() != null
				&& !result.getName().equals(currentEntry.getName())) {
			currentEntry.setName(result.getName());
			modified = true;
		}
		if (result.getUsername() != null
				&& !result.getUsername().equals(currentEntry.getUsername())) {
			currentEntry.setUsername(result.getUsername());
			modified = true;
		}
		if (result.getPassword() != null
				&& !result.getPassword().equals(currentEntry.getPassword())) {
			currentEntry.setPassword(result.getPassword());
			modified = true;
		}
		if (result.getUrl() != null
				&& !result.getUrl().equals(currentEntry.getUrl())) {
			currentEntry.setUrl(result.getUrl());
			modified = true;
		}
		if (result.getDescription() != null
				&& !result.getDescription().equals(
						currentEntry.getDescription())) {
			currentEntry.setDescription(result.getDescription());
			modified = true;
		}
		if (result.getDateExpiration() != null
				&& !result.getDateExpiration().equals(
						currentEntry.getDateExpiration())) {
			currentEntry.setDateExpiration(result.getDateExpiration());
			modified = true;
		}

		Calendar c = Calendar.getInstance();

		if (currentEntry.getId() == null || currentEntry.getId().isEmpty()) {
			currentEntry.setId(PWMUtils.getPWMID(result.getName(), c));
			modified = true;
		}
		if (currentEntry.getDateAdded() == null) {
			currentEntry.setDateAdded(c);
			modified = true;
		}
		if (modified)
			currentEntry.setDateModified(c);

		if (modified) {
			try {
				PWMUtils.savePWMGroup(containerGroup, currentGroup,
						(String) adapter.getInternalAdapter(String.class));
				de.dzim.jfx.event.EventHandler.getInstance().fireEvent(
						new Object[] { currentGroup,
								adapter.getInternalAdapter(String.class) },
						EditorWindow.UPDATE_CONTENT);
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.showError(
						getOwner(),
						"PWM - Error",
						"Could not create the new Entry.\n\nReason: "
								+ e.getMessage());
			}
		}
	}

	private void handleRemoveEntry(ActionEvent event) {

		PWMContainerGroup containerGroup = (PWMContainerGroup) adapter
				.getInternalAdapter(PWMContainerGroup.class);
		PWMGroup currentGroup = (PWMGroup) adapter
				.getInternalAdapter(PWMGroup.class);
		PWMGroupEntry currentEntry = (PWMGroupEntry) adapter
				.getInternalAdapter(PWMGroupEntry.class);

		if (currentEntry == null || currentGroup == null)
			return;

		boolean confirm = MessageDialog
				.showQuestion(
						getOwner(),
						"PWM - Remove Entry",
						String.format(
								"Are you sure, that you want to remove the Entry '%s'?",
								currentEntry.getName()));

		if (!confirm)
			return;

		currentGroup.removeEntry(currentEntry);

		try {
			PWMUtils.savePWMGroup(containerGroup, currentGroup,
					(String) adapter.getInternalAdapter(String.class));
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.showError(getOwner(), "PWM - Error",
					"Could not remove the Entry\n\nReason: " + e.getMessage());
		}
	}

	private void handleOpenURL(ActionEvent event) {

		Hyperlink h = ((Hyperlink) event.getSource());
		if (h == null)
			return;
		String text = h.getText();
		if (text == null || text.isEmpty())
			return;
		Desktop d = Desktop.getDesktop();
		if (d == null)
			return;
		try {
			d.browse(new URI(text));
		} catch (Exception e) {
			MessageDialog.showError(
					(Window) adapter.getInternalAdapter(Window.class),
					"PWM - Error",
					String.format(
							"An error occured when opening the external browser:\n\n"
									+ "The Desired URL was %s\n\n"
									+ "The message was:\n%s", text,
							e.getMessage()));
		}
	}

	private void handleClipboardURL(ActionEvent event) {

		PWMGroupEntry currentEntry = (PWMGroupEntry) adapter
				.getInternalAdapter(PWMGroupEntry.class);

		if (currentEntry == null)
			return;

		if (currentEntry.getUrl() == null || currentEntry.getUrl().isEmpty())
			return;

		Clipboard cb = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();
		content.put(DataFormat.PLAIN_TEXT, currentEntry.getUrl());
		cb.setContent(content);
	}

	private void handleClipboardUser(ActionEvent event) {

		PWMGroupEntry currentEntry = (PWMGroupEntry) adapter
				.getInternalAdapter(PWMGroupEntry.class);

		if (currentEntry == null)
			return;

		if (currentEntry.getUsername() == null
				|| currentEntry.getUsername().isEmpty())
			return;

		Clipboard cb = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();
		content.put(DataFormat.PLAIN_TEXT, currentEntry.getUsername());
		cb.setContent(content);
	}

	private void handleClipboardPassword(ActionEvent event) {

		PWMGroupEntry currentEntry = (PWMGroupEntry) adapter
				.getInternalAdapter(PWMGroupEntry.class);

		if (currentEntry == null)
			return;

		if (currentEntry.getPassword() == null
				|| currentEntry.getPassword().isEmpty())
			return;

		Clipboard cb = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();
		content.put(DataFormat.PLAIN_TEXT, currentEntry.getPassword());
		cb.setContent(content);

	}

	private Window getOwner() {
		return adapter != null ? (Window) adapter
				.getInternalAdapter(Window.class) : null;
	}

	private ObservableList<ExtensionFilter> getExtensionFilter() {

		ObservableList<ExtensionFilter> result = FXCollections
				.<ExtensionFilter> observableArrayList();

		List<String> extensions = new ArrayList<String>();
		extensions.add("*.pwm");
		extensions.add("*.PWM");
		result.add(new ExtensionFilter("PWM Files", extensions));

		extensions = new ArrayList<String>();
		extensions.add("*");
		result.add(new ExtensionFilter("All Files", extensions));

		return result;
	}
}
