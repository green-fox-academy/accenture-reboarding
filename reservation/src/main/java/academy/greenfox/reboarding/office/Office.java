package academy.greenfox.reboarding.office;

import academy.greenfox.reboarding.seat.Seat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Office {

  @Id
  String id;
  String layoutId;
  @OneToMany(mappedBy = "office", cascade = CascadeType.ALL)
  List<Seat> seats;

  public Office(String id) {
    this.id = id;
  }
}
