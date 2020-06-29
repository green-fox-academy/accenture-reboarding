package academy.greenfox.officeservice.office;

import academy.greenfox.officeservice.floorplan.FloorPlan;
import academy.greenfox.officeservice.seat.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Office {

  @Id
  String id;
  @OneToMany(mappedBy = "office", cascade = CascadeType.ALL)
  List<Seat> seats;
  @OneToOne(cascade = CascadeType.ALL, mappedBy = "office")
  FloorPlan floorPlan;

}
