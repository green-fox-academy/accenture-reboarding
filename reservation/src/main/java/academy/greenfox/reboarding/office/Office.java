package academy.greenfox.reboarding.office;

import academy.greenfox.reboarding.seat.Seat;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Data
public class Office {

  @Id
  String id;
  @OneToMany(mappedBy = "office", cascade = CascadeType.ALL)
  List<Seat> seats;

}
