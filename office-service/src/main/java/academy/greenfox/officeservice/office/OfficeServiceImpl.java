package academy.greenfox.officeservice.office;

import academy.greenfox.officeservice.externalservices.LayoutRequest;
import academy.greenfox.officeservice.floorplan.FloorPlan;
import academy.greenfox.officeservice.seat.Seat;
import academy.greenfox.officeservice.seat.SeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfficeServiceImpl implements OfficeService {

  private OfficeRepository officeRepository;
  private WebClient imageService;
  @Value("${seat.template.url}")
  private String seatTemplate;

  public OfficeServiceImpl(OfficeRepository officeRepository, @Qualifier("ImageService") WebClient imageService) {
    this.officeRepository = officeRepository;
    this.imageService = imageService;
  }

  @Override
  public Office findOffice(String officeId) throws NoSuchOfficeException {
    return officeRepository.findById(officeId).orElseThrow(NoSuchOfficeException::new);
  }

  @Override
  public Office registerOffice(OfficeRequest req) {
    Office office = convert(req);
    SeatSetup seatSetup = setupSeats(req.getFloorPlanUrl());
    List<Seat> seats = convertToSeats(office, seatSetup);
    office.setSeats(seats);
    return officeRepository.save(office);
  }

  @Override
  public SeatSetup setupSeats(String floorPlanUrl) {
    return imageService.post()
        .uri("/layout")
        .body(Mono.just(new LayoutRequest(floorPlanUrl, seatTemplate)), LayoutRequest.class)
        .retrieve()
        .bodyToMono(SeatSetup.class)
        .block();
  }

  @Override
  public List<Seat> convertToSeats(Office office, SeatSetup seatSetup) {
    return seatSetup.matches.stream()
        .map(position -> Seat.builder()
                                .office(office)
                                .position(position)
                                .status(SeatStatus.FREE)
                                .build())
        .collect(Collectors.toList());
  }

  @Override
  public Office convert(OfficeRequest req) {
    Office office = Office.builder()
        .id(req.id)
        .seats(new ArrayList<>())
        .build();
    FloorPlan floorPlan = FloorPlan.builder()
        .imageUrl(req.floorPlanUrl)
        .office(office)
        .build();
    office.setFloorPlan(floorPlan);
    return office;
  }

}
