package application;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.*;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;

public class TestThreshold {
	static boolean exit = false;
	static int lowH = 0;
	static int highH = 20;
	static int lowS = 29;
	static int highS = 154;
	static int lowV = 25;
	static int highV = 145;
    static int RATIO = 3;
    static int KERNEL_SIZE = 3;
    static Size BLUR_SIZE = new Size(4,4);
    static int lowThresh = 0;
    static double minSize = 100 * 200;
    static int centerSize = 4;
	static VideoCapture camera;
	static Processor processor;
	static JFrame frame;
	static Mat extImg;
	static JLabel processedLabel;
	static JLabel originalLabel;
	static Random rng;
	
	public TestThreshold() {
		extImg = new Mat();
		
		try {
			camera = new VideoCapture(1);
			if(!camera.read(extImg)) {
				throw new Exception("No external webcam detected, using integrated cam!");
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			camera = new VideoCapture(0);
			camera.read(extImg);
		}
		
		rng = new Random(12345);
		
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
		Mat blurredImg = new Mat();
		Mat thresholdedImg = new Mat();
		Mat detectedEdges = new Mat();
		Mat processedImg;
		
		Imgproc.cvtColor(img, hsvImg, Imgproc.COLOR_BGR2HSV);
		Imgproc.blur(hsvImg, blurredImg, BLUR_SIZE);
		Core.inRange(hsvImg, new Scalar(lowH, lowS, lowV), new Scalar(highH, highS, highV), thresholdedImg);
		Imgproc.Canny(thresholdedImg, detectedEdges, lowThresh, lowThresh * RATIO, KERNEL_SIZE, false);
        processedImg = new Mat(hsvImg.size(), CvType.CV_8UC3, Scalar.all(0));
        thresholdedImg.copyTo(processedImg, detectedEdges);
        
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(thresholdedImg, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        MatOfPoint2f[] contoursPoly  = new MatOfPoint2f[contours.size()];
        Rect[] boundRect = new Rect[contours.size()];
        for (int i = 0; i < contours.size(); i++) {
            contoursPoly[i] = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contoursPoly[i], 3, true);
            boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));
        }
        Mat drawing = Mat.zeros(thresholdedImg.size(), CvType.CV_8UC3);
        List<MatOfPoint> contoursPolyList = new ArrayList<>(contoursPoly.length);
        for (MatOfPoint2f poly : contoursPoly) {
            contoursPolyList.add(new MatOfPoint(poly.toArray()));
        }
        for (int i = 0; i < contours.size(); i++) {
            Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
            if(boundRect[i].area() > minSize) {
            	Imgproc.drawContours(drawing, contoursPolyList, i, color);
            	Imgproc.rectangle(drawing, boundRect[i].tl(), boundRect[i].br(), color, 2);
            	Point center = new Point((boundRect[i].tl().x + boundRect[i].br().x) / 2, (boundRect[i].tl().y + boundRect[i].br().y) / 2);
            	Imgproc.rectangle(drawing, new Point(center.x - centerSize, center.y - centerSize), 
            					new Point(center.x + centerSize, center.y + centerSize), new Scalar(255, 255, 255), Imgproc.FILLED);
            }
        }
		
		return drawing;
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
