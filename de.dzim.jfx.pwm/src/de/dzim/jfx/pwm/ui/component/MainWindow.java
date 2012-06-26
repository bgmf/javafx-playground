package de.dzim.jfx.pwm.ui.component;

import java.io.File;

import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import de.dzim.jfx.event.EventHandler;
import de.dzim.jfx.event.EventHandler.Event;
import de.dzim.jfx.event.EventHandlerListener;
import de.dzim.jfx.pwm.handler.PWMActionEventHandler;
import de.dzim.jfx.pwm.handler.PWMActionEventHandler.Type;
import de.dzim.jfx.pwm.model.container.PWMContainer;
import de.dzim.jfx.pwm.model.container.PWMContainerGroup;
import de.dzim.jfx.pwm.model.content.PWMGroup;
import de.dzim.jfx.pwm.model.content.PWMGroupEntry;
import de.dzim.jfx.pwm.ui.PWMJFXApplication;
import de.dzim.jfx.ui.resource.ImageResource;
import de.dzim.jfx.ui.resource.ImageResource.ImageResourceType;
import de.dzim.jfx.util.InternalAdapter;

public class MainWindow implements InternalAdapter {

	// event names

	public static final String FILE_OPENED = MainWindow.class.getName()
			+ ".file-opened";

	public static final String DIRTY = MainWindow.class.getName() + ".dirty";
	public static final String NOT_DIRTY = MainWindow.class.getName()
			+ ".dirty.not";

	public static final String GROUP_SELECTED = MainWindow.class.getName()
			+ "group.selected";
	public static final String GROUP_NOT_SELECTED = MainWindow.class.getName()
			+ "group.selected.not";

	public static final String GROUP_LOADED = MainWindow.class.getName()
			+ "group.loaded";
	public static final String GROUP_UNLOADED = MainWindow.class.getName()
			+ "group.loaded.not";

	public static final String ENTRY_SELECTED = MainWindow.class.getName()
			+ "entry.selected";
	public static final String ENTRY_NOT_SELECTED = MainWindow.class.getName()
			+ "entry.selected.not";

	public static final String UPDATE_FILE = MainWindow.class.getName()
			+ ".update.file";

	// window

	private final Window parent;
	private final Pane root;

	private boolean createMenu = true;
	private boolean createToolbar = true;

	private EditorWindow editor;

	public MainWindow(Window parent, Pane root) {

		this.parent = parent;
		this.root = root;

		EventHandler.getInstance().addListener(
				new InternalEventHandlerListener(), FILE_OPENED, DIRTY,
				NOT_DIRTY, GROUP_SELECTED, GROUP_NOT_SELECTED, GROUP_LOADED,
				GROUP_UNLOADED, ENTRY_SELECTED, ENTRY_NOT_SELECTED);
	}

	public void setCreateMenu(boolean createMenu) {
		this.createMenu = createMenu;
	}

	public boolean isCreateMenu() {
		return createMenu;
	}

	public void setCreateToolbar(boolean createToolbar) {
		this.createToolbar = createToolbar;
	}

	public boolean isCreateToolbar() {
		return createToolbar;
	}

	private MenuBar menubar;
	private ToolBar toolbar;

	public void createContent() {

		VBox menubarToolbarBox = new VBox(0);

		if (createMenu) {
			menubar = new MenuBar();
			menubar.setUseSystemMenuBar(false);
			createMenuBar(menubar);
			menubarToolbarBox.getChildren().add(menubar);
		}

		if (createToolbar) {
			toolbar = new ToolBar();
			toolbar.setCache(false);
			createToolbar(toolbar);
			menubarToolbarBox.getChildren().add(toolbar);
		}

		if (menubarToolbarBox.getChildren().size() > 0) {
			if (root instanceof GridPane)
				((GridPane) root).add(menubarToolbarBox, 0,
						PWMJFXApplication.useCustomDecoration ? 1 : 0);
			else
				root.getChildren().add(menubarToolbarBox);
		}

		editor = new EditorWindow(this);
		Pane editorPane = editor.createContent();
		if (root instanceof GridPane) {
			((GridPane) root).add(editorPane, 0,
					PWMJFXApplication.useCustomDecoration ? 2 : 1);
			GridPane.setHgrow(editorPane, Priority.ALWAYS);
			GridPane.setVgrow(editorPane, Priority.ALWAYS);
		} else
			root.getChildren().add(editorPane);
	}

	private MenuItem fileNewItem;
	private MenuItem fileOpenItem;
	private MenuItem fileSaveItem;
	private MenuItem fileSaveAsItem;
	private MenuItem fileExitItem;

	private MenuItem groupAddItem;
	private MenuItem groupAddSubItem;
	private MenuItem groupEditItem;
	private MenuItem groupRemoveItem;

	private MenuItem entryAddItem;
	private MenuItem entryEditItem;
	private MenuItem entryRemoveItem;

	private void createMenuBar(MenuBar menubar) {

		// file menu

		Menu file = new Menu("_File");

		fileNewItem = new MenuItem("_New",
				ImageResource.getImageView(ImageResourceType.NEW_DATABASE_16));
		fileOpenItem = new MenuItem("_Open",
				ImageResource.getImageView(ImageResourceType.OPEN_DATABASE_16));
		fileSaveItem = new MenuItem("_Save",
				ImageResource.getImageView(ImageResourceType.SAVE_16));
		fileSaveAsItem = new MenuItem("Save As...",
				ImageResource.getImageView(ImageResourceType.SAVE_AS_16));
		fileExitItem = new MenuItem("E_xit",
				ImageResource.getImageView(ImageResourceType.EXIT_16));

		fileNewItem.setAccelerator(KeyCombination.valueOf("Ctrl+N"));
		fileOpenItem.setAccelerator(KeyCombination.valueOf("Ctrl+O"));
		fileSaveItem.setAccelerator(KeyCombination.valueOf("Ctrl+S"));
		fileExitItem.setAccelerator(KeyCombination.valueOf("Ctrl+X"));

		fileNewItem.setOnAction(getHandler(Type.NEW));
		fileOpenItem.setOnAction(getHandler(Type.OPEN));
		fileSaveItem.setOnAction(getHandler(Type.SAVE));
		fileSaveAsItem.setOnAction(getHandler(Type.SAVE_AS));
		fileExitItem.setOnAction(getHandler(Type.EXIT));

		file.getItems().addAll(fileNewItem, fileOpenItem,
				new SeparatorMenuItem(), fileSaveItem, fileSaveAsItem,
				new SeparatorMenuItem(), fileExitItem);

		// group menu

		Menu group = new Menu("_Groups");

		groupAddItem = new MenuItem("_Add Group",
				ImageResource.getImageView(ImageResourceType.ADD_FOLDER_16));
		groupAddSubItem = new MenuItem("Add _Sub-Group",
				ImageResource
						.getImageView(ImageResourceType.ADD_FOLDER_BOOKMARK_16));
		groupEditItem = new MenuItem("_Edit Group",
				ImageResource.getImageView(ImageResourceType.EDIT_FOLDER_16));
		groupRemoveItem = new MenuItem("_Remove Group",
				ImageResource.getImageView(ImageResourceType.REMOVE_FOLDER_16));

		groupAddItem.setAccelerator(KeyCombination.valueOf("Ctrl+Shift+A"));
		groupAddSubItem.setAccelerator(KeyCombination.valueOf("Ctrl+Shift+S"));
		groupEditItem.setAccelerator(KeyCombination.valueOf("Ctrl+Shift+E"));
		groupRemoveItem.setAccelerator(KeyCombination.valueOf("Ctrl+Shift+R"));

		groupAddItem.setOnAction(getHandler(Type.ADD_GROUP));
		groupAddSubItem.setOnAction(getHandler(Type.ADD_SUB_GROUP));
		groupEditItem.setOnAction(getHandler(Type.EDIT_GROUP));
		groupRemoveItem.setOnAction(getHandler(Type.REMOVE_GROUP));

		group.getItems().addAll(groupAddItem, groupAddSubItem, groupEditItem,
				groupRemoveItem);

		// entry menu

		Menu entry = new Menu("_Entry");

		entryAddItem = new MenuItem("_Add Entry",
				ImageResource.getImageView(ImageResourceType.ADD_KEY_16));
		entryEditItem = new MenuItem("_Edit Entry",
				ImageResource.getImageView(ImageResourceType.EDIT_KEY_16));
		entryRemoveItem = new MenuItem("_Remove Entry",
				ImageResource.getImageView(ImageResourceType.REMOVE_KEY_16));

		entryAddItem.setAccelerator(KeyCombination.valueOf("Alt+Shift+A"));
		entryEditItem.setAccelerator(KeyCombination.valueOf("Alt+Shift+E"));
		entryRemoveItem.setAccelerator(KeyCombination.valueOf("Alt+Shift+R"));

		entryAddItem.setOnAction(getHandler(Type.ADD_ENTRY));
		entryEditItem.setOnAction(getHandler(Type.EDIT_ENTRY));
		entryRemoveItem.setOnAction(getHandler(Type.REMOVE_ENTRY));

		entry.getItems().addAll(entryAddItem, entryEditItem, entryRemoveItem);

		// build global menu

		fileSaveItem.setDisable(true);
		fileSaveAsItem.setDisable(true);
		groupAddItem.setDisable(true);
		groupAddSubItem.setDisable(true);
		groupEditItem.setDisable(true);
		groupRemoveItem.setDisable(true);
		entryAddItem.setDisable(true);
		entryEditItem.setDisable(true);
		entryRemoveItem.setDisable(true);

		menubar.getMenus().addAll(file, group, entry);
	}

	private Button newButton;
	private Button openButton;
	private Button saveButton;
	private Button saveAsButton;

	private Button addGroupButton;
	private Button addSubGroupButton;
	private Button editGroupButton;
	private Button removeGroupButton;

	private Button addEntryButton;
	private Button editEntryButton;
	private Button removeEntryButton;

	private void createToolbar(ToolBar toolbar) {

		newButton = new Button(
				ImageResource.getImage(ImageResourceType.NEW_DATABASE_16) == null ? "New"
						: "", ImageResource
						.getImageView(ImageResourceType.NEW_DATABASE_16));
		openButton = new Button(
				ImageResource.getImage(ImageResourceType.OPEN_DATABASE_16) == null ? "Open"
						: "", ImageResource
						.getImageView(ImageResourceType.OPEN_DATABASE_16));
		saveButton = new Button(
				ImageResource.getImage(ImageResourceType.OPEN_DATABASE_16) == null ? "Save"
						: "", ImageResource
						.getImageView(ImageResourceType.SAVE_16));
		saveAsButton = new Button(
				ImageResource.getImage(ImageResourceType.OPEN_DATABASE_16) == null ? "Save As..."
						: "", ImageResource
						.getImageView(ImageResourceType.SAVE_AS_16));

		addGroupButton = new Button(
				ImageResource.getImage(ImageResourceType.OPEN_DATABASE_16) == null ? "Add Group"
						: "", ImageResource
						.getImageView(ImageResourceType.ADD_FOLDER_16));
		addSubGroupButton = new Button(
				ImageResource.getImage(ImageResourceType.OPEN_DATABASE_16) == null ? "Add Sub-Group"
						: "", ImageResource
						.getImageView(ImageResourceType.ADD_FOLDER_BOOKMARK_16));
		editGroupButton = new Button(
				ImageResource.getImage(ImageResourceType.OPEN_DATABASE_16) == null ? "Edit Group"
						: "", ImageResource
						.getImageView(ImageResourceType.EDIT_FOLDER_16));
		removeGroupButton = new Button(
				ImageResource.getImage(ImageResourceType.OPEN_DATABASE_16) == null ? "Remove Group"
						: "", ImageResource
						.getImageView(ImageResourceType.REMOVE_FOLDER_16));

		addEntryButton = new Button(
				ImageResource.getImage(ImageResourceType.OPEN_DATABASE_16) == null ? "Add Entry"
						: "", ImageResource
						.getImageView(ImageResourceType.ADD_KEY_16));
		editEntryButton = new Button(
				ImageResource.getImage(ImageResourceType.OPEN_DATABASE_16) == null ? "Edit Entry"
						: "", ImageResource
						.getImageView(ImageResourceType.EDIT_KEY_16));
		removeEntryButton = new Button(
				ImageResource.getImage(ImageResourceType.OPEN_DATABASE_16) == null ? "Remove Entry"
						: "", ImageResource
						.getImageView(ImageResourceType.REMOVE_KEY_16));

		newButton.setOnAction(getHandler(Type.NEW));
		openButton.setOnAction(getHandler(Type.OPEN));
		saveButton.setOnAction(getHandler(Type.SAVE));
		saveAsButton.setOnAction(getHandler(Type.SAVE_AS));

		addGroupButton.setOnAction(getHandler(Type.ADD_GROUP));
		addSubGroupButton.setOnAction(getHandler(Type.ADD_SUB_GROUP));
		editGroupButton.setOnAction(getHandler(Type.EDIT_GROUP));
		removeGroupButton.setOnAction(getHandler(Type.REMOVE_GROUP));

		addEntryButton.setOnAction(getHandler(Type.ADD_ENTRY));
		editEntryButton.setOnAction(getHandler(Type.EDIT_ENTRY));
		removeEntryButton.setOnAction(getHandler(Type.REMOVE_ENTRY));

		saveButton.setDisable(true);
		saveAsButton.setDisable(true);
		addGroupButton.setDisable(true);
		addSubGroupButton.setDisable(true);
		editGroupButton.setDisable(true);
		removeGroupButton.setDisable(true);
		addEntryButton.setDisable(true);
		editEntryButton.setDisable(true);
		removeEntryButton.setDisable(true);

		toolbar.getItems().addAll(newButton, openButton, new Separator(),
				saveButton, saveAsButton, new Separator(), addGroupButton,
				addSubGroupButton, editGroupButton, removeGroupButton,
				new Separator(), addEntryButton, editEntryButton,
				removeEntryButton);
	}

	private PWMActionEventHandler getHandler(Type type) {
		return new PWMActionEventHandler(type, this);
	}

	@Override
	public Object getInternalAdapter(Class<?> adapter) {
		if (MainWindow.class.isAssignableFrom(adapter))
			return this;
		else if (EditorWindow.class.isAssignableFrom(adapter))
			return editor;
		else if (Window.class.isAssignableFrom(adapter))
			return parent;
		else if (File.class.isAssignableFrom(adapter))
			return editor.getInternalAdapter(adapter);
		else if (PWMContainer.class.isAssignableFrom(adapter))
			return editor.getInternalAdapter(adapter);
		else if (PWMContainerGroup.class.isAssignableFrom(adapter))
			return editor.getInternalAdapter(adapter);
		else if (PWMGroup.class.isAssignableFrom(adapter))
			return editor.getInternalAdapter(adapter);
		else if (PWMGroupEntry.class.isAssignableFrom(adapter))
			return editor.getInternalAdapter(adapter);
		else if (String.class.isAssignableFrom(adapter))
			return editor.getInternalAdapter(adapter);
		else if (Boolean.class.isAssignableFrom(adapter)
				|| boolean.class.isAssignableFrom(adapter))
			return dirty;
		return null;
	}

	private boolean dirty = false;
	private File containerFile = null;

	private class InternalEventHandlerListener implements EventHandlerListener {
		@Override
		public void handleEvent(Event event) {

			if (event.getName() == null)
				return;
			else if (event.getName().equals(FILE_OPENED)) {
				saveAsButton.setDisable(false);
				fileSaveAsItem.setDisable(false);
				addGroupButton.setDisable(false);
				groupAddItem.setDisable(false);
				if (event.getSource() != null
						&& event.getSource() instanceof File)
					containerFile = (File) event.getSource();
			} else if (event.getName().equals(UPDATE_FILE)) {
				containerFile = (File) event.getSource();
			} else if (event.getName().equals(DIRTY)) {
				saveButton.setDisable(false);
				fileSaveItem.setDisable(false);
				dirty = true;
			} else if (event.getName().equals(NOT_DIRTY)) {
				saveButton.setDisable(true);
				fileSaveItem.setDisable(true);
				dirty = false;
			} else if (event.getName().equals(GROUP_SELECTED)) {
				addSubGroupButton.setDisable(false);
				groupAddSubItem.setDisable(false);
				editGroupButton.setDisable(false);
				groupEditItem.setDisable(false);
				removeGroupButton.setDisable(false);
				groupRemoveItem.setDisable(false);
			} else if (event.getName().equals(GROUP_NOT_SELECTED)) {
				addSubGroupButton.setDisable(true);
				groupAddSubItem.setDisable(true);
				editGroupButton.setDisable(true);
				groupEditItem.setDisable(true);
				removeGroupButton.setDisable(true);
				groupRemoveItem.setDisable(true);
			} else if (event.getName().equals(GROUP_LOADED)) {
				addEntryButton.setDisable(false);
				entryAddItem.setDisable(false);
			} else if (event.getName().equals(GROUP_UNLOADED)) {
				addEntryButton.setDisable(true);
				entryAddItem.setDisable(true);
			} else if (event.getName().equals(ENTRY_SELECTED)) {
				editEntryButton.setDisable(false);
				entryEditItem.setDisable(false);
				removeEntryButton.setDisable(false);
				entryRemoveItem.setDisable(false);
			} else if (event.getName().equals(ENTRY_NOT_SELECTED)) {
				editEntryButton.setDisable(true);
				entryEditItem.setDisable(true);
				removeEntryButton.setDisable(true);
				entryRemoveItem.setDisable(true);
			}

			Stage parentStage = null;
			if (parent instanceof Stage)
				parentStage = (Stage) parent;
			if (parentStage != null)
				parentStage.setTitle(String
						.format("%s%s%s",
								dirty ? "*" : "",
								PWMJFXApplication.APPLICATION_TITLE,
								containerFile != null ? (": " + containerFile
										.getName()) : ""));
		}
	}
}
