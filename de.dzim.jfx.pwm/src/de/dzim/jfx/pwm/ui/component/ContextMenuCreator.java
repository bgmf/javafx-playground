package de.dzim.jfx.pwm.ui.component;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import de.dzim.jfx.pwm.handler.PWMActionEventHandler;
import de.dzim.jfx.pwm.handler.PWMActionEventHandler.Type;
import de.dzim.jfx.ui.resource.ImageResource;
import de.dzim.jfx.ui.resource.ImageResource.ImageResourceType;
import de.dzim.jfx.util.InternalAdapter;

public class ContextMenuCreator {

	private static Map<InternalAdapter, ContextMenuCreator> instance;

	public static ContextMenuCreator getInstance(InternalAdapter adapter) {
		if (instance == null)
			instance = new HashMap<InternalAdapter, ContextMenuCreator>();
		if (instance.get(adapter) == null)
			instance.put(adapter, new ContextMenuCreator(adapter));
		return instance.get(adapter);
	}

	private final InternalAdapter adapter;

	private ContextMenuCreator(final InternalAdapter adapter) {
		this.adapter = adapter;
	}

	public MenuItem getMenuItem(Type type) {
		switch (type) {
		case EXIT:
			break;
		case NEW:
			break;
		case OPEN:
			break;
		case SAVE:
			break;
		case SAVE_AS:
			break;
		case ADD_GROUP:
			return groupAddItem;
		case ADD_SUB_GROUP:
			return groupAddSubItem;
		case EDIT_GROUP:
			return groupEditItem;
		case REMOVE_GROUP:
			return groupRemoveItem;
		case ADD_ENTRY:
			return entryAddItem;
		case EDIT_ENTRY:
			return entryEditItem;
		case REMOVE_ENTRY:
			return entryRemoveItem;
		case OPEN_URL:
			break;
		case CLIPBOARD_URL:
			return clipboardUrlItem;
		case CLIPBOARD_USER:
			return clipboardUserItem;
		case CLIPBOARD_PASSWORD:
			return clipboardPasswordItem;
		}
		return null;
	}

	private PWMActionEventHandler getHandler(Type type) {
		return new PWMActionEventHandler(type, adapter);
	}

	private MenuItem groupAddItem;
	private MenuItem groupAddSubItem;
	private MenuItem groupEditItem;
	private MenuItem groupRemoveItem;

	public ContextMenu getGroupMenu(boolean withAccelerator) {

		if (groupAddItem == null)
			groupAddItem = new MenuItem("_Add Group",
					ImageResource.getImageView(ImageResourceType.ADD_FOLDER_16));
		if (groupAddSubItem == null)
			groupAddSubItem = new MenuItem(
					"Add _Sub-Group",
					ImageResource
							.getImageView(ImageResourceType.ADD_FOLDER_BOOKMARK_16));
		if (groupEditItem == null)
			groupEditItem = new MenuItem("_Edit Group",
					ImageResource
							.getImageView(ImageResourceType.EDIT_FOLDER_16));
		if (groupRemoveItem == null)
			groupRemoveItem = new MenuItem("_Remove Group",
					ImageResource
							.getImageView(ImageResourceType.REMOVE_FOLDER_16));

		if (withAccelerator) {
			groupAddItem.setAccelerator(KeyCombination.valueOf("Ctrl+Shift+A"));
			groupAddSubItem.setAccelerator(KeyCombination
					.valueOf("Ctrl+Shift+S"));
			groupEditItem
					.setAccelerator(KeyCombination.valueOf("Ctrl+Shift+E"));
			groupRemoveItem.setAccelerator(KeyCombination
					.valueOf("Ctrl+Shift+R"));
		}

		groupAddItem.setOnAction(getHandler(Type.ADD_GROUP));
		groupAddSubItem.setOnAction(getHandler(Type.ADD_SUB_GROUP));
		groupEditItem.setOnAction(getHandler(Type.EDIT_GROUP));
		groupRemoveItem.setOnAction(getHandler(Type.REMOVE_GROUP));

		groupAddItem.setDisable(true);
		groupAddSubItem.setDisable(true);
		groupEditItem.setDisable(true);
		groupRemoveItem.setDisable(true);

		return new ContextMenu(groupAddItem, groupAddSubItem, groupEditItem,
				groupRemoveItem);
	}

	private MenuItem entryAddItem;
	private MenuItem entryEditItem;
	private MenuItem entryRemoveItem;

	private MenuItem clipboardUrlItem;
	private MenuItem clipboardUserItem;
	private MenuItem clipboardPasswordItem;

	public ContextMenu getContentMenu(boolean withAccelerator) {

		if (entryAddItem == null)
			entryAddItem = new MenuItem("_Add Entry",
					ImageResource.getImageView(ImageResourceType.ADD_KEY_16));
		if (entryEditItem == null)
			entryEditItem = new MenuItem("_Edit Entry",
					ImageResource.getImageView(ImageResourceType.EDIT_KEY_16));
		if (entryRemoveItem == null)
			entryRemoveItem = new MenuItem("_Remove Entry",
					ImageResource.getImageView(ImageResourceType.REMOVE_KEY_16));
		if (clipboardUrlItem == null)
			clipboardUrlItem = new MenuItem(
					"Copy _URL",
					ImageResource
							.getImageView(ImageResourceType.CLIPBOARD_INVOICE_16));
		if (clipboardUserItem == null)
			clipboardUserItem = new MenuItem(
					"Copy User_name",
					ImageResource
							.getImageView(ImageResourceType.CLIPBOARD_INVOICE_16));
		if (clipboardPasswordItem == null)
			clipboardPasswordItem = new MenuItem(
					"Copy _Password",
					ImageResource
							.getImageView(ImageResourceType.CLIPBOARD_INVOICE_16));

		if (withAccelerator) {
			entryAddItem.setAccelerator(KeyCombination.valueOf("Alt+Shift+A"));
			entryEditItem.setAccelerator(KeyCombination.valueOf("Alt+Shift+E"));
			entryRemoveItem.setAccelerator(KeyCombination
					.valueOf("Alt+Shift+R"));
		}

		entryAddItem.setOnAction(getHandler(Type.ADD_ENTRY));
		entryEditItem.setOnAction(getHandler(Type.EDIT_ENTRY));
		entryRemoveItem.setOnAction(getHandler(Type.REMOVE_ENTRY));

		clipboardUrlItem.setOnAction(getHandler(Type.CLIPBOARD_URL));
		clipboardUserItem.setOnAction(getHandler(Type.CLIPBOARD_USER));
		clipboardPasswordItem.setOnAction(getHandler(Type.CLIPBOARD_PASSWORD));

		entryAddItem.setDisable(true);
		entryEditItem.setDisable(true);
		entryRemoveItem.setDisable(true);

		clipboardUrlItem.setDisable(true);
		clipboardUserItem.setDisable(true);
		clipboardPasswordItem.setDisable(true);

		return new ContextMenu(entryAddItem, entryEditItem, entryRemoveItem,
				new SeparatorMenuItem(), clipboardUrlItem, clipboardUserItem,
				clipboardPasswordItem);
	}
}