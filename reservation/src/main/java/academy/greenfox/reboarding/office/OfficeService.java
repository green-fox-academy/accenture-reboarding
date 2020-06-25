package academy.greenfox.reboarding.office;

import academy.greenfox.reboarding.seat.Seat;

import java.util.Map;

public interface OfficeService {

  Office registerOffice(Office office);

  Office setupSeats(String officeId, SeatSetupDTO seats) throws NoSuchOfficeException;

  Map<Integer, Seat> convertToSeats(Office office, SeatSetupDTO seats);

}
