package academy.greenfox.reboarding.seat;

import academy.greenfox.reboarding.office.Office;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Seat {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;
  String layoutUrl;
  @Embedded
//  @AttributeOverrides({
//      @AttributeOverride(name = "x", column = @Column(name = "x")),
//      @AttributeOverride(name = "y", column = @Column(name = "y"))
//  })
  Position position;
  @Enumerated(EnumType.STRING)
  SeatStatus status;
  @JsonIgnore
  @ManyToOne
  Office office;
  String message;

}
