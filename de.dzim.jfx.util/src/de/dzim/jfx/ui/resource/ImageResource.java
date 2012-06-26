package de.dzim.jfx.ui.resource;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageResource {

	public enum ImageResourceType {

		// ---------------------------
		// default icons - START
		// ---------------------------

		// exit icon
		EXIT_16("fugue_door-open-in.png"),
		// new icon
		NEW_16("new.gif"),
		// open icon
		OPEN_16("fldr_obj.gif"),
		// save icon
		SAVE_16("save_edit.gif"),
		// save as icon
		SAVE_AS_16("saveas_edit.gif"),

		//
		// message icons
		//

		// error icon (with circle around it)
		ERROR_16("fugue_cross-circle.png"),
		// error icon (simple cross)
		ERROR_SIMPLE_16("fugue_cross.png"),
		// info icon
		INFORMATION_16("fugue_information.png"),
		// warning icon
		WARNING_16("fugue_exclamation.png"),
		// question icon
		QUESTION_16("fugue_question.png"),
		// ok icon
		OK_16("fugue_tick.png"),
		// cancel icon
		CANCEL_16("fugue_minus-circle.png"),
		// add icon (simple plus)
		ADD_16("fugue_plus.png"),
		// edit icon (a pencil)
		EDIT_16("fugue_pencil.png"),
		// remove icon (simple minus)
		REMOVE_16("fugue_minus.png"),
		// error icon (with circle around it) - small
		ERROR_SMALL_16("fugue_cross-small-circle.png"),
		// add icon (simple plus) - small
		ADD_SMALL_16("fugue_plus-small.png"),
		// edit icon (a pencil) - small
		EDIT_SMALL_16("fugue_pencil-small.png"),
		// remove icon (simple minus) - small
		REMOVE_SMALL_16("fugue_minus-small.png"),

		// error icon (with circle around it)
		ERROR_32("fugue_32_cross-circle.png"),
		// error icon (simple cross)
		ERROR_SIMPLE_32("fugue_32_cross.png"),
		// info icon
		INFORMATION_32("fugue_32_information.png"),
		// warning icon
		WARNING_32("fugue_32_exclamation.png"),
		// question icon
		QUESTION_32("fugue_32_question.png"),
		// ok icon
		OK_32("fugue_32_tick.png"),
		// cancel icon
		CANCEL_32("fugue_32_minus-circle.png"),
		// add icon (simple plus)
		ADD_32("fugue_32_plus.png"),
		// remove icon (simple minus)
		REMOVE_32("fugue_32_minus.png"),

		// next / forward
		NEXT_16("fugue_arrow.png"),
		// back
		BACK_16("fugue_arrow-180.png"),

		//
		// clipboard icons
		//

		// invoice
		CLIPBOARD_INVOICE_16("fugue_clipboard-invoice.png"),

		//
		// other icons
		//

		// lock
		LOCK_16("fugue_lock.png"),
		// lock 32x32
		LOCK_32("fugue_32_lock.png"),
		// calendar
		CALENDAR_16("fugue_calendar.png"),
		// calendar 32x32
		CALENDAR_32("fugue_32_calendar.png"),
		// calendar
		CALENDAR_BLUE_16("fugue_calendar-blue.png"),
		// calendar 32x32
		CALENDAR_BLUE_32("fugue_32_calendar-blue.png"),
		// color base
		COLOR_16("fugue_color.png"),
		// color base 32x32
		COLOR_32("fugue_32_color.png"),
		// counter
		COUNTER_16("fugue_counter.png"),
		// counter 32x32
		COUNTER_32("fugue_32_counter.png"),
		// external browser
		EXTERNAL_BROWSER_16("external_browser.gif"),

		// ---------------------------
		// default icons - END
		// ---------------------------

		// ---------------------------
		// PWM Icons - START
		// ---------------------------

		// new db icon
		NEW_DATABASE_16("fugue_database--plus.png"),
		// open db icon
		OPEN_DATABASE_16("fugue_database.png"),

		//
		// group icons
		//

		// add group icon
		ADD_FOLDER_16("fugue_folder--plus.png"),
		// add sub group icon
		ADD_FOLDER_BOOKMARK_16("fugue_folder-bookmark.png"),
		// edit group icon
		EDIT_FOLDER_16("fugue_folder--pencil.png"),
		// remove group icon
		REMOVE_FOLDER_16("fugue_folder--minus.png"),

		//
		// entry icons
		//

		// add entry icon
		ADD_KEY_16("fugue_key--plus.png"),
		// edit entry icon
		EDIT_KEY_16("fugue_key--pencil.png"),
		// remove entry icon
		REMOVE_KEY_16("fugue_key--minus.png");

		// ---------------------------
		// PWM Icons - END
		// ---------------------------

		// ---------------------------
		// Counter Icons - START
		// ---------------------------

		// ---------------------------
		// Counter Icons - START
		// ---------------------------

		private final String name;

		private ImageResourceType(String name) {
			this.name = name;
		}
	}

	private static Map<ImageResourceType, Image> images = new HashMap<ImageResourceType, Image>();

	public static Image getImage(ImageResourceType type) {
		if (images.get(type) == null)
			images.put(
					type,
					new Image(ImageResource.class
							.getResourceAsStream(type.name)));
		return images.get(type);
	}

	public static ImageView getImageView(ImageResourceType type) {
		Image img = getImage(type);
		if (img != null)
			return new ImageView(img);
		return null;
	}
}
