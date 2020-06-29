package academy.greenfox.officeservice.office;

import academy.greenfox.officeservice.floorplan.FloorPlan;
import academy.greenfox.officeservice.seat.Seat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
  @OneToOne(cascade = CascadeType.ALL, mappedBy = "office")
  FloorPlan floorPlan;
  @OneToMany(mappedBy = "office", cascade = CascadeType.ALL)
  List<Seat> seats;

}
