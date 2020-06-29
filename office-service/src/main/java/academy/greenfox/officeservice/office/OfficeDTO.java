package academy.greenfox.officeservice.office;

import academy.greenfox.officeservice.seat.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfficeDTO {

  String id;
  String layoutId;
  List<Seat> seats;

}
