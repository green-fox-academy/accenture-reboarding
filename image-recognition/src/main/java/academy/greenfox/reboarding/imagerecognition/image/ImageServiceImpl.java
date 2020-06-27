package academy.greenfox.reboarding.imagerecognition.image;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {

  private static final String LAYOUTS_PATH = "layouts/";
  private static final String TEMPLATES_PATH = "templates/";
  private static final String MARKS_PATH = "marks/";
  private static final int SMOOTHING_THRESHOLD = 4;
  private static final int MARK_WIDTH = 18;
  private static final int MARK_HEIGHT = 9;

  AtomicInteger layouts = new AtomicInteger();
  AtomicInteger templates = new AtomicInteger();
  AtomicInteger marks = new AtomicInteger();

  public ImageServiceImpl() throws IOException {
    createDirIfNotExists(LAYOUTS_PATH);
    createDirIfNotExists(MARKS_PATH);
    createDirIfNotExists(TEMPLATES_PATH);
  }

  private void createDirIfNotExists(String dir) throws IOException {
    Path path = Paths.get(dir);
    if(!Files.exists(path)) {
      Files.createDirectory(path);
    }
  }

  @Override
  public String storeLayout(String url) {
    String localFilename = layouts.incrementAndGet() + ".jpg";
    storeImageLocally(url, LAYOUTS_PATH + localFilename);
    return localFilename;
  }

  @Override
  public String storeTemplate(String url) {
    String localFilename = templates.incrementAndGet() + ".jpg";
    storeImageLocally(url, TEMPLATES_PATH + localFilename);
    return localFilename;
  }

  @Override
  public String markLayout(String layout, List<Position> positions) {
    if(layout == null) {
      layout = LAYOUTS_PATH + layouts.get() + ".jpg"; 
    }
    Mat mat = Imgcodecs.imread(LAYOUTS_PATH + layout);
    for (Position position : positions) {
      Imgproc.rectangle(mat, new Point(position.getX(), position.getY()), new Point(position.getX() + MARK_WIDTH, position.getY() + MARK_HEIGHT),
        new Scalar(0, 0, 255), 2);
    }
    String markPath = MARKS_PATH + marks.getAndIncrement() + ".jpg";
    Imgcodecs.imwrite(markPath, mat);
    return markPath;
  }
  
  @Override
  public void storeImageLocally(String url, String path) {
    try (InputStream in = new URL(url).openStream()) {
      Files.copy(in, Paths.get(path));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public List<Position> processLayout(String localPath, String templatePath) {
    Mat layout = Imgcodecs.imread(LAYOUTS_PATH + localPath);
    Mat template = Imgcodecs.imread(TEMPLATES_PATH + templatePath);
    Mat img_display = new Mat();
    List<Position> positions = new ArrayList<>();
    layout.copyTo(img_display);
    createMatches(layout, template, img_display, positions);
    Mat template180 = rotate(template);
    createMatches(layout, template180, img_display, positions);

    return positions;
  }

  public static Mat rotate(Mat source) {
    Mat destination = new Mat(source.rows(), source.cols(), source.type());
    Core.flip(source, destination, -1);
    return destination;
  }

  public static void createMatches(Mat layout, Mat template, Mat img_display, List<Position> positions) {
    Mat result = new Mat();
    Imgproc.matchTemplate(layout, template, result, Imgproc.TM_CCOEFF_NORMED);
    Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
    Point matchLoc;
    Mat mask = new Mat();
    Core.inRange(layout, new Scalar(200, 0, 0), new Scalar(240, 255, 255), mask);
    
    while (true) {
      Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
      matchLoc = mmr.maxLoc;
      if (mmr.maxVal > 0.8) {
        if (checkMask(mask, (int)matchLoc.x, (int)matchLoc.y)
          && checkMask(mask, (int)matchLoc.x + template.cols()-10, (int)matchLoc.y + template.rows()-10)
          && !checkOverlap(positions, new Position(matchLoc.x, matchLoc.y), template.cols(), template.rows())
        ) {
          positions.add(new Position(matchLoc.x, matchLoc.y));
        }
        Imgproc.rectangle(result, matchLoc, new Point(matchLoc.x + template.cols(), matchLoc.y + template.rows()),
        new Scalar(0, 0, 0), -1);
        mmr = Core.minMaxLoc(result);
      } else {
        break;
      }
    }
  }

  private static boolean checkOverlap(List<Position> positions, Position against, int width, int height) {
    for (Position position : positions) {
      if(positionsOverlap(position, against, width, height)) {
        return true;
      }
    }
    return false;
  }

  private static boolean positionsOverlap(Position l1, Position l2, int width, int height) {
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

  private static boolean checkMask(Mat mask, int x, int y) {
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
