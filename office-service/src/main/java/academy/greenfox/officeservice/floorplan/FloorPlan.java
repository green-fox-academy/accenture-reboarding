package academy.greenfox.officeservice.floorplan;

import academy.greenfox.officeservice.office.Office;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloorPlan {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;
  @NotEmpty
  String layoutUrl;
  @JsonIgnore
  @OneToOne
  Office office;

}
