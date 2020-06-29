package academy.greenfox.officeservice.office;

import academy.greenfox.officeservice.seat.Seat;

import java.util.List;
import java.util.Map;

public interface OfficeService {

  Office findOffice(String id);

  Office registerOffice(OfficeRequest office);

  SeatSetup setupSeats(String floorPlanUrl);

  List<Seat> convertToSeats(Office office, SeatSetup seats);

  Office convert(OfficeRequest req);

}
