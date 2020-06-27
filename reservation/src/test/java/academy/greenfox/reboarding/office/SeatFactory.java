package academy.greenfox.reboarding.office;

import academy.greenfox.reboarding.seat.Position;
import academy.greenfox.reboarding.seat.Seat;
import academy.greenfox.reboarding.seat.SeatStatus;

import java.util.concurrent.atomic.AtomicInteger;

public class SeatFactory {

  private static AtomicInteger idGenerator = new AtomicInteger();

  public static Seat create(double x, double y) {
    return Seat.builder()
        .id(idGenerator.getAndIncrement())
        .position(new Position(x, y))
        .status(SeatStatus.FREE)
        .build();
  }

}
