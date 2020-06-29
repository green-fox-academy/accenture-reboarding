package academy.greenfox.reboarding.office;

import academy.greenfox.reboarding.externalservices.MarkRequest;
import academy.greenfox.reboarding.externalservices.MarkResponse;
import academy.greenfox.reboarding.officerule.OfficeRules;
import academy.greenfox.reboarding.seat.Position;
import academy.greenfox.reboarding.seat.Seat;
import academy.greenfox.reboarding.seat.SeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OfficeReservationServiceImpl implements OfficeReservationService {

  private OfficeRepository officeRepository;
  private OfficeRules officeRules;
  private WebClient officeService;
  private WebClient imageService;

  public OfficeReservationServiceImpl(OfficeRepository officeRepository,
                                      OfficeRules officeRules,
                                      @Qualifier("OfficeService") WebClient officeService,
                                      @Qualifier("ImageService") WebClient imageService) {
    this.officeRepository = officeRepository;
    this.officeRules = officeRules;
    this.officeService = officeService;
    this.imageService = imageService;
  }

  @Override
  public Office updateOffice(String officeId) throws NoSuchOfficeException {
    Office updatedOffice = fetchOffice(officeId).orElseThrow(NoSuchOfficeException::new);
    Optional<Office> officeOp = officeRepository.findById(officeId);
    if (!officeOp.isPresent()) {
      Office office = new Office(officeId);
      List<Seat> seats = applyOfficeRules(
          updatedOffice.seats.stream()
              .map(s -> {
                s.setOffice(office);
                return s;
              })
              .collect(Collectors.toList()),
          0);
      updatedOffice.setSeats(seats);
      return officeRepository.save(updatedOffice);
    }
    Office office = officeOp.get();
    List<Seat> updatesSeats = updateSeats(office, updatedOffice.getSeats());
    office.setSeats(updatesSeats);
    return officeRepository.save(office);
  }

  @Override
  public List<Seat> updateSeats(Office office, List<Seat> fetchedSeats) {
    List<Seat> updatedSeats = removeClosedSeats(office.getSeats(), fetchedSeats);
    updatedSeats = addNewSeats(updatedSeats, fetchedSeats, office);
    int lastIndex = office.getSeats().get(office.getSeats().size()).getId();
    List<Seat> verifiedSeats = applyOfficeRules(updatedSeats, lastIndex);
    return verifiedSeats;
  }

  @Override
  public Seat reserveASeat(String officeId, String userId) throws NoSuchOfficeException {
    Optional<Office> officeOp = officeRepository.findById(officeId);
    Office office = officeOp.isPresent() ? officeOp.get() : updateOffice(officeId);
    List<Seat> freeSeats = office.getSeats().parallelStream()
        .filter(s -> s.getStatus().equals(SeatStatus.FREE))
        .collect(Collectors.toList());
    if (freeSeats.isEmpty()) return null;
    Seat seat = freeSeats.get(0);
    seat.setStatus(SeatStatus.RESERVED);
    seat.setMessage(userId);
    return seat;
  }

  @Override
  public MarkResponse visualStatus(String officeId) throws NoSuchOfficeException {
    Optional<Office> officeOp = officeRepository.findById(officeId);
    Office office = officeOp.isPresent() ? officeOp.get() : updateOffice(officeId);
    MarkRequest req = MarkRequest.builder()
        .layoutId(office.getLayoutId())
        .free(findSeats(office, SeatStatus.FREE))
        .reserved(findSeats(office, SeatStatus.RESERVED))
        .inUse(findSeats(office, SeatStatus.IN_USE))
        .build();
    return imageService.put()
        .uri("/layout")
        .body(Mono.just(req), MarkRequest.class)
        .retrieve()
        .bodyToMono(MarkResponse.class)
        .block();
  }

  public List<Position> findSeats(Office office, SeatStatus free) {
    return office.seats.stream()
        .filter(s -> s.getStatus().equals(SeatStatus.FREE))
        .map(s -> s.getPosition())
        .collect(Collectors.toList());
  }

  public List<Seat> applyOfficeRules(List<Seat> seats, int lastIndex) {
    Map<Integer, Seat> seatMap = buildSeatMapById(seats, lastIndex);
    officeRules.rules.forEach(rule -> rule.apply(seatMap));
    return seatMap.values().stream().collect(Collectors.toList());
  }

  public List<Seat> addNewSeats(List<Seat> updatedSeats, List<Seat> fetchedSeats, Office office) {
    Map<double[], Seat> seatMap = buildSeatMapByPosition(updatedSeats);
    fetchedSeats.parallelStream()
        .forEach(s -> {
          if (!seatMap.containsKey(s.getPosition().withKey().getKey())) {
            s.setOffice(office);
            updatedSeats.add(s);
          }
        });
    return updatedSeats;
  }

  public List<Seat> removeClosedSeats(List<Seat> seats, List<Seat> fetchedSeats) {
    Map<double[], Seat> fetchedSeatMap = buildSeatMapByPosition(fetchedSeats);
    List<Seat> remainingSeats = new ArrayList<>();
    List<Seat> removedSeats = new ArrayList<>();
    seats.forEach(s -> {
      if (fetchedSeatMap.containsKey(s.getPosition().withKey().getKey())) {
        remainingSeats.add(s);
      } else {
        removedSeats.add(s);
      }
    });
    // TODO: do something with removed seats
    return remainingSeats;
  }

  public Map<Integer, Seat> buildSeatMapById(List<Seat> seats, int lastIndex) {
    AtomicInteger idGenerator = new AtomicInteger(lastIndex + 1);
    return seats.stream()
        .map(s -> {
          if (s.getId() == null) {
            s.setId(idGenerator.getAndIncrement());
          }
          return s;
        })
        .collect(Collectors.toMap(Seat::getId, s -> s));
  }


  public Map<double[], Seat> buildSeatMapByPosition(List<Seat> seats) {
    return seats.parallelStream()
        .map(s -> {
          s.setPosition(s.getPosition().withKey());
          return s;
        })
        .collect(Collectors.toMap(s -> s.getPosition().getKey(), s -> s));
  }

  public Optional<Office> fetchOffice(String officeId) {
    WebClient.ResponseSpec response = officeService.get()
        .uri(String.format("/office/%s", officeId))
        .retrieve();
    try {
      return Optional.of(response.bodyToMono(Office.class).block());
    } catch (Exception e) {
      return Optional.empty();
    }
  }

}
