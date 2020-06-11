package academy.greenfox.reboarding.entry;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EntryDTO {

  String userId;
  EntryStatus status;

  LocalDate day;
  LocalDateTime enteredAt;
  LocalDateTime leftAt;
  
  int waitListPosition;


}
