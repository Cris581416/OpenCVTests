package application;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import utilities.Pic;

public class OpenCVTests {
	static Pic file = new Pic("C:/Users/cvela/Pictures/shovel2.jpg", 1920, 1202);
	
	public static void main(String[] args) throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		/*Mat matrix = new Mat(5, 5, CvType.CV_8UC1, new Scalar(0));
		Mat row0 = matrix.row(0);
		row0.setTo(new Scalar(1));
		Mat col3 = matrix.col(3);
		col3.setTo(new Scalar(3));*/
		
		BufferedImage image = loadImage(file.path, false);
		
		JFrame frame = new JFrame();
		
		frame.add(new JLabel(new ImageIcon(image)));
		frame.pack();
		frame.setVisible(true);		
	}
	
	protected static BufferedImage loadImage(String path, boolean process) throws IOException {
		Mat matrix;
		
		if(process) {
			Mat oldMatrix = Imgcodecs.imread(path, 0);
			
			matrix = new Mat();
			
			Imgproc.adaptiveThreshold(oldMatrix, matrix, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 20);
			
			//System.out.println(matrix.get(135, 91)[0]);
		} else {
			matrix = Imgcodecs.imread(path, 0);
		}
		
		MatOfByte byteMat = new MatOfByte();
		
		Imgcodecs.imencode(".png", matrix, byteMat);
		
		byte[] byteArray = byteMat.toArray();
		
		InputStream in = new ByteArrayInputStream(byteArray);
		
		BufferedImage image = ImageIO.read(in);
		
		return image;
	}
}
