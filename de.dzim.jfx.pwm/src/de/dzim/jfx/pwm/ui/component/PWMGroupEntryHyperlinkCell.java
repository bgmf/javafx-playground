package de.dzim.jfx.pwm.ui.component;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import de.dzim.jfx.pwm.handler.PWMActionEventHandler;
import de.dzim.jfx.pwm.model.content.PWMGroupEntry;
import de.dzim.jfx.pwm.util.InternalAdapter;

class PWMGroupEntryHyperlinkCell extends TableCell<PWMGroupEntry, String> {

	private final Hyperlink hyperlink;

	public PWMGroupEntryHyperlinkCell(final InternalAdapter parentAdapter) {
		hyperlink = new Hyperlink();
		hyperlink.setOnAction(new PWMActionEventHandler(
				de.dzim.jfx.pwm.handler.PWMActionEventHandler.Type.OPEN_URL,
				parentAdapter));
	}

	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (hyperlink != null) {
				hyperlink.setText(getString());
			}
			setText(null);
			setGraphic(hyperlink);
		}
	}

	private String getString() {
		return getItem() == null ? "" : getItem().toString();
	}
}