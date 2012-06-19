package de.dzim.jfx.ui.resource;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageResource {

	public enum ImageResourceType {

		//
		// default icons
		//

		// exit icon
		EXIT("fugue_door-open-in.png"),
		// new icon
		NEW("fugue_database--plus.png"),
		// open icon
		OPEN("fugue_database.png"),
		// save icon
		SAVE("save_edit.gif"),
		// save as icon
		SAVE_AS("saveas_edit.gif"),

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
		// remove icon (simple minus)
		REMOVE_16("fugue_minus.png"),

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
		NEXT("fugue_arrow.png"),
		// back
		BACK("fugue_arrow-180.png"),

		//
		// group icons
		//

		// add group icon
		ADD_GROUP("fugue_folder--plus.png"),
		// add sub group icon
		ADD_SUB_GROUP("fugue_folder-bookmark.png"),
		// edit group icon
		EDIT_GROUP("fugue_folder--pencil.png"),
		// remove group icon
		REMOVE_GROUP("fugue_folder--minus.png"),

		//
		// entry icons
		//

		// add entry icon
		ADD_ENTRY("fugue_key--plus.png"),
		// edit entry icon
		EDIT_ENTRY("fugue_key--pencil.png"),
		// remove entry icon
		REMOVE_ENTRY("fugue_key--minus.png"),

		//
		// clipboard icons
		//

		// invoice
		CLIPBOARD_INVOICE("fugue_clipboard-invoice.png"),

		//
		// other icons
		//

		// lock
		LOCK_16("fugue_lock.png"),
		// lock 32x32
		LOCK_32("fugue_32_lock.png");

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
