package academy.greenfox.reboarding.imagerecognition.image;

import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

@Service
public class OpenCvWrapper {
  private static final int SMOOTHING_THRESHOLD = 4;

  public Mat read(String filename) {
    return Imgcodecs.imread(filename);
  }

  public void write(String filename, Mat image) {
    Imgcodecs.imwrite(filename, image);
  }

  public void rect(Mat image, Point p1, Point p2, Scalar color, int thickness) {
    Imgproc.rectangle(image, p1, p2, color, thickness);
  }

  public void flip(Mat image, Mat result, int flipCode) {
    Core.flip(image, result, flipCode);
  }

  public void match(Mat image, Mat template, Mat result, int method) {
    Imgproc.matchTemplate(image, template, result, method);
  }

  public void normalize(Mat src, Mat dst, double alpha, double beta, int norm_type, int dtype, Mat mask) {
    Core.normalize(src, dst, alpha, beta, norm_type, dtype, mask);
  }

  public void inRange(Mat src, Scalar lowerb, Scalar upperb, Mat dst) {
    Core.inRange(src, lowerb, upperb, dst);
  }

  public Core.MinMaxLocResult minMaxLoc(Mat src) {
    return Core.minMaxLoc(src);
  }

  public Mat copy(Mat original) {
    Mat copied = new Mat();
    original.copyTo(copied);
    return copied;
  }

  public Mat rotate(Mat source) {
    Mat destination = new Mat(source.rows(), source.cols(), source.type());
    flip(source, destination, -1);
    return destination;
  }

  public void createMatches(Mat layout, Mat template, Mat img_display, List<Position> positions) {
    Mat result = new Mat();
    match(layout, template, result, Imgproc.TM_CCOEFF_NORMED);
    normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
    Point matchLoc;
    Mat mask = new Mat();
    inRange(layout, new Scalar(200, 0, 0), new Scalar(240, 255, 255), mask);
    
    while (true) {
      Core.MinMaxLocResult mmr = minMaxLoc(result);
      matchLoc = mmr.maxLoc;
      if (mmr.maxVal > 0.8) {
        if (checkMask(mask, (int)matchLoc.x, (int)matchLoc.y)
          && checkMask(mask, (int)matchLoc.x + template.cols()-10, (int)matchLoc.y + template.rows()-10)
          && !checkOverlap(positions, new Position(matchLoc.x, matchLoc.y), template.cols(), template.rows())
        ) {
          positions.add(new Position(matchLoc.x, matchLoc.y));
        }
        rect(result, matchLoc, new Point(matchLoc.x + template.cols(), matchLoc.y + template.rows()),
        new Scalar(0, 0, 0), -1);
        mmr = minMaxLoc(result);
      } else {
        break;
      }
    }
  }

  private boolean checkOverlap(List<Position> positions, Position against, int width, int height) {
    for (Position position : positions) {
      if(positionsOverlap(position, against, width, height)) {
        return true;
      }
    }
    return false;
  }

  private boolean positionsOverlap(Position l1, Position l2, int width, int height) {
    Position r1 = new Position(l1.getX() + width - SMOOTHING_THRESHOLD, l1.getY() + height);
    Position r2 = new Position(l2.getX() + width - SMOOTHING_THRESHOLD, l2.getY() + height);
    if (l1.getX() >= r2.getX() || l2.getX() >= r1.getX()) { 
        return false; 
    }
    if (l1.getY() >= r2.getY() || l2.getY() >= r1.getY()) { 
        return false; 
    }
    return true; 
  }

  private boolean checkMask(Mat mask, int x, int y) {
    int distance = 10;
    int sample = 1;
    int count = 0;
    float treshold = 0.4f;
    for(int i = 0; i < distance; i += sample) {
      for(int j = 0; j < distance; j += sample) {
        if (y+j < mask.rows() && x+i < mask.cols() && mask.get(y+j, x+i)[0] == 255) {
          count++;
        }
      }
    }
    return ((float)count / (distance*distance)) > treshold;
  }
}
