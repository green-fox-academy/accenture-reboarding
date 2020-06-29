package academy.greenfox.officeservice.office;

import academy.greenfox.officeservice.seat.Seat;

import java.util.List;

public interface OfficeService {

  OfficeDTO findOffice(String id);

  OfficeDTO registerOffice(OfficeRequest office);

  SeatSetup setupSeats(String floorPlanUrl);

  List<Seat> convertToSeats(Office office, SeatSetup seats);

  Office convert(OfficeRequest req);

}
