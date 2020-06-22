package academy.greenfox.reboarding.imagerecognition;

import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.jni.Library;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoApplication {

	@GetMapping("/")
	public String test() {
		Mat layout = Imgcodecs.imread("layout.jpg");
		Mat template = Imgcodecs.imread("template2.jpg");
		Mat img_display = new Mat();
		layout.copyTo(img_display);
		Mat result = new Mat();
		Imgproc.matchTemplate(layout, template, result, Imgproc.TM_CCOEFF_NORMED);
		Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
		Point matchLoc;
		while(true) {
			Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
			matchLoc = mmr.maxLoc;
			System.out.println(matchLoc.toString());
			if(mmr.maxVal > 0.8) {
				Imgproc.rectangle(img_display, matchLoc, new Point(matchLoc.x + template.cols(), matchLoc.y + template.rows()),
										new Scalar(255, 0, 0), 2, 8, 0);
				Imgproc.rectangle(result, matchLoc, new Point(matchLoc.x + template.cols(), matchLoc.y + template.rows()),
								new Scalar(0, 0, 0), -1);
				mmr = Core.minMaxLoc(result);
				System.out.println(mmr.maxLoc.toString());
				System.out.println(mmr.maxVal);
			} else {
				break;
			}
		}
		Imgcodecs.imwrite("result.jpg", img_display);
		
		Mat mask = new Mat();
		Mat result2 = new Mat();
		layout.copyTo(result2);
		// Imgproc.cvtColor(layout, mask, Imgproc.COLOR_BGR2HSV_FULL);
		Core.inRange(layout, new Scalar(200, 0, 0), new Scalar(240, 255, 255), mask);
		Imgcodecs.imwrite("mask.jpg", mask);
		Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3));
		Mat opening = new Mat();
		Imgproc.morphologyEx(mask, opening, Imgproc.MORPH_OPEN, kernel);
		List<MatOfPoint> contours = new ArrayList<>();
		Mat contoursMat = new Mat();
		Imgproc.findContours(opening, contours, contoursMat, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		System.out.println(contours.size());
		for (int i = 0; i < contours.size(); i++) {
			System.out.println("drawing "+ i);
			Imgproc.drawContours(result2, contours, i, new Scalar(0, 0, 255), -1);
		}
		Imgcodecs.imwrite("result2.jpg", result2);
		return template.cols() + "-" + template.rows();
	}

	public static void main(String[] args) {
		Library.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		SpringApplication.run(DemoApplication.class, args);
	}

}
