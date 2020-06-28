package academy.greenfox.reboarding.imagerecognition.rest;

import java.util.List;

import academy.greenfox.reboarding.imagerecognition.image.Position;
import lombok.Data;

@Data
public class MarkRequest {
  String layoutId;
  List<Position> positions;
}
