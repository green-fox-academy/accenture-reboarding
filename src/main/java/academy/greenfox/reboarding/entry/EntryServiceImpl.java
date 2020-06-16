package academy.greenfox.reboarding.entry;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EntryServiceImpl implements EntryService {

  private EntryRepository repo;
  private int ALLOWED_IN;

  private Logger logger;

  public EntryServiceImpl(
    EntryRepository repo,
    @Value("${entry.allowedIn.default}") Integer allowedInDefault,
    @Value("${entry.allowedIn.limit}") Integer allowedIn
    ) {
    this.repo = repo;
    logger = LoggerFactory.getLogger(getClass());
    if(allowedIn == null) {
      logger.error("REBOARDING_ALLOWED_IN environment variable is not a valid whole number");
      logger.info("ALLOWED_IN set to default");
      allowedIn = allowedInDefault;
    }
    ALLOWED_IN = allowedIn;
    logger.info("ALLOWED_IN set to " + ALLOWED_IN);
  }

  @Override
  public EntryDTO create(Entry entry) throws RegisterException {
    entry.setCreatedAt(LocalDateTime.now());
    entry.setStatus(EntryStatus.WAITLISTED);
    if (repo.findByUserIdAndDay(entry.getUserId(), entry.getDay()).isPresent()) {
      throw new RegisterException(RegisterException.ALREADY_REGISTERED);
    }
    if(repo.countByDayAndStatus(entry.getDay(), EntryStatus.ACCEPTED, entry.getCreatedAt()) < ALLOWED_IN) {
      entry.setStatus(EntryStatus.ACCEPTED);
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
      .enteredAt(entry.getEnteredAt())
      .leftAt(entry.getLeftAt())
      .status(entry.getStatus())
      .waitListPosition(getWaitListSize(entry))
      .build();
    logger.debug("converted" + dto);
    return dto;
  }

  @Override
  public int getWaitListSize(Entry entry) {
    return repo.countByDayAndStatus(entry.getDay(), EntryStatus.WAITLISTED, entry.getCreatedAt());
  }

  public boolean enoughSpaceForWaitlisted(Entry entry) {
    return getWaitListSize(entry) < (ALLOWED_IN - repo.countByDayAndEnterAtAndLeftAt(LocalDate.now(), LocalDateTime.now(), null));
  }

  @Override
  public EntryDTO enter(String userId) throws EnterException, NoSuchEntryException {
    Entry entry = repo.findByUserIdAndDay(userId, LocalDate.now()).orElseThrow(() -> new NoSuchEntryException(userId));
    if(entry.getStatus().equals(EntryStatus.USED)) {
      throw new EnterException(EnterException.ALREADY_USED);
    } else if(entry.getStatus().equals(EntryStatus.WAITLISTED) && !enoughSpaceForWaitlisted(entry)) {
      throw new EnterException(EnterException.NOT_ENOUGH_SPACE);
    }
    entry.setEnteredAt(LocalDateTime.now());
    return convert(repo.save(entry));
  }

  @Override
  public EntryDTO leave(String userId) throws NoSuchEntryException {
    Entry entry = repo.findByUserIdAndDay(userId, LocalDate.now()).orElseThrow(() -> new NoSuchEntryException(userId));
    entry.setLeftAt(LocalDateTime.now());
    entry.setStatus(EntryStatus.USED);
    Optional<Entry> other = repo.findFirstByUserIdAndDay(entry.getDay(), EntryStatus.WAITLISTED);
    if (other.isPresent()) {
      other.get().setStatus(EntryStatus.ACCEPTED);
      repo.save(other.get());
    }
    return convert(repo.save(entry));
  }
}