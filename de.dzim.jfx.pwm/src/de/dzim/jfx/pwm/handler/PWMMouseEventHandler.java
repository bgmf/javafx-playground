package de.dzim.jfx.pwm.handler;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

import javax.xml.bind.JAXBException;

import de.dzim.jfx.pwm.model.container.PWMContainerGroup;
import de.dzim.jfx.pwm.model.content.PWMGroup;
import de.dzim.jfx.pwm.model.content.PWMGroupEntry;
import de.dzim.jfx.pwm.ui.component.EditorWindow;
import de.dzim.jfx.pwm.ui.component.MainWindow;
import de.dzim.jfx.pwm.ui.dialog.PasswordDialog;
import de.dzim.jfx.pwm.util.InternalAdapter;
import de.dzim.jfx.pwm.util.PWMUtils;
import de.dzim.jfx.ui.dialog.MessageDialog;

public class PWMMouseEventHandler implements EventHandler<MouseEvent> {

	public enum Type {
		CONTAINER_GROUP, CONTENT;
	}

	protected final Type type;

	protected InternalAdapter adapter;

	public PWMMouseEventHandler(final Type type) {
		this.type = type;
	}

	public PWMMouseEventHandler(final Type type, final InternalAdapter adapter) {
		this(type);
		this.adapter = adapter;
	}

	public void setAdapter(InternalAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void handle(MouseEvent event) {

		switch (type) {
		case CONTAINER_GROUP:
			handleContainerGroup(event);
			break;
		case CONTENT:
			handleContent(event);
			break;
		}
	}

	private void handleContainerGroup(MouseEvent event) {

		PWMContainerGroup containerGroup = (PWMContainerGroup) adapter
				.getInternalAdapter(PWMContainerGroup.class);
		if (containerGroup == null)
			return;

		de.dzim.jfx.event.EventHandler.getInstance().fireEvent(containerGroup,
				MainWindow.GROUP_SELECTED);

		if (event != null) {
			switch (event.getButton()) {
			case PRIMARY:
				if (event.getClickCount() == 1) {
					// do nothing so far
				} else if (event.getClickCount() == 2) {
					handleContainerGroupLoading(containerGroup);
				}
				break;
			case SECONDARY:
				break;
			case MIDDLE:
				break;
			}
		}
		// XXX Fake to reuse the handler
		else {
			handleContainerGroupLoading(containerGroup);
		}
	}

	private void handleContainerGroupLoading(PWMContainerGroup containerGroup) {

		Window owner = (Window) adapter.getInternalAdapter(Window.class);

		String pw = "";
		PWMGroup group = null;
		// try with empty password
		try {
			group = PWMUtils.loadPWMGroup(containerGroup, pw);
		} catch (JAXBException e) {
		}
		if (group == null) {
			try {
				PasswordDialog dialog = new PasswordDialog(owner,
						containerGroup);
				dialog.showDialog();
				if (Boolean.TRUE.equals(dialog.getCloseAs()))
					pw = dialog.getResult();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// try with specified password
		boolean pwProblem = false;
		try {
			group = PWMUtils.loadPWMGroup(containerGroup, pw);
			if (group == null)
				pwProblem = true;
		} catch (JAXBException e) {
			pwProblem = true;
		}
		if (pwProblem) {
			MessageDialog.showError(owner, "PWM - Wrong Password?",
					"The password you've specified seems to be incorrect...\n"
							+ "Please try again!");
		} else {
			de.dzim.jfx.event.EventHandler.getInstance().fireEvent(
					new Object[] { group, pw }, EditorWindow.UPDATE_CONTENT);
		}
	}

	private void handleContent(MouseEvent event) {

		PWMGroupEntry entry = (PWMGroupEntry) adapter
				.getInternalAdapter(PWMGroupEntry.class);
		if (entry == null)
			return;

		if (event != null) {
			switch (event.getButton()) {
			case PRIMARY:
				de.dzim.jfx.event.EventHandler.getInstance().fireEvent(entry,
						EditorWindow.UPDATE_ENTRY);
				break;
			case SECONDARY:
				// ((Control)event.getSource()).getContextMenu()
				break;
			case MIDDLE:
				break;
			}
		}
		// XXX Fake to reuse the handler
		else {
			de.dzim.jfx.event.EventHandler.getInstance().fireEvent(entry,
					EditorWindow.UPDATE_ENTRY);
		}
	}
}
