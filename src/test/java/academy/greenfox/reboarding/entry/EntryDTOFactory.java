package academy.greenfox.reboarding.entry;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EntryDTOFactory {

  public static EntryDTO create(String userId, EntryStatus status) {
    return create(userId, status, LocalDate.now());
  }

  public static EntryDTO create(String userId, EntryStatus status, LocalDate day) {
    return EntryDTO.builder()
        .userId(userId)
        .status(status)
        .day(day)
        .enteredAt(null)
        .leftAt(null)
        .waitListPosition(0)
        .build();
  }

  public static EntryDTO createEntered(String userId) {
    EntryDTO entered = create(userId, EntryStatus.ACCEPTED);
    entered.setEnteredAt(LocalDateTime.now());
    return entered;
  }

  public static EntryDTO createUsed(String userId) {
    EntryDTO used = create(userId, EntryStatus.USED);
    used.setEnteredAt(LocalDateTime.now());
    used.setLeftAt(LocalDateTime.now());
    return used;
  }
}