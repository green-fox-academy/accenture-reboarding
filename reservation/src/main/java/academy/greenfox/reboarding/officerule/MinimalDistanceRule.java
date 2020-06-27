package academy.greenfox.reboarding.officerule;

import academy.greenfox.reboarding.seat.Seat;
import academy.greenfox.reboarding.seat.SeatStatus;
import com.harium.storage.kdtree.KDTree;
import com.harium.storage.kdtree.KeySizeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
final class MinimalDistanceRule implements OfficeRule {

  public static final String MESSAGE = "TOO CLOSE";
  Logger logger;
  @Value("${rule.minimalMetresFromEachOther}")
  private int minimalMetresFromEachOther;
  @Value("${rule.pixelsPerMeter}")
  private int pixelsPerMeter;
  private int minimalPixels;

  public MinimalDistanceRule() {
    logger = LoggerFactory.getLogger(getClass());
  }

  @Override
  public Map<Integer, Seat> apply(Map<Integer, Seat> seats) {
    minimalPixels = minimalMetresFromEachOther * pixelsPerMeter;
    KDTree<Integer> seatTree = createSeatTree(seats);
    return applyRule(seats, seatTree);
  }

  public Map<Integer, Seat> applyRule(Map<Integer, Seat> availableSeats, KDTree<Integer> seatTree) {
    availableSeats.values().forEach(seat -> {
      if (seat.getStatus().equals(SeatStatus.NOT_AVAILABLE)) return;
      markCloseSeatsNotAvailable(availableSeats, seatTree, seat);
    });
    return availableSeats;
  }

  public Map<Integer, Seat> markCloseSeatsNotAvailable(Map<Integer, Seat> availableSeats,
                                                       KDTree<Integer> seatTree,
                                                       Seat seat) {
    try {
      List<Integer> nearbySeatIds = seatTree.nearestEuclidean(seat.getPosition().getKey(), minimalPixels);
      nearbySeatIds.parallelStream()
        .filter(id -> availableSeats.get(id).getPosition().getKey() != seat.getPosition().getKey())
        .forEach(id -> {
          availableSeats.get(id).setStatus(SeatStatus.NOT_AVAILABLE);
          availableSeats.get(id).setMessage(MESSAGE);
        });
    } catch (KeySizeException e) {
      logger.error("Unable to filter seat tree.");
    }
    return availableSeats;
  }


  public KDTree<Integer> createSeatTree(Map<Integer, Seat> availableSeats) {
    KDTree<Integer> seatTree = new KDTree<>(2);
    availableSeats.entrySet().stream()
        .forEach(seat -> {
          try {
            seatTree.insert(seat.getValue().getPosition().getKey(), seat.getKey());
          } catch (Exception e) {
            logger.error("Unable to build seat tree.");
          }
        });
    return seatTree;
  }

}
