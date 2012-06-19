package de.dzim.jfx.pwm.ui.dialog;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import de.dzim.jfx.pwm.model.content.PWMGroupEntry;
import de.dzim.jfx.pwm.ui.PWMJFXApplication;
import de.dzim.jfx.ui.dialog.Dialog;

public class EntryDialog extends Dialog<PWMGroupEntry> {

	public enum Type {
		ADD, EDIT;
	}

	private static final String WINDOW_TITLE = "PWM - Manage an Entry";

	private static final String AREA_TITLE_1 = "Create a new Entry";
	private static final String AREA_TITLE_2 = "Edit an Entry";

	private final Type type;

	// private PWMGroupEntry entry;

	public EntryDialog(Window owner, Type type, PWMGroupEntry entry) {

		super(owner, Modality.APPLICATION_MODAL, StageStyle.DECORATED,
				WINDOW_TITLE);

		this.type = type;
		// this.entry = entry;

		result = new PWMGroupEntry();
		if (type == Type.EDIT) {
			result.setId(entry.getId());
			result.setName(entry.getName());
			result.setUsername(entry.getUsername());
			result.setPassword(entry.getPassword());
			result.setUrl(entry.getUrl());
			result.setDescription(entry.getDescription());
			result.setDateAdded(entry.getDateAdded());
			result.setDateModified(entry.getDateModified());
			result.setDateExpiration(entry.getDateExpiration());
		}

		addButton(ButtonID.OK).setDisable(true);
		addButton(ButtonID.CANCEL);

		setStylesheetLocation(PWMJFXApplication.CSS_DIALOG);
	}

	@FXML
	private Label areaTitle;
	@FXML
	private TextField titleField;
	@FXML
	private TextField userField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private TextField urlField;
	@FXML
	private TextArea descriptionArea;

	@Override
	protected Node createCenterContent() {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setController(this);
			loader.setLocation(PasswordDialog.class
					.getResource("EntryDialog.fxml"));
			Node node = (Node) loader.load();

			switch (type) {

			case ADD:
				areaTitle.setText(AREA_TITLE_1);
				break;

			case EDIT:

				areaTitle.setText(AREA_TITLE_2);

				titleField.setText(result.getName() != null ? result.getName()
						: "");
				userField.setText(result.getUsername() != null ? result
						.getUsername() : "");
				passwordField.setText(result.getPassword() != null ? result
						.getPassword() : "");
				urlField.setText(result.getUrl() != null ? result.getUrl() : "");
				descriptionArea
						.setText(result.getDescription() != null ? result
								.getDescription() : "");

				getButton(ButtonID.OK).setDisable(!checkOkCriteria());
				break;
			}

			return node;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@FXML
	protected void handleTitle(KeyEvent event) {
		getButton(ButtonID.OK).setDisable(!checkOkCriteria());
		result.setName(titleField.getText());
	}

	@FXML
	protected void handleUser(KeyEvent event) {
		getButton(ButtonID.OK).setDisable(!checkOkCriteria());
		result.setUsername(userField.getText());
	}

	@FXML
	protected void handlePassword(KeyEvent event) {
		getButton(ButtonID.OK).setDisable(!checkOkCriteria());
		result.setPassword(passwordField.getText());
	}

	@FXML
	protected void handleUrl(KeyEvent event) {
		getButton(ButtonID.OK).setDisable(!checkOkCriteria());
		result.setUrl(urlField.getText());
	}

	@FXML
	protected void handleDescr(KeyEvent event) {
		getButton(ButtonID.OK).setDisable(!checkOkCriteria());
		result.setDescription(descriptionArea.getText());
	}

	private boolean checkOkCriteria() {
		if (titleField.getText().isEmpty())
			return false;
		return true;
	}
}
