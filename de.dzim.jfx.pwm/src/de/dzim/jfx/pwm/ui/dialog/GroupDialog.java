package de.dzim.jfx.pwm.ui.dialog;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import javax.xml.bind.JAXBException;

import de.dzim.jfx.pwm.model.container.PWMContainerGroup;
import de.dzim.jfx.pwm.model.content.PWMGroup;
import de.dzim.jfx.pwm.ui.PWMJFXApplication;
import de.dzim.jfx.pwm.util.PWMUtils;
import de.dzim.jfx.ui.dialog.Dialog;

public class GroupDialog extends Dialog<PWMContainerGroup> {

	public enum Type {
		ADD, ADD_SUB, EDIT
	}

	private static final String WINDOW_TITLE = "PWM - Manage a Group";

	private static final String AREA_TITLE_1 = "Create a new Group";
	private static final String AREA_TITLE_2 = "Create a new Sub-Group";
	private static final String AREA_TITLE_3 = "Edit a Group";

	private final Type type;
	private PWMContainerGroup group;

	private String oldPassword;
	private String newPassword;

	public GroupDialog(Window owner, Type type, PWMContainerGroup group) {

		super(owner, Modality.APPLICATION_MODAL, StageStyle.DECORATED,
				WINDOW_TITLE);

		this.type = type;
		this.group = group;

		result = new PWMContainerGroup();
		if (type == Type.EDIT)
			result.setName(group.getName());

		addButton(ButtonID.OK).setDisable(true);
		addButton(ButtonID.CANCEL);

		setStylesheetLocation(PWMJFXApplication.CSS_DIALOG);
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	@FXML
	private Label areaTitle;
	@FXML
	private TextField groupName;
	@FXML
	private PasswordField groupPassword;

	@FXML
	private Separator separator;
	@FXML
	private Label newLabel;
	@FXML
	private PasswordField newField;
	@FXML
	private Label repeatLabel;
	@FXML
	private PasswordField repeatField;

	@Override
	protected Node createCenterContent() {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setController(this);
			loader.setLocation(PasswordDialog.class
					.getResource("GroupDialog.fxml"));
			Node node = (Node) loader.load();

			switch (type) {

			case ADD:
				areaTitle.setText(AREA_TITLE_1);
				break;

			case ADD_SUB:
				areaTitle.setText(AREA_TITLE_2);
				break;

			case EDIT:

				areaTitle.setText(AREA_TITLE_3);

				groupName.setText(group.getName());
				// groupPassword.setText(password);

				separator.setVisible(true);
				newLabel.setVisible(true);
				newField.setVisible(true);
				repeatLabel.setVisible(true);
				repeatField.setVisible(true);

				newField.setDisable(true);
				repeatField.setDisable(true);

				InternalKeyEventHandler handler = new InternalKeyEventHandler();
				newField.setOnKeyReleased(handler);
				repeatField.setOnKeyReleased(handler);

				getButton(ButtonID.OK).setDisable(true);

				break;
			}
			return node;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@FXML
	protected void handleName(KeyEvent event) {
		boolean check = checkOkCriteria();
		getButton(ButtonID.OK).setDisable(!check);
		result.setName(groupName.getText());
	}

	@FXML
	protected void handlePassword(KeyEvent event) {
		switch (type) {
		case EDIT:
			boolean correctPW = checkPW(groupPassword.getText());
			newField.setDisable(!correctPW);
			repeatField.setDisable(!correctPW);
			break;
		default:
			newPassword = groupPassword.getText();
			break;
		}
		boolean check = checkOkCriteria();
		getButton(ButtonID.OK).setDisable(!check);
	}

	private boolean checkOkCriteria() {
		switch (type) {
		case EDIT:
			boolean correctPW = checkPW(groupPassword.getText());
			if (groupName.getText() == null || groupName.getText().isEmpty()
					|| !correctPW)
				return false;
			return true;
		default:
			if (groupName.getText() == null || groupName.getText().isEmpty())
				return false;
			return true;
		}
	}

	private boolean checkPW(String pw) {
		PWMGroup group = null;
		try {
			group = PWMUtils.loadPWMGroup(this.group, pw);
			if (group != null)
				oldPassword = pw;
			else
				oldPassword = null;
		} catch (JAXBException e1) {
		}
		return group != null;
	}

	private class InternalKeyEventHandler implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent event) {
			boolean check = checkOkCriteria();
			if (check && newField.getText().equals(repeatField.getText())) {
				newPassword = newField.getText();
				getButton(ButtonID.OK).setDisable(false);
			} else {
				newPassword = null;
				getButton(ButtonID.OK).setDisable(true);
			}
		}
	}
}
