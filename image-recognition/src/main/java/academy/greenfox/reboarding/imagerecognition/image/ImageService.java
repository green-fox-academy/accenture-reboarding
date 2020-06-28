package academy.greenfox.reboarding.imagerecognition.image;

import java.util.List;

public interface ImageService {
  String storeLayout(String url);
  String storeTemplate(String url);
  String markLayout(String layout, List<Position> positions);
  void storeImageLocally(String url, String path);
  List<Position> processLayout(String localPath, String templatePath);
}
