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
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import academy.greenfox.reboarding.imagerecognition.rest.dto.MarkRequest;

@Service
public class ImageServiceImpl implements ImageService {

  private static final String LAYOUTS_PATH = "layouts/";
  private static final String TEMPLATES_PATH = "templates/";
  private static final String MARKS_PATH = "marks/";
  private static final String JPG_EXT = ".jpg";
  private static final int MARK_WIDTH = 18;
  private static final int MARK_HEIGHT = 9;

  private OpenCvWrapper opencv;

  AtomicInteger layouts = new AtomicInteger();
  AtomicInteger templates = new AtomicInteger();
  AtomicInteger marks = new AtomicInteger();

  public ImageServiceImpl(OpenCvWrapper opencv) throws IOException {
    createDirIfNotExists(LAYOUTS_PATH);
    createDirIfNotExists(MARKS_PATH);
    createDirIfNotExists(TEMPLATES_PATH);
    this.opencv = opencv;
  }

  private void createDirIfNotExists(String dir) throws IOException {
    Path path = Paths.get(dir);
    if(!Files.exists(path)) {
      Files.createDirectory(path);
    }
  }

  @Override
  public String storeLayout(String url) {
    String localFilename = layouts.incrementAndGet() + JPG_EXT;
    storeImageLocally(url, LAYOUTS_PATH + localFilename);
    return localFilename;
  }

  @Override
  public String storeTemplate(String url) {
    String localFilename = templates.incrementAndGet() + JPG_EXT;
    storeImageLocally(url, TEMPLATES_PATH + localFilename);
    return localFilename;
  }

  @Override
  public String markLayout(MarkRequest data) {
    if(data.getLayoutId() == null) {
      data.setLayoutId(LAYOUTS_PATH + layouts.get() + JPG_EXT);
    }
    Mat mat = opencv.read(LAYOUTS_PATH + data.getLayoutId());
    markPositionsWithColor(mat, data.getReserved(), new Scalar(0, 0, 255));
    markPositionsWithColor(mat, data.getFree(), new Scalar(0, 255, 0));
    markPositionsWithColor(mat, data.getInUse(), new Scalar(255, 0, 0));
    String markPath = MARKS_PATH + marks.getAndIncrement() + JPG_EXT;
    opencv.write(markPath, mat);
    return markPath;
  }

  private void markPositionsWithColor(Mat image, List<Position> positions, Scalar color) {
    if (positions == null) return;
    for (Position position : positions) {
      opencv.rect(image, new Point(position.getX(), position.getY()), new Point(position.getX() + MARK_WIDTH, position.getY() + MARK_HEIGHT),
        color, 2);
    }
  }
  
  @Override
  public void storeImageLocally(String url, String path) {
    try (InputStream in = new URL(url).openStream()) {
      Files.deleteIfExists(Paths.get(path));
      Files.copy(in, Paths.get(path));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public List<Position> processLayout(String localPath, String templatePath) {
    Mat layout = opencv.read(LAYOUTS_PATH + localPath);
    Mat template = opencv.read(TEMPLATES_PATH + templatePath);
    List<Position> positions = new ArrayList<>();
    Mat img_display = opencv.copy(layout);
    opencv.createMatches(layout, template, img_display, positions);
    Mat template180 = opencv.rotate(template);
    opencv.createMatches(layout, template180, img_display, positions);
    return positions;
  }

}
