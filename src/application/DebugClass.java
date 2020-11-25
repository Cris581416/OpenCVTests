package application;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

class DebugClass {
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Mat matrix = new Mat(5, 5, CvType.CV_8UC1, new Scalar(0));
		
		Mat row0 = matrix.row(0);
		
		row0.setTo(new Scalar(1));
		
		Mat col3 = matrix.col(3);
		
		col3.setTo(new Scalar(3));
		
		System.out.println("OpenCV Mat data:\n" + matrix.dump());
	}
}
