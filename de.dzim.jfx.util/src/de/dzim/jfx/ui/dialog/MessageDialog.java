package de.dzim.jfx.ui.dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import de.dzim.jfx.ui.dialog.Dialog.ButtonID;
import de.dzim.jfx.ui.resource.ImageResource;
import de.dzim.jfx.ui.resource.ImageResource.ImageResourceType;

public class MessageDialog {

	/**
	 * if set, the generic {@link Dialog}s
	 * {@link Dialog#setStylesheetLocation(String)} is used to trigger a look
	 * and feel you desire.
	 */
	public static String CSS_PATH = null;

	public static void showError(final Window owner, final String title,
			final String message) {
		final Dialog<Boolean> dialog = createDialog(ImageResourceType.ERROR_32,
				owner, title, message);
		dialog.showDialog();
	}

	public static void showWarning(final Window owner, final String title,
			final String message) {
		final Dialog<Boolean> dialog = createDialog(
				ImageResourceType.WARNING_32, owner, title, message);
		dialog.showDialog();
	}

	public static void showInformation(final Window owner, final String title,
			final String message) {
		final Dialog<Boolean> dialog = createDialog(
				ImageResourceType.INFORMATION_32, owner, title, message);
		dialog.showDialog();
	}

	public static boolean showQuestion(final Window owner, final String title,
			final String message) {
		final Dialog<Boolean> dialog = createDialog(
				ImageResourceType.QUESTION_32, owner, title, message);
		dialog.addButton(ButtonID.CANCEL);
		dialog.showDialog();
		return Boolean.TRUE == dialog.closeAs;
	}

	private static Dialog<Boolean> createDialog(final ImageResourceType type,
			final Window owner, final String title, final String message) {
		final Dialog<Boolean> dialog = new Dialog<Boolean>(owner,
				Modality.APPLICATION_MODAL, StageStyle.DECORATED, title) {
			@Override
			protected Node createCenterContent() {
				ImageView icon = ImageResource.getImageView(type);
				HBox hbox = HBoxBuilder.create()
						.padding(new Insets(0, 5, 5, 5))
						.alignment(Pos.TOP_LEFT)
						.children(icon, new Label(message)).build();
				return hbox;
			}
		};
		if (CSS_PATH != null)
			dialog.setStylesheetLocation(CSS_PATH);
		dialog.setMinSize(100, 400);
		dialog.addButton(ButtonID.OK);
		return dialog;
	}
}
