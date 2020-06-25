package academy.greenfox.reboarding.office;

import academy.greenfox.reboarding.seat.Position;
import lombok.Data;

import java.util.List;

@Data
public class SeatSetupDTO {

  List<Position> matches;

}
