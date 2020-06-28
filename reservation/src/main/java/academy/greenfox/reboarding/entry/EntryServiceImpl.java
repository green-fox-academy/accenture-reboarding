package academy.greenfox.reboarding.entry;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import academy.greenfox.reboarding.office.NoSuchOfficeException;
import academy.greenfox.reboarding.office.Office;
import academy.greenfox.reboarding.office.OfficeService;
import academy.greenfox.reboarding.seat.Seat;
import academy.greenfox.reboarding.seat.SeatStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EntryServiceImpl implements EntryService {

  private EntryRepository repo;
  private OfficeService officeService;

  private Logger logger;

  public EntryServiceImpl(EntryRepository repo, OfficeService officeService) {
    this.repo = repo;
    this.officeService = officeService;
    logger = LoggerFactory.getLogger(getClass());
  }

  @Override
  public EntryDTO create(EntryRequest entryRequest) throws RegisterException, NoSuchOfficeException {
    Entry entry = convert(entryRequest);
    entry.setCreatedAt(LocalDateTime.now());
    entry.setStatus(EntryStatus.WAITLISTED);
    if (repo.findByUserIdAndDay(entry.getUserId(), entry.getDay()).isPresent()) {
      throw new RegisterException(RegisterException.ALREADY_REGISTERED);
    }
    Seat reservedSeat = officeService.reserveASeat(entry.getOfficeId(), entry.getUserId());
    if (reservedSeat != null) {
      entry.setStatus(EntryStatus.ACCEPTED);
      entry.setSeat(reservedSeat);
    }
    return convert(repo.save(entry));
  }

  @Override
  public EntryDTO read(String userId) throws NoSuchEntryException {
    return repo.findByUserIdAndDay(userId, LocalDate.now())
        .map(this::convert)
        .orElseThrow(() -> new NoSuchEntryException(userId));
  }

  @Override
  public EntryDTO convert(Entry entry) {
    EntryDTO dto = EntryDTO.builder()
      .day(entry.getDay())
      .userId(entry.getUserId())
      .officeId(entry.getOfficeId())
      .seatId(entry.getSeat() != null ? entry.getSeat().getId() : null)
      .enteredAt(entry.getEnteredAt())
      .leftAt(entry.getLeftAt())
      .status(entry.getStatus())
      .waitListPosition(getWaitListSize(entry))
      .build();
    logger.debug("converted" + dto);
    return dto;
  }

  @Override
  public Entry convert(EntryRequest entryRequest) {
    return Entry.builder()
        .userId(entryRequest.getUserId())
        .officeId(entryRequest.getOfficeId())
        .day(entryRequest.getDay())
        .build();
  }

  @Override
  public int getWaitListSize(Entry entry) {
    return repo.countByDayAndStatus(entry.getDay(), EntryStatus.WAITLISTED, entry.getCreatedAt());
  }

  @Override
  public EntryDTO enter(String userId) throws EnterException, NoSuchEntryException {
    Entry entry = repo.findByUserIdAndDay(userId, LocalDate.now()).orElseThrow(() -> new NoSuchEntryException(userId));
    if(entry.getStatus().equals(EntryStatus.USED)) {
      throw new EnterException(EnterException.ALREADY_USED);
    } else if(entry.getStatus().equals(EntryStatus.WAITLISTED)) {
      throw new EnterException(EnterException.NOT_ENOUGH_SPACE);
    }
    entry.setEnteredAt(LocalDateTime.now());
    entry.getSeat().setStatus(SeatStatus.IN_USE);
    return convert(repo.save(entry));
  }

  @Override
  public EntryDTO leave(String userId) throws NoSuchEntryException {
    Entry entry = repo.findByUserIdAndDay(userId, LocalDate.now()).orElseThrow(() -> new NoSuchEntryException(userId));
    entry.setLeftAt(LocalDateTime.now());
    entry.setStatus(EntryStatus.USED);
    Seat seat = entry.getSeat();
    seat.setStatus(SeatStatus.FREE);
    Optional<Entry> otherOp = repo.findFirstByUserIdAndDay(entry.getDay(), EntryStatus.WAITLISTED);
    if (otherOp.isPresent()) {
      Entry other = otherOp.get();
      other.setStatus(EntryStatus.ACCEPTED);
      other.setSeat(seat);
      seat.setStatus(SeatStatus.RESERVED);
      seat.setMessage(other.userId);
      repo.save(other);
    }
    return convert(repo.save(entry));
  }
}