package academy.greenfox.reboarding.officerule;

import academy.greenfox.reboarding.seat.Seat;
import academy.greenfox.reboarding.office.SeatFactory;
import academy.greenfox.reboarding.seat.SeatStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinimalDistanceTest {

  private MinimalDistance minimalDistance;
  private SeatFactory seatFactory;
  private int minimalMetresFromEachOther;
  private int pixelsPerMeter;
  private int minimalPixels;

  @BeforeEach
  public void setUp() {
    minimalDistance = new MinimalDistance();
    seatFactory = new SeatFactory();
    minimalMetresFromEachOther = 5;
    pixelsPerMeter = 10;
    minimalPixels = minimalMetresFromEachOther * pixelsPerMeter;
    minimalPixels = minimalMetresFromEachOther * pixelsPerMeter;
    ReflectionTestUtils.setField(minimalDistance, "minimalMetresFromEachOther", minimalMetresFromEachOther);
    ReflectionTestUtils.setField(minimalDistance, "pixelsPerMeter", pixelsPerMeter);
    ReflectionTestUtils.setField(minimalDistance, "minimalPixels", minimalPixels);
  }

  @Test
  public void testApplyMarksTooCloseSeats() {
    Seat seat1 = seatFactory.create(10, 10);
    Seat seat2 = seatFactory.create(10 + minimalPixels - 1, 10);
    Seat seat3 = seatFactory.create(10 + minimalPixels / 2, 10 + minimalPixels / 2);
    Map<Integer, Seat> seats = new HashMap<Integer, Seat>(){{
      put(seat1.getId(), seat1);
      put(seat2.getId(), seat2);
      put(seat3.getId(), seat3);
    }};

    seats = minimalDistance.apply(seats);

    assertEquals(SeatStatus.FREE, seats.get(seat1.getId()).getStatus());
    assertEquals(SeatStatus.NOT_AVAILABLE, seats.get(seat2.getId()).getStatus());
    assertEquals(MinimalDistance.MESSAGE, seats.get(seat2.getId()).getMessage());
    assertEquals(SeatStatus.NOT_AVAILABLE, seats.get(seat3.getId()).getStatus());
    assertEquals(MinimalDistance.MESSAGE, seats.get(seat3.getId()).getMessage());
  }

  @Test
  public void testApplyKeepsOutOfDistanceSeatsFree() {
    Seat seat1 = seatFactory.create(10, 10);
    Seat seat2 = seatFactory.create(10 + minimalPixels, 10);
    Seat seat3 = seatFactory.create(10 + 2 * minimalPixels, 10);
    Map<Integer, Seat> seats = new HashMap<Integer, Seat>(){{
      put(seat1.getId(), seat1);
      put(seat2.getId(), seat2);
      put(seat3.getId(), seat3);
    }};

    seats = minimalDistance.apply(seats);

    assertEquals(SeatStatus.FREE, seats.get(seat1.getId()).getStatus());
    assertEquals(SeatStatus.FREE, seats.get(seat2.getId()).getStatus());
    assertEquals(SeatStatus.FREE, seats.get(seat3.getId()).getStatus());
  }

  @Test
  public void testApplyWithRandomPositions() {
    Seat seat = seatFactory.create(10, 10);
    Seat closeSeat = seatFactory.create(
        10 + Math.random() * minimalPixels / 2,
        10 + Math.random() * minimalPixels / 2);
    Seat farSeat = seatFactory.create(
        10 + minimalPixels + (Math.random() * minimalPixels),
        10 + minimalPixels + (Math.random() * minimalPixels));
    Map<Integer, Seat> seats = new HashMap<Integer, Seat>(){{
      put(seat.getId(), seat);
      put(closeSeat.getId(), closeSeat);
      put(farSeat.getId(), farSeat);
    }};

    seats = minimalDistance.apply(seats);

    assertEquals(SeatStatus.FREE, seats.get(seat.getId()).getStatus());
    assertEquals(SeatStatus.NOT_AVAILABLE, seats.get(closeSeat.getId()).getStatus());
    assertEquals(SeatStatus.FREE, seats.get(farSeat.getId()).getStatus());
  }

}
