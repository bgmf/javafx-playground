package de.dzim.jfx.pwm.ui.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import de.dzim.jfx.event.EventHandler;
import de.dzim.jfx.event.EventHandler.Event;
import de.dzim.jfx.event.EventHandlerListener;
import de.dzim.jfx.pwm.handler.PWMActionEventHandler;
import de.dzim.jfx.pwm.handler.PWMMouseEventHandler;
import de.dzim.jfx.pwm.handler.PWMMouseEventHandler.Type;
import de.dzim.jfx.pwm.model.container.PWMContainer;
import de.dzim.jfx.pwm.model.container.PWMContainerGroup;
import de.dzim.jfx.pwm.model.content.PWMGroup;
import de.dzim.jfx.pwm.model.content.PWMGroupEntry;
import de.dzim.jfx.pwm.util.InternalAdapter;

public class EditorWindow implements InternalAdapter {

	// event names

	public static final String UPDATE_DATABASE = EditorWindow.class.getName()
			+ ".update.database";
	public static final String UPDATE_CONTENT = EditorWindow.class.getName()
			+ ".update.content";
	public static final String UPDATE_ENTRY = EditorWindow.class.getName()
			+ ".update.entry";

	// window

	private final InternalAdapter parentAdapter;

	public EditorWindow(final InternalAdapter parentAdapter) {

		this.parentAdapter = parentAdapter;

		EventHandler.getInstance().addListener(
				new InternalEventHandlerListener(),
				// from PWMMainWindow
				MainWindow.FILE_OPENED, MainWindow.GROUP_SELECTED,
				MainWindow.GROUP_NOT_SELECTED, MainWindow.GROUP_LOADED,
				MainWindow.GROUP_UNLOADED, MainWindow.ENTRY_SELECTED,
				MainWindow.ENTRY_NOT_SELECTED,
				// from current window
				UPDATE_DATABASE, UPDATE_CONTENT, UPDATE_ENTRY);
	}

	public Pane createContent() {

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setPadding(new Insets(5, 5, 5, 5));

		Pane groupPane = createGroupPane();
		Pane contentPane = createContentPane();

		grid.add(groupPane, 0, 0);
		grid.add(contentPane, 1, 0);

		// GridPane.setHgrow(groupPane, Priority.ALWAYS);
		GridPane.setVgrow(groupPane, Priority.ALWAYS);
		GridPane.setHgrow(contentPane, Priority.ALWAYS);
		GridPane.setVgrow(contentPane, Priority.ALWAYS);

		return grid;
	}

	private File containerFile;
	private PWMContainer container;

	private PWMContainerGroup currentContainerGroup;
	private PWMGroup currentGroup;
	private String currentGroupPassword;
	private PWMGroupEntry currentEntry;

	private TreeView<PWMContainerGroup> groupTreeView;
	private TableView<PWMGroupEntry> contentTableView;

	private Text tTitle;
	private Text tUser;
	private Text tPass;
	private Hyperlink hURL;
	private Text tDateAdd;
	private Text tDateMod;
	private Text tDateExp;
	private TextArea taDesc;

	private Pane createGroupPane() {

		GridPane grid = new GridPane();
		grid.setVgap(5);

		groupTreeView = new TreeView<PWMContainerGroup>(null);
		groupTreeView.setShowRoot(false);
		groupTreeView.setCellFactory(new PWMContainerGroupCallback());
		groupTreeView.setMinWidth(100);

		groupTreeView.setOnMouseClicked(new InternalPWMMouseEventHandler(
				Type.CONTAINER_GROUP));
		groupTreeView.setOnKeyTyped(new InternalKeyEventHandler(
				Type.CONTAINER_GROUP));

		groupTreeView.setContextMenu(ContextMenuCreator.getInstance(
				parentAdapter).getGroupMenu(false));

		TitledPane groupPane = new TitledPane();
		groupPane.setText("Groups");
		groupPane.setCollapsible(false);
		groupPane.setExpanded(true);
		groupPane.setContent(groupTreeView);
		groupPane.setMaxHeight(Double.MAX_VALUE);

		grid.add(groupPane, 0, 0);

		GridPane.setHgrow(groupPane, Priority.ALWAYS);
		GridPane.setVgrow(groupPane, Priority.ALWAYS);

		return grid;
	}

	private Pane createContentPane() {

		VBox vbox = new VBox(5);

		// content table section

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		contentTableView = new TableView<PWMGroupEntry>(
				FXCollections.<PWMGroupEntry> emptyObservableList());
		contentTableView.getSelectionModel().setSelectionMode(
				SelectionMode.SINGLE);
		contentTableView
				.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		contentTableView.setMinSize(TableView.USE_PREF_SIZE,
				TableView.USE_PREF_SIZE);

		contentTableView.setOnMouseClicked(new InternalPWMMouseEventHandler(
				Type.CONTENT));
		contentTableView
				.setOnKeyTyped(new InternalKeyEventHandler(Type.CONTENT));

		contentTableView.setContextMenu(ContextMenuCreator.getInstance(
				parentAdapter).getContentMenu(false));

		TableColumn<PWMGroupEntry, String> tcTitle = new TableColumn<PWMGroupEntry, String>(
				"Title");
		tcTitle.setPrefWidth(200);
		tcTitle.setCellValueFactory(new PropertyValueFactory<PWMGroupEntry, String>(
				"name"));
		TableColumn<PWMGroupEntry, String> tcUser = new TableColumn<PWMGroupEntry, String>(
				"Username");
		tcUser.setPrefWidth(200);
		tcUser.setCellValueFactory(new PropertyValueFactory<PWMGroupEntry, String>(
				"username"));
		TableColumn<PWMGroupEntry, String> tcURL = new TableColumn<PWMGroupEntry, String>(
				"URL");
		tcURL.setPrefWidth(250);
		tcURL.setCellValueFactory(new PropertyValueFactory<PWMGroupEntry, String>(
				"url"));
		tcURL.setCellFactory(new Callback<TableColumn<PWMGroupEntry, String>, TableCell<PWMGroupEntry, String>>() {
			@Override
			public TableCell<PWMGroupEntry, String> call(
					TableColumn<PWMGroupEntry, String> param) {
				return new PWMGroupEntryHyperlinkCell(parentAdapter);
			}
		});
		TableColumn<PWMGroupEntry, Calendar> tcDateAdd = new TableColumn<PWMGroupEntry, Calendar>(
				"Date Added");
		tcDateAdd.setPrefWidth(150);
		tcDateAdd
				.setCellValueFactory(new PropertyValueFactory<PWMGroupEntry, Calendar>(
						"dateAdded"));
		tcDateAdd
				.setCellFactory(new Callback<TableColumn<PWMGroupEntry, Calendar>, TableCell<PWMGroupEntry, Calendar>>() {
					@Override
					public TableCell<PWMGroupEntry, Calendar> call(
							TableColumn<PWMGroupEntry, Calendar> param) {
						return new PWMGroupEntryDateCell();
					}
				});
		TableColumn<PWMGroupEntry, Calendar> tcDateMod = new TableColumn<PWMGroupEntry, Calendar>(
				"Date Modified");
		tcDateMod.setPrefWidth(150);
		tcDateMod
				.setCellValueFactory(new PropertyValueFactory<PWMGroupEntry, Calendar>(
						"dateModified"));
		tcDateMod
				.setCellFactory(new Callback<TableColumn<PWMGroupEntry, Calendar>, TableCell<PWMGroupEntry, Calendar>>() {
					@Override
					public TableCell<PWMGroupEntry, Calendar> call(
							TableColumn<PWMGroupEntry, Calendar> param) {
						return new PWMGroupEntryDateCell();
					}
				});

		contentTableView.getColumns().add(tcTitle);
		contentTableView.getColumns().add(tcUser);
		contentTableView.getColumns().add(tcURL);
		contentTableView.getColumns().add(tcDateAdd);
		contentTableView.getColumns().add(tcDateMod);

		scrollPane.setContent(contentTableView);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);

		TitledPane contentPane = new TitledPane();
		contentPane.setText("Entries");
		contentPane.setCollapsible(false);
		contentPane.setExpanded(true);
		contentPane.setMaxHeight(Double.MAX_VALUE);
		contentPane.setMinWidth(300);
		contentPane.setMinHeight(300);
		contentPane.setContent(scrollPane);

		// content overview section

		GridPane grid2 = new GridPane();
		grid2.setId("tp-content");
		grid2.setPadding(new Insets(5, 5, 0, 5));
		grid2.setHgap(5);
		grid2.setVgap(5);

		Label lTitle = new Label("Title:");
		lTitle.setTextAlignment(TextAlignment.RIGHT);
		lTitle.getStyleClass().add("label");
		Label lUser = new Label("Username:");
		lUser.setTextAlignment(TextAlignment.RIGHT);
		Label lPass = new Label("Password:");
		lPass.setTextAlignment(TextAlignment.RIGHT);
		Label lURL = new Label("URL:");
		lURL.setTextAlignment(TextAlignment.RIGHT);
		Label lDateAdd = new Label("Date Added:");
		lDateAdd.setTextAlignment(TextAlignment.RIGHT);
		Label lDateMod = new Label("Date Modified:");
		lDateMod.setTextAlignment(TextAlignment.RIGHT);
		Label lDateExp = new Label("Expiration Date:");
		lDateExp.setTextAlignment(TextAlignment.RIGHT);
		Label lDesc = new Label("Description:");
		lDesc.setTextAlignment(TextAlignment.RIGHT);

		tTitle = new Text();
		tUser = new Text();
		tPass = new Text();
		hURL = new Hyperlink();
		hURL.setOnAction(new PWMActionEventHandler(
				de.dzim.jfx.pwm.handler.PWMActionEventHandler.Type.OPEN_URL,
				parentAdapter));
		tDateAdd = new Text();
		tDateMod = new Text();
		tDateExp = new Text();
		taDesc = new TextArea();
		taDesc.setId("ta-description");
		taDesc.setEditable(false);
		taDesc.setMinHeight(50);
		taDesc.setMaxHeight(50);

		grid2.addColumn(0, lTitle, lUser, lPass, lURL, lDesc);
		grid2.addColumn(1, tTitle, tUser, tPass, hURL, taDesc);
		grid2.addColumn(2, lDateAdd, lDateMod, lDateExp);
		grid2.addColumn(3, tDateAdd, tDateMod, tDateExp);

		GridPane.setHgrow(tTitle, Priority.ALWAYS);
		GridPane.setHgrow(tUser, Priority.ALWAYS);
		GridPane.setHgrow(tPass, Priority.ALWAYS);
		GridPane.setHgrow(hURL, Priority.ALWAYS);
		GridPane.setHgrow(taDesc, Priority.ALWAYS);
		GridPane.setHgrow(tDateAdd, Priority.ALWAYS);
		GridPane.setHgrow(tDateMod, Priority.ALWAYS);
		GridPane.setHgrow(tDateExp, Priority.ALWAYS);
		// GridPane.setVgrow(tfDesc, Priority.ALWAYS);
		GridPane.setColumnSpan(taDesc, 3);

		TitledPane contentOverviewPane = new TitledPane();
		contentOverviewPane.setText("Entry Overview");
		contentOverviewPane.setCollapsible(false);
		contentOverviewPane.setExpanded(true);
		contentOverviewPane.setContent(grid2);
		contentOverviewPane.setMinHeight(175);

		// add to vbox

		vbox.getChildren().add(contentPane);
		vbox.getChildren().add(contentOverviewPane);

		VBox.setVgrow(contentPane, Priority.ALWAYS);

		return vbox;
	}

	public void updateDatabase(File containerFile, PWMContainer container) {

		if (container == null)
			return;

		if (containerFile != null)
			this.containerFile = containerFile;

		if (this.container != null)
			this.container.removeListener(internalPropertyChangeListener);

		this.container = container;

		PWMContainer.init(this.container);
		this.container.addListener(internalPropertyChangeListener);

		groupTreeView.setRoot(createGroupNode(container));
		contentTableView.setItems(FXCollections
				.<PWMGroupEntry> emptyObservableList());

		EventHandler.getInstance().fireEvent(container, MainWindow.FILE_OPENED);
	}

	public void updateContent(PWMGroup group, String password) {

		if (this.currentGroup != null)
			currentGroup.removeListener(internalPropertyChangeListener);

		currentGroupPassword = password;
		currentGroup = group;

		PWMGroup.init(currentGroup);
		currentGroup.addListener(internalPropertyChangeListener);

		contentTableView.setItems(FXCollections
				.<PWMGroupEntry> observableArrayList(group.getEntries()));

		EventHandler.getInstance().fireEvent(group, MainWindow.GROUP_LOADED);
		EventHandler.getInstance().fireEvent(group,
				MainWindow.ENTRY_NOT_SELECTED);
	}

	public void updateEntry(PWMGroupEntry entry) {

		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

		tTitle.setText(entry.getName() != null ? entry.getName() : "");
		tUser.setText(entry.getUsername() != null ? entry.getUsername() : "");
		if (entry.getPassword() != null) {
			String pw = "";
			for (; pw.length() < entry.getPassword().length(); pw = pw
					.concat("*"))
				;
			tPass.setText(pw);
		}
		hURL.setVisited(false);
		hURL.setText(entry.getUrl() != null ? entry.getUrl() : "");
		tDateAdd.setText(entry.getDateAdded() != null ? df.format(entry
				.getDateAdded().getTime()) : "");
		tDateMod.setText(entry.getDateModified() != null ? df.format(entry
				.getDateModified().getTime()) : "");
		tDateExp.setText(entry.getDateExpiration() != null ? df.format(entry
				.getDateExpiration().getTime()) : "");
		taDesc.setText(entry.getDescription() != null ? entry.getDescription()
				: "");

		EventHandler.getInstance().fireEvent(entry, MainWindow.ENTRY_SELECTED);
	}

	private TreeItem<PWMContainerGroup> createGroupNode(PWMContainer container) {
		return createGroupNode(new PWMContainerGroupRoot(container));
	}

	private TreeItem<PWMContainerGroup> createGroupNode(PWMContainerGroup group) {
		return new PWMContainerGroupTreeItem(group);
	}

	private ObservableList<TreeItem<PWMContainerGroup>> buildGroupChildren(
			TreeItem<PWMContainerGroup> treeItem) {
		PWMContainerGroup group = treeItem.getValue();
		if (group != null && !group.getGroups().isEmpty()) {
			ObservableList<TreeItem<PWMContainerGroup>> children = FXCollections
					.observableArrayList();
			for (PWMContainerGroup childGroup : group.getGroups())
				children.add(createGroupNode(childGroup));
			return children;
		}

		return FXCollections.emptyObservableList();
	}

	@Override
	public Object getInternalAdapter(Class<?> adapter) {
		if (EditorWindow.class.isAssignableFrom(adapter))
			return this;
		else if (File.class.isAssignableFrom(adapter))
			return containerFile;
		else if (PWMContainer.class.isAssignableFrom(adapter))
			return container;
		else if (PWMContainerGroup.class.isAssignableFrom(adapter))
			return currentContainerGroup;
		else if (PWMGroup.class.isAssignableFrom(adapter))
			return currentGroup;
		else if (PWMGroupEntry.class.isAssignableFrom(adapter))
			return currentEntry;
		else if (String.class.isAssignableFrom(adapter))
			return currentGroupPassword;
		return null;
	}

	/**
	 * Custom TreeItem for group entries.
	 * 
	 * @author dzimmermann
	 */
	public final class PWMContainerGroupTreeItem extends
			TreeItem<PWMContainerGroup> {

		private boolean isLeaf;
		private boolean isFirstTimeChildren = true;
		private boolean isFirstTimeLeaf = true;

		public PWMContainerGroupTreeItem(PWMContainerGroup group) {
			super(group);
		}

		@Override
		public ObservableList<TreeItem<PWMContainerGroup>> getChildren() {
			if (isFirstTimeChildren) {
				super.getChildren().setAll(buildGroupChildren(this));
				isFirstTimeChildren = false;
			}
			return super.getChildren();
		}

		@Override
		public boolean isLeaf() {
			if (isFirstTimeLeaf) {
				PWMContainerGroup g = (PWMContainerGroup) getValue();
				isLeaf = g.getGroups().isEmpty();
				isFirstTimeLeaf = false;
			}
			return isLeaf;
		}
	}

	/**
	 * A fake root for the Tree.
	 * 
	 * @author dzimmermann
	 */
	public static final class PWMContainerGroupRoot extends PWMContainerGroup {

		public PWMContainerGroupRoot(PWMContainer container) {
			setName("PWM Groups");
			setId(getName());
			getGroups().addAll(container.getGroups());
		}
	}

	/**
	 * Custom callback handler for printing the TreeItems label.
	 * 
	 * @author dzimmermann
	 */
	public static final class PWMContainerGroupCallback implements
			Callback<TreeView<PWMContainerGroup>, TreeCell<PWMContainerGroup>> {

		@Override
		public TreeCell<PWMContainerGroup> call(
				TreeView<PWMContainerGroup> param) {
			return new TreeCell<PWMContainerGroup>() {
				@Override
				protected void updateItem(PWMContainerGroup item, boolean empty) {
					super.updateItem(item, empty);
					if (item != null)
						setText(item.getName());
				}
			};
		}
	}

	/**
	 * Custom callback handler for printing the TreeItems label.
	 * 
	 * @author dzimmermann
	 */
	public static final class PWMTableViewCallback implements
			Callback<TreeView<Object>, TreeCell<Object>> {

		@Override
		public TreeCell<Object> call(TreeView<Object> param) {
			return new TreeCell<Object>() {
				@Override
				protected void updateItem(Object item, boolean empty) {
					super.updateItem(item, empty);
					if (item != null) {
						if (item instanceof PWMContainerGroup)
							setText(((PWMContainerGroup) item).getName());
						else if (item instanceof PWMGroup)
							setText(((PWMGroup) item).getName());
					}
				}
			};
		}
	}

	private class InternalPWMMouseEventHandler extends PWMMouseEventHandler {

		public InternalPWMMouseEventHandler(Type type) {
			super(type, EditorWindow.this);
		}

		@Override
		public void handle(MouseEvent event) {

			switch (type) {
			case CONTAINER_GROUP:
				TreeItem<PWMContainerGroup> selection = groupTreeView
						.getSelectionModel().getSelectedItem();
				if (selection == null)
					return;
				currentContainerGroup = selection.getValue();
				break;
			case CONTENT:
				PWMGroupEntry entry = contentTableView.getSelectionModel()
						.getSelectedItem();
				if (entry == null)
					return;
				currentEntry = entry;
				break;
			}

			super.handle(event);
		}
	}

	private class InternalKeyEventHandler implements
			javafx.event.EventHandler<KeyEvent> {

		private final Type type;

		public InternalKeyEventHandler(Type type) {
			this.type = type;
		}

		@Override
		public void handle(KeyEvent event) {
			if (event.getCode() == KeyCode.UNDEFINED) {
				switch (type) {
				case CONTAINER_GROUP:
					TreeItem<PWMContainerGroup> selection = groupTreeView
							.getSelectionModel().getSelectedItem();
					if (selection == null)
						return;
					currentContainerGroup = selection.getValue();
					break;
				case CONTENT:
					PWMGroupEntry entry = contentTableView.getSelectionModel()
							.getSelectedItem();
					if (entry == null)
						return;
					currentEntry = entry;
					break;
				}
				new PWMMouseEventHandler(type, parentAdapter).handle(null);
			} else {
				System.err.println("Unknown Key Event: " + event);
			}
		}
	}

	private InternalPropertyChangeListener internalPropertyChangeListener = new InternalPropertyChangeListener();

	private class InternalPropertyChangeListener implements
			PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {

			System.err.printf("%s --> %s%n", evt.getPropertyName(), evt
					.getNewValue().toString());

			EventHandler.getInstance().fireEvent(evt.getNewValue(),
					MainWindow.DIRTY);

			String evtName = evt.getPropertyName();

			if (evtName.equals(PWMContainer.GROUP_CHANGED_ADD)
					|| evtName.equals(PWMContainer.GROUP_CHANGED_REMOVE)
					|| evtName.equals(PWMContainerGroup.NAME_CHANGED)
					|| evtName.equals(PWMContainerGroup.ID_CHANGED)
					|| evtName.equals(PWMContainerGroup.CONTENT_CHANGED)) {
				updateDatabase(null, container);
			} else if (evtName.equals(PWMGroup.ENTRY_ADD_CHANGED)
					|| evtName.equals(PWMGroup.ENTRY_REMOVE_CHANGED)) {
				updateContent(currentGroup, currentGroupPassword);
			}
			// XXX Handling for all changes on an Entry would follow, but...
			// ... right now this would cause much overhead in possibly
			// reloading the whole tree several times, so the
			// PWMActionEventHandler, triggering these events, is calling my
			// internal EventHandler mechanism one time to update the tree, when
			// the data are saved into the internal secure storage format
		}
	}

	private class InternalEventHandlerListener implements EventHandlerListener {
		@Override
		public void handleEvent(Event event) {
			ContextMenuCreator creator = ContextMenuCreator
					.getInstance(parentAdapter);
			if (event.getName() == null)
				return;
			else if (event.getName().equals(UPDATE_DATABASE)) {
				Object[] source = (Object[]) event.getSource();
				updateDatabase((File) source[0], (PWMContainer) source[1]);
			} else if (event.getName().equals(UPDATE_CONTENT)) {
				Object[] source = (Object[]) event.getSource();
				updateContent((PWMGroup) source[0], (String) source[1]);
			} else if (event.getName().equals(UPDATE_ENTRY)) {
				updateEntry((PWMGroupEntry) event.getSource());
			} else if (event.getName().equals(MainWindow.FILE_OPENED)) {
				creator.getMenuItem(PWMActionEventHandler.Type.ADD_GROUP)
						.setDisable(false);
			} else if (event.getName().equals(MainWindow.GROUP_SELECTED)) {
				creator.getMenuItem(PWMActionEventHandler.Type.ADD_SUB_GROUP)
						.setDisable(false);
				creator.getMenuItem(PWMActionEventHandler.Type.EDIT_GROUP)
						.setDisable(false);
				creator.getMenuItem(PWMActionEventHandler.Type.REMOVE_GROUP)
						.setDisable(false);
			} else if (event.getName().equals(MainWindow.GROUP_NOT_SELECTED)) {
				creator.getMenuItem(PWMActionEventHandler.Type.ADD_SUB_GROUP)
						.setDisable(true);
				creator.getMenuItem(PWMActionEventHandler.Type.EDIT_GROUP)
						.setDisable(true);
				creator.getMenuItem(PWMActionEventHandler.Type.REMOVE_GROUP)
						.setDisable(true);
			} else if (event.getName().equals(MainWindow.GROUP_LOADED)) {
				creator.getMenuItem(PWMActionEventHandler.Type.ADD_ENTRY)
						.setDisable(false);
			} else if (event.getName().equals(MainWindow.GROUP_UNLOADED)) {
				creator.getMenuItem(PWMActionEventHandler.Type.ADD_ENTRY)
						.setDisable(false);
			} else if (event.getName().equals(MainWindow.ENTRY_SELECTED)) {
				creator.getMenuItem(PWMActionEventHandler.Type.EDIT_ENTRY)
						.setDisable(false);
				creator.getMenuItem(PWMActionEventHandler.Type.REMOVE_ENTRY)
						.setDisable(false);
				creator.getMenuItem(PWMActionEventHandler.Type.CLIPBOARD_URL)
						.setDisable(false);
				creator.getMenuItem(PWMActionEventHandler.Type.CLIPBOARD_USER)
						.setDisable(false);
				creator.getMenuItem(
						PWMActionEventHandler.Type.CLIPBOARD_PASSWORD)
						.setDisable(false);
			} else if (event.getName().equals(MainWindow.ENTRY_NOT_SELECTED)) {
				creator.getMenuItem(PWMActionEventHandler.Type.EDIT_ENTRY)
						.setDisable(true);
				creator.getMenuItem(PWMActionEventHandler.Type.REMOVE_ENTRY)
						.setDisable(true);
				creator.getMenuItem(PWMActionEventHandler.Type.CLIPBOARD_URL)
						.setDisable(true);
				creator.getMenuItem(PWMActionEventHandler.Type.CLIPBOARD_USER)
						.setDisable(true);
				creator.getMenuItem(
						PWMActionEventHandler.Type.CLIPBOARD_PASSWORD)
						.setDisable(true);
			}
		}
	}
}
