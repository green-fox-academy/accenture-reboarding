package academy.greenfox.reboarding.entry;

import academy.greenfox.reboarding.office.NoSuchOfficeException;
import academy.greenfox.reboarding.seat.Seat;

public interface EntryService {

  EntryDTO create(EntryRequest entry) throws RegisterException, NoSuchOfficeException;

  String generateSeatLayout(Seat seat);

  EntryDTO read(String userId);

  EntryDTO convert(Entry entry);

  Entry convert(EntryRequest entryRequest);

  int getWaitListSize(Entry entry);

  EntryDTO enter(String userId) throws EnterException;

  EntryDTO leave(String userId);

}
