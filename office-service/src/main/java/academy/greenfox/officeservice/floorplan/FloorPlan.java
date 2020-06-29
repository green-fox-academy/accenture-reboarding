package academy.greenfox.officeservice.floorplan;

import academy.greenfox.officeservice.office.Office;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
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
  @NotEmpty
  String imageUrl;
  @JsonIgnore
  @OneToOne
  Office office;

}
