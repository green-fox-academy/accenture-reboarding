package academy.greenfox.reboarding.office;

import academy.greenfox.reboarding.officerule.OfficeRules;
import academy.greenfox.reboarding.seat.Seat;
import academy.greenfox.reboarding.seat.SeatStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
  public Office setupSeats(String officeId, SeatSetupDTO seatSetup) throws NoSuchOfficeException {
    Office office = officeRepository.findById(officeId).orElseThrow(NoSuchOfficeException::new);
    Map<Integer, Seat> seats = convertToSeats(office, seatSetup);
    officeRules.rules.forEach(rule -> rule.apply(seats));
    office.setSeats(new ArrayList<>(seats.values()));
    return officeRepository.save(office);
  }

  @Override
  public Map<Integer, Seat> convertToSeats(Office office, SeatSetupDTO seatSetup) {
    AtomicInteger idGenerator = new AtomicInteger();
    return seatSetup.matches.parallelStream()
        .map(position -> position.withKey())
        .map(position -> Seat.builder()
                                .office(office)
                                .position(position)
                                .status(SeatStatus.FREE)
                                .build())
        .collect(Collectors.toMap(s -> idGenerator.getAndIncrement(), s -> s));
  }

}
