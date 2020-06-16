package academy.greenfox.reboarding.entry;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntryDTO {

  String userId;
  EntryStatus status;

  LocalDate day;
  LocalDateTime enteredAt;
  LocalDateTime leftAt;
  
  int waitListPosition;

}
