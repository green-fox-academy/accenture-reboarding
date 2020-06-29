package academy.greenfox.reboarding.office;

import academy.greenfox.reboarding.externalservices.MarkResponse;
import academy.greenfox.reboarding.seat.Seat;

import java.util.List;

public interface OfficeReservationService {

  Office updateOffice(String officeId);

  List<Seat> updateSeats(Office office, List<Seat> fetchedSeats);

  Seat reserveASeat(String officeId, String userId) throws NoSuchOfficeException;

  MarkResponse visualStatus(String officeId) throws NoSuchOfficeException;

}
