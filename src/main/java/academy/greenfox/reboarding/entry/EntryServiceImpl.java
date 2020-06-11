package academy.greenfox.reboarding.entry;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class EntryServiceImpl implements EntryService {

  private EntryRepository repo;
  private static int ALLOWED_IN = 25;

  public EntryServiceImpl(EntryRepository repo) {
    this.repo = repo;
  }

  @Override
  public EntryDTO create(Entry entry) throws RegisterException {
    entry.setCreatedAt(LocalDateTime.now());
    entry.setStatus(EntryStatus.WAITLISTED);
    if(repo.countByDayAndStatus(entry.getDay(), EntryStatus.ACCEPTED, entry.getCreatedAt()) < ALLOWED_IN) {
      entry.setStatus(EntryStatus.ACCEPTED);
    }
    if (repo.findByUserIdAndDay(entry.getUserId(), entry.getDay()) != null) {
      throw new RegisterException("This user is registered for the day.");
    }
    return convert(repo.save(entry));
  }

  @Override
  public EntryDTO read(String userId) {
    return convert(repo.findByUserIdAndDay(userId, LocalDate.now()));
  }

  @Override
  public EntryDTO convert(Entry entry) {
    EntryDTO dto = new EntryDTO();
    dto.setDay(entry.getDay());
    dto.setUserId(entry.getUserId());
    dto.setEnteredAt(entry.getEnteredAt());
    dto.setLeftAt(entry.getLeftAt());
    dto.setStatus(entry.getStatus());
    dto.setWaitListPosition(getWaitListSize(entry));
    System.out.println("converted" + dto);
    return null;
  }

  @Override
  public int getWaitListSize(Entry entry) {
    return repo.countByDayAndStatus(entry.getDay(), EntryStatus.WAITLISTED, entry.getCreatedAt());
  }

  private boolean enoughSpaceForWaitlisted(Entry entry) {
    return getWaitListSize(entry) < (ALLOWED_IN - repo.countByDayAndEnterAtAndLeftAt(LocalDate.now(), LocalDateTime.now(), null));
  }

  @Override
  public EntryDTO enter(String userId) throws EnterException {
    Entry entry = repo.findByUserIdAndDay(userId, LocalDate.now());
    if(entry.getStatus().equals(EntryStatus.USED)) {
      throw new EnterException("Try another day, you workaholic!");
    } else if(entry.getStatus().equals(EntryStatus.WAITLISTED) && !enoughSpaceForWaitlisted(entry)) {
      throw new EnterException("Not your turn, bitch.");
    }
    entry.setEnteredAt(LocalDateTime.now());
    return convert(repo.save(entry));
  }
  
  @Override
  public EntryDTO leave(String userId) {
    Entry entry = repo.findByUserIdAndDay(userId, LocalDate.now());
    entry.setLeftAt(LocalDateTime.now());
    Optional<Entry> other = repo.findFirstByUserIdAndDay(entry.getDay(), EntryStatus.WAITLISTED);
    if (other.isPresent()) {
      other.get().setStatus(EntryStatus.ACCEPTED);
      repo.save(other.get());
    }
    entry.setStatus(EntryStatus.USED);
    return convert(repo.save(entry));
  }
}
