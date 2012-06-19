package de.dzim.jfx.pwm.ui.component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javafx.scene.control.TableCell;
import de.dzim.jfx.pwm.model.content.PWMGroupEntry;

class PWMGroupEntryDateCell extends TableCell<PWMGroupEntry, Calendar> {

	@Override
	public void updateItem(Calendar item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			setText(getString());
		}
	}

	private DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	private String getString() {
		return getItem() == null ? "" : df.format(((Calendar) getItem())
				.getTime());
	}
}