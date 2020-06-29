package academy.greenfox.reboarding.imagerecognition.image;

import java.util.List;

import academy.greenfox.reboarding.imagerecognition.rest.dto.MarkRequest;

public interface ImageService {
  String storeLayout(String url);
  String storeTemplate(String url);
  String markLayout(MarkRequest layoutsAndPositions);
  void storeImageLocally(String url, String path);
  List<Position> processLayout(String localPath, String templatePath);
}
