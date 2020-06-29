package academy.greenfox.reboarding.imagerecognition.rest;

import java.util.List;

import academy.greenfox.reboarding.imagerecognition.image.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatSetup {

  private List<Position> matches;
  private String layoutId;

}
