package application;
	
import java.io.IOException;

import org.opencv.core.Core;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws IOException{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		int scaler = 4;
		
		WritableImage ogWritable = SwingFXUtils.toFXImage(OpenCVTests.loadImage(OpenCVTests.file.path, false), null);
		WritableImage processedWritable = SwingFXUtils.toFXImage(OpenCVTests.loadImage(OpenCVTests.file.path, true), null);
		
		ImageView ogImg = new ImageView(ogWritable);
		ImageView processedImg = new ImageView(processedWritable);
		
		GridPane root = new GridPane();
		
		ogImg.setFitWidth(OpenCVTests.file.width / scaler);
		ogImg.setFitHeight(Math.round(OpenCVTests.file.height / scaler));
		ogImg.setPreserveRatio(true);
		
		processedImg.setFitWidth(OpenCVTests.file.width / scaler);
		processedImg.setFitHeight(Math.round(OpenCVTests.file.height / scaler));
		processedImg.setPreserveRatio(true);
		
		root.add(ogImg, 0, 0);
		root.add(processedImg, OpenCVTests.file.width / scaler, 0);
		
		Scene scene = new Scene(root, OpenCVTests.file.width / scaler * 2, OpenCVTests.file.height / scaler);
		
		primaryStage.setTitle("Creepy");
		
		primaryStage.setScene(scene);
		
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
