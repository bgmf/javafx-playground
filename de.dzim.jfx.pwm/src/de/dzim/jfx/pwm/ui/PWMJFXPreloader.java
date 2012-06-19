package de.dzim.jfx.pwm.ui;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class PWMJFXPreloader extends Preloader {

	public void start(Stage primaryStage) throws Exception {

		Image img = new Image(this.getClass().getResourceAsStream(
				"resource/splash.bmp"));

		if (img != null) {

			GridPane grid = new GridPane();
			grid.add(new ImageView(img), 1, 1);
			grid.add(new Label("PWM - The Password Manager"), 1, 2);

			Scene scene = new Scene(grid);
			primaryStage.setScene(scene);
			primaryStage.show();
		}
	}
}
