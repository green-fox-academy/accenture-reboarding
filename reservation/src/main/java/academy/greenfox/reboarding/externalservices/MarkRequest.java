package academy.greenfox.reboarding.externalservices;

import academy.greenfox.reboarding.seat.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkRequest {

  String layoutId;
  List<Position> free;
  List<Position> inUse;
  List<Position> reserved;

}
