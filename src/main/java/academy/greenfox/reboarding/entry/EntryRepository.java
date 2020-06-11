package academy.greenfox.reboarding.entry;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface EntryRepository extends CrudRepository<Entry, Long> {
  Entry findByUserIdAndDay(String userId, LocalDate day);

  @Query("select count(e) from Entry e where e.day = ?1 and e.status = ?2 and e.createdAt < ?3")
  int countByDayAndStatus(LocalDate day, EntryStatus status, LocalDateTime before);

  @Query("select e from Entry e where e.day = ?1 and e.status = ?2 order by e.createdAt ASC")
  Optional<Entry> findFirstByUserIdAndDay(LocalDate day, EntryStatus status);
  
  @Query("select count(e) from Entry e where e.day = ?1 and e.enteredAt < ?2 and e.leftAt = ?3")
  int countByDayAndEnterAtAndLeftAt(LocalDate day, LocalDateTime enteredAt, LocalDateTime leftAt);
}
