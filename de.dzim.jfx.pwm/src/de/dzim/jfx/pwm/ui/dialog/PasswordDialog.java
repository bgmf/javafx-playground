package de.dzim.jfx.pwm.ui.dialog;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
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

public class PasswordDialog extends Dialog<String> {

	private static final String WINDOW_TITLE = "PWM - Password";

	private final PWMContainerGroup containerGroup;

	public PasswordDialog(Window owner, PWMContainerGroup containerGroup) {

		super(owner, Modality.APPLICATION_MODAL, StageStyle.DECORATED,
				WINDOW_TITLE);
		this.containerGroup = containerGroup;

		addButton(ButtonID.OK).setDisable(true);
		addButton(ButtonID.CANCEL);
		
		setStylesheetLocation(PWMJFXApplication.CSS_DIALOG);
	}

	@Override
	protected Node createCenterContent() {
		try {
			// Node node = FXMLLoader.<Node> load(PasswortDialog.class
			// .getResource("PasswordDialog.fxml"));
			FXMLLoader loader = new FXMLLoader();
			loader.setController(this);
			loader.setLocation(PasswordDialog.class
					.getResource("PasswordDialog.fxml"));
			Node node = (Node) loader.load();
			return node;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@FXML
	private PasswordField passwordField;

	@FXML
	protected void passwordCheck(KeyEvent e) {
		PWMGroup group = null;
		try {
			group = PWMUtils.loadPWMGroup(containerGroup,
					passwordField.getText());
			result = passwordField.getText();
		} catch (JAXBException e1) {
		}
		getButton(ButtonID.OK).setDisable(group == null);
	}
}
