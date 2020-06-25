package academy.greenfox.reboarding.officerule;

import academy.greenfox.reboarding.seat.Seat;

import java.util.Map;

public interface OfficeRule {

  Map<Integer, Seat> apply(Map<Integer, Seat> seats);

}
