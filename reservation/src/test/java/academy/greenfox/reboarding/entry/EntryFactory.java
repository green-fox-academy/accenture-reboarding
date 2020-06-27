package academy.greenfox.reboarding.entry;

import academy.greenfox.reboarding.office.SeatFactory;
import academy.greenfox.reboarding.seat.Seat;

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
    .officeId("A66")
    .seat(SeatFactory.create(100, 100))
    .enteredAt(LocalDateTime.now())
    .leftAt(LocalDateTime.now().plusMinutes(1))
    .status(status)
    .createdAt(LocalDateTime.now())
    .build();
  }

  public static Entry create(EntryStatus status) {
    return create(UUID.randomUUID().toString(), status);
  }

  public static EntryRequest createRequest() {
    return EntryRequest.builder()
    .userId("chuck")
    .officeId("A66")
    .day(LocalDate.now())
    .build();
  }
}
