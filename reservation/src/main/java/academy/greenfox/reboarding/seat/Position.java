package academy.greenfox.reboarding.seat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
@Data
@NoArgsConstructor
public class Position {

  double x;
  double y;
  @JsonIgnore
  @Transient
  double[] key;

  public Position(double x, double y) {
    this.x = x;
    this.y = y;
    this.key = new double[]{x, y};
  }

  public Position withKey() {
    if (key == null) key = new double[]{x, y};
    return this;
  }

}
