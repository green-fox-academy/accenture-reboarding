package academy.greenfox.reboarding.entry;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class EntryFactory {

  public static Entry create() {
    return create(
      EntryStatus.values()[new Random().nextInt(EntryStatus.values().length)]);
  }

  public static Entry create(String userId, EntryStatus status) {
    return Entry.builder()
    .day(LocalDate.now())
    .userId(userId)
    .enteredAt(LocalDateTime.now())
    .leftAt(LocalDateTime.now().plusMinutes(1))
    .status(status)
    .createdAt(LocalDateTime.now())
    .build();
  }

  public static Entry create(EntryStatus status) {
    return create(UUID.randomUUID().toString(), status);
  }

  public static Entry createRequest() {
    return Entry.builder()
    .userId("userId")
    .day(LocalDate.now())
    .build();
  }
}
