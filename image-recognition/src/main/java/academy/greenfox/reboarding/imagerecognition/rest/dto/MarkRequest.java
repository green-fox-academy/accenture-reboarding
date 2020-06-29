package academy.greenfox.reboarding.imagerecognition.rest.dto;

import java.util.List;

import academy.greenfox.reboarding.imagerecognition.image.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarkRequest {
  String layoutId;
  List<Position> positions;
}
