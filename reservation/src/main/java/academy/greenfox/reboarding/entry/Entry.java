package academy.greenfox.reboarding.entry;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import academy.greenfox.reboarding.seat.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entry {
  @Id
  @GeneratedValue
  long id;

  @NotEmpty
  String userId;
  @NotEmpty
  String officeId;
  @ManyToOne(cascade = CascadeType.PERSIST)
  Seat seat;
  
  LocalDateTime createdAt;

  @NotNull
  LocalDate day;
  LocalDateTime enteredAt;
  LocalDateTime leftAt;

  @Enumerated(EnumType.STRING)
  EntryStatus status;
}