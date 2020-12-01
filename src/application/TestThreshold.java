package application;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.*;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;

public class TestThreshold {
	static boolean exit = false;
	static int lowH = 60;
	static int highH = 255 / 2;
	static int lowS = 0;
	static int highS = 255;
	static int lowV = 0;
	static int highV = 255;
	static VideoCapture camera;
	static Processor processor;
	static JFrame frame;
	static Mat extImg;
	static JLabel processedLabel;
	static JLabel originalLabel;
	
	public TestThreshold() {
		extImg = new Mat();
		
		camera = new VideoCapture(0);
		camera.read(extImg);
		
		frame = new JFrame("TestThreshold");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				processor.cancel(true);
				System.exit(0);
			}
		});
		
		originalLabel = new JLabel(new ImageIcon(HighGui.toBufferedImage(extImg)));
		processedLabel = new JLabel(new ImageIcon(HighGui.toBufferedImage(threshold(extImg))));
		
		frame.getContentPane().add(BorderLayout.EAST, originalLabel);
		frame.getContentPane().add(BorderLayout.WEST, processedLabel);
		
		frame.pack();
		frame.setVisible(true);
		processor = new Processor();
		processor.execute();
	}
	
	class Processor extends SwingWorker<Void, Mat> {

		@Override
		protected Void doInBackground() throws Exception {
			// TODO Auto-generated method stub
			Mat frame = new Mat();
			while(!isCancelled()) {
				camera.read(frame);
				publish(frame.clone());
			}
			return null;
		}
		
		
		@Override
		protected void process(List<Mat> mats) {
			Mat originalMat = mats.get(0);
			Mat processedMat = threshold(originalMat);
			processedLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(processedMat)));
			originalLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(originalMat)));
			frame.repaint();
		}
	}
	
	public static Mat threshold(Mat img) {
		Mat hsvImg = new Mat();
		Mat processedImg = new Mat();
		
		Imgproc.cvtColor(img, hsvImg, Imgproc.COLOR_BGR2HSV);
		Core.inRange(hsvImg, new Scalar(lowH, lowS, lowV), new Scalar(highH, highS, highV), processedImg);
		
		return processedImg;
	}
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new TestThreshold();
			}
		});
	}

}
