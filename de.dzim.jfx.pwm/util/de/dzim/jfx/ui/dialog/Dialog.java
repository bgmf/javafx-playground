package de.dzim.jfx.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import de.dzim.jfx.pwm.util.InternalAdapter;
import de.dzim.jfx.ui.resource.ImageResource;
import de.dzim.jfx.ui.resource.ImageResource.ImageResourceType;

public abstract class Dialog<T> extends Stage implements InternalAdapter {

	/**
	 * Default constructor.
	 * 
	 * @param owner
	 * @param modality
	 * @param style
	 * @param title
	 */
	public Dialog(Window owner, Modality modality, StageStyle style,
			String title) {

		super(style);

		initOwner(owner);
		initModality(modality);
		setOpacity(1);
		if (title != null && !title.isEmpty())
			setTitle(title);
	}

	/**
	 * Default constructor without a text for the dialogs title.
	 * 
	 * @param owner
	 * @param modality
	 * @param style
	 */
	public Dialog(Window owner, Modality modality, StageStyle style) {
		this(owner, modality, style, null);
	}

	/**
	 * Default constructor without a specific StageStyle.
	 * 
	 * @param owner
	 * @param modality
	 * @param title
	 */
	public Dialog(Window owner, Modality modality, String title) {
		this(owner, modality, null, title);
	}

	/**
	 * This constructor is for convenience, be sure to set the owner, the
	 * modality the title and so on, before you show it!
	 */
	public Dialog() {
	}

	private Double contentHeight;
	private Double contentWidth;

	/**
	 * set the min size of this dialog, must be set before the methods
	 * {@link #show()}, {@link #showAndWait()} or {@link #showDialog()} are
	 * used.
	 * 
	 * @param height
	 * @param width
	 */
	public void setMinSize(double height, double width) {
		contentHeight = height;
		contentWidth = width;
	}

	/**
	 * See the parent {@link #showAndWait()} method. Simply returns a custom
	 * value, if the implementation make use of it, otherwise the value might be
	 * <code>null</code>.
	 * 
	 * @return a result, might be <code>null</code>
	 */
	public T showDialog(boolean wait) {

		createDialogContent();

		if (wait)
			super.showAndWait();
		else
			super.show();

		return result;
	}

	public T showDialog() {
		return showDialog(true);
	}

	/**
	 * a might-be result
	 */
	protected T result;

	/**
	 * The result on "ok"/"cancel" (if not overrided).
	 */
	protected Boolean closeAs;

	/**
	 * The dialog content.
	 */
	protected BorderPane borderPane;

	/**
	 * The dialogs main content.
	 */
	private Node centerNode;

	/**
	 * A horizontal box of buttons.
	 */
	private HBox buttonHBox;

	private Map<ButtonID, Button> buttons = new HashMap<ButtonID, Button>();
	private Map<String, Button> customButtons = new HashMap<String, Button>();
	private List<Button> orderedButtonList = new ArrayList<Button>();

	/**
	 * A custom CSS stylesheet.
	 */
	private String stylesheetLocation = null;

	/**
	 * create the container and load the stuff for the center of the BorderPane
	 * by calling the abstract method {@link #createCenterContent()}
	 */
	private void createDialogContent() {

		borderPane = new BorderPane();
		borderPane.setUserData(this);
		borderPane.setId("dialog");

		borderPane.setPadding(new Insets(10, 10, 10, 10));

		buttonHBox = new HBox(5);
		buttonHBox.setUserData(this);
		buttonHBox.setAlignment(Pos.BOTTOM_RIGHT);
		buttonHBox.setPadding(new Insets(10, 5, 0, 5));

		for (Button b : orderedButtonList)
			buttonHBox.getChildren().add(b);

		centerNode = createCenterContent();
		centerNode.setUserData(this);
		if (centerNode != null)
			borderPane.setCenter(centerNode);

		borderPane.setBottom(buttonHBox);

		if (contentHeight != null)
			borderPane.setMinHeight(contentHeight);
		if (contentWidth != null)
			borderPane.setMinWidth(contentWidth);

		BorderPane.setAlignment(centerNode, Pos.CENTER);
		BorderPane.setAlignment(buttonHBox, Pos.BOTTOM_RIGHT);

		this.showingProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue) {
					Dialog.this.layout();
				}
			}
		});
		Scene scene = new Scene(borderPane);
		if (stylesheetLocation != null)
			scene.getStylesheets().add(stylesheetLocation);
		this.setScene(scene);
	}

	void layout() {

		double maxWidth = 0;
		for (Button b : orderedButtonList) {
			maxWidth = Math.max(maxWidth, b.prefWidth(-1));
		}

		for (Button b : orderedButtonList) {
			b.setPrefWidth(maxWidth);
		}

		// Point2D size = getInitialSize();
		// stage.setWidth(size.getX());
		// stage.setHeight(size.getY());
		// stage.sizeToScene();
	}

	/**
	 * Set a custom stylsheet to be used by the dialog.
	 * 
	 * @param stylesheetLocation
	 *            Since a relative path would mean relative to the Dialog class,
	 *            you might need to specify a full path.
	 * @see {@link #stylesheetLocation}
	 */
	public void setStylesheetLocation(String stylesheetLocation) {
		this.stylesheetLocation = stylesheetLocation;
	}

	/**
	 * The parent for the content is a BorderPane and will per default be added
	 * to center. The bottom of the pane is used for the buttons - an HBox.
	 * </br>Keep that in mind when trying to add other elements to this
	 * BorderPane: You "only" have the top, left and right positions left for
	 * other content.
	 * 
	 * @return
	 */
	protected abstract Node createCenterContent();

	/**
	 * see {@link #showDialog()}
	 * 
	 * @return a result, might be <code>null</code>
	 */
	public T getResult() {
		return result;
	}

	/**
	 * for any unmodified OK/CANCEL result.
	 * 
	 * @return
	 */
	public Boolean getCloseAs() {
		return closeAs;
	}

	/**
	 * Add a default button (text &amp; icon).
	 * 
	 * @param buttonId
	 * @return
	 */
	public Button addButton(ButtonID buttonId) {
		final Button b = new Button();
		b.setText(buttonId.title);
		if (buttonId.icon != null)
			b.setGraphic(new ImageView(buttonId.icon));
		switch (buttonId) {
		case OK:
			b.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent event) {
					handleOk(event);
				}
			});
			// TODO fix problematic NPE on VK_ENTER press
			// b.setDefaultButton(true);
			break;
		case CANCEL:
			b.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent event) {
					handleCancel(event);
				}
			});
			break;
		case NEXT:
			b.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent event) {
					handleNext(event);
				}
			});
			break;
		case BACK:
			b.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent event) {
					handleBack(event);
				}
			});
			break;
		case HELP:
			b.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent event) {
					handleHelp(event);
				}
			});
			break;
		}
		orderedButtonList.add(b);
		buttons.put(buttonId, b);
		return b;
	}

	/**
	 * add a custom button with a custom id for it.
	 * 
	 * @param id
	 * @param button
	 * @return
	 */
	public Button addCustomButton(String id, Button button) {
		orderedButtonList.add(button);
		customButtons.put(id, button);
		return button;
	}

	/**
	 * The default implementation for the "ok" button sets the {@link #closeAs}
	 * value to <code>true</code> and closes the window.
	 * 
	 * @param event
	 */
	protected void handleOk(ActionEvent event) {
		Dialog.this.closeAs = Boolean.TRUE;
		Dialog.this.close();
	}

	/**
	 * The default implementation for the "ok" button sets the {@link #closeAs}
	 * value to <code>false</code> and closes the window.
	 * 
	 * @param event
	 */
	protected void handleCancel(ActionEvent event) {
		Dialog.this.closeAs = Boolean.FALSE;
		Dialog.this.close();
	}

	/**
	 * The default implementation for the "next" button does nothing.
	 * 
	 * @param event
	 */
	protected void handleNext(ActionEvent event) {
	}

	/**
	 * The default implementation for the "back" button does nothing.
	 * 
	 * @param event
	 */
	protected void handleBack(ActionEvent event) {
	}

	/**
	 * The default implementation for the "help" button does nothing.
	 * 
	 * @param event
	 */
	protected void handleHelp(ActionEvent event) {
	}

	/**
	 * get a default button
	 * 
	 * @param buttonID
	 * @return
	 */
	public Button getButton(ButtonID buttonID) {
		return buttons.get(buttonID);
	}

	/**
	 * get a custom button via it's id
	 * 
	 * @param id
	 * @return
	 */
	public Button getCustomButton(String id) {
		return customButtons.get(id);
	}

	@Override
	public Object getInternalAdapter(Class<?> adapter) {
		if (List.class.isAssignableFrom(adapter))
			return orderedButtonList;
		return null;
	}

	/**
	 * Default IDs for the buttons at the bottom HBox of the parent BorderPane.
	 * 
	 * @author dzimmermann
	 * 
	 */
	public enum ButtonID {
		// ok
		OK("_OK", ImageResource.getImage(ImageResourceType.OK_16)),
		// cancel
		CANCEL("_Cancel", ImageResource.getImage(ImageResourceType.CANCEL_16)),
		// next
		NEXT("_Next", ImageResource.getImage(ImageResourceType.NEXT)),
		// back
		BACK("_Back", ImageResource.getImage(ImageResourceType.BACK)),
		// help
		HELP("_Help", ImageResource.getImage(ImageResourceType.QUESTION_16));

		private final String title;
		private final Image icon;

		private ButtonID(String title, Image icon) {
			this.title = title;
			this.icon = icon;
		}

		public String getTitle() {
			return title;
		}

		public Image getIcon() {
			return icon;
		}
	}
}
