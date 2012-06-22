package de.dzim.jfx.pwm.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import de.dzim.jfx.pwm.handler.PWMActionEventHandler;
import de.dzim.jfx.pwm.handler.PWMActionEventHandler.Type;
import de.dzim.jfx.pwm.ui.component.MainWindow;
import de.dzim.jfx.ui.dialog.MessageDialog;
import de.dzim.jfx.ui.resource.ImageResource;
import de.dzim.jfx.ui.resource.ImageResource.ImageResourceType;

public class PWMJFXApplication extends Application {

	public static final String CSS = "de/dzim/jfx/pwm/ui/pwm.css";
	public static final String CSS_DIALOG = "de/dzim/jfx/pwm/ui/pwm-dialog.css";

	public static final String APPLICATION_TITLE = "PWM - The Password Manager";

	public static boolean useCustomDecoration = false;

	@Override
	public void start(final Stage primaryStage) {

		Node node = null;
		try {
			node = FXMLLoader.<Node> load(PWMJFXApplication.class
					.getResource("DecorationArea.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (useCustomDecoration && node != null) {
			primaryStage.initStyle(StageStyle.UNDECORATED);
		}

		primaryStage.setTitle(APPLICATION_TITLE);
		primaryStage.getIcons().add(
				ImageResource.getImage(ImageResourceType.LOCK_32));

		GridPane grid = new GridPane();
		grid.setId("root-grid-pane");

		if (useCustomDecoration && node != null)
			grid.add(node, 0, 0);

		final MainWindow window = new MainWindow(primaryStage, grid);
		window.setCreateMenu(true);
		window.createContent();

		Scene scene = new Scene(grid, 1024, 800);
		scene.getStylesheets().add(CSS);

		MessageDialog.CSS_PATH = CSS_DIALOG;

		primaryStage.setScene(scene);
		primaryStage.show();

		primaryStage
				.setOnCloseRequest(new javafx.event.EventHandler<WindowEvent>() {
					@Override
					public void handle(WindowEvent event) {
						Boolean dirty = (Boolean) window
								.getInternalAdapter(Boolean.class);
						if (dirty) {
							boolean confirm = MessageDialog
									.showQuestion(primaryStage,
											"Unsaved data!",
											"Do you want to save your data before exiting the program?");
							if (!confirm)
								return;
							PWMActionEventHandler handler = new PWMActionEventHandler(
									Type.SAVE, window);
							handler.handle(new ActionEvent());
						}
					}
				});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
