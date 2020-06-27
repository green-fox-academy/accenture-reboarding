package academy.greenfox.reboarding.office;

import academy.greenfox.reboarding.officerule.OfficeRules;
import academy.greenfox.reboarding.seat.Position;
import academy.greenfox.reboarding.seat.Seat;
import academy.greenfox.reboarding.seat.SeatStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OfficeServiceImpl implements OfficeService {

  private OfficeRepository officeRepository;
  private OfficeRules officeRules;

  public OfficeServiceImpl(OfficeRepository officeRepository, OfficeRules officeRules) {
    this.officeRepository = officeRepository;
    this.officeRules = officeRules;
  }

  @Override
  public Office registerOffice(Office office) {
    return officeRepository.save(office);
  }

  @Override
  public Office setupSeats(String officeId, SeatSetup seatSetup) throws NoSuchOfficeException {
    Office office = officeRepository.findById(officeId).orElseThrow(NoSuchOfficeException::new);
    Map<Integer, Seat> seats = convertToSeats(office, seatSetup);
    officeRules.rules.forEach(rule -> rule.apply(seats));
    office.setSeats(new ArrayList<>(seats.values()));
    return officeRepository.save(office);
  }

  @Override
  public Map<Integer, Seat> convertToSeats(Office office, SeatSetup seatSetup) {
    AtomicInteger idGenerator = new AtomicInteger();
    return seatSetup.matches.stream()
        .map(Position::withKey)
        .map(position -> Seat.builder()
                                .office(office)
                                .position(position)
                                .status(SeatStatus.FREE)
                                .build())
        .collect(Collectors.toMap(s -> idGenerator.getAndIncrement(), s -> s));
  }

  @Override
  public Seat reserveASeat(String officeId, String userId) throws NoSuchOfficeException {
    Office office = officeRepository.findById(officeId).orElseThrow(NoSuchOfficeException::new);
    List<Seat> freeSeats = office.getSeats().parallelStream()
        .filter(s -> s.getStatus().equals(SeatStatus.FREE))
        .collect(Collectors.toList());
    if (freeSeats.isEmpty()) return null;
    Seat seat = freeSeats.get(0);
    seat.setStatus(SeatStatus.RESERVED);
    seat.setMessage(userId);
    return seat;
  }

}
