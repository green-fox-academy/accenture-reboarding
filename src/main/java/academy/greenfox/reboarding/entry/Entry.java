package academy.greenfox.reboarding.entry;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
public class Entry {
  @Id
  @GeneratedValue
  long id;

  @NotNull
  String userId;

  @NotNull
  LocalDateTime createdAt;

  @NotNull
  LocalDate day;
  LocalDateTime enteredAt;
  LocalDateTime leftAt;

  @Enumerated(EnumType.STRING)
  @NotNull
  EntryStatus status;
}