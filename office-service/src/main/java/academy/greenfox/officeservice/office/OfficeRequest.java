package academy.greenfox.officeservice.office;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfficeRequest {

  @NotEmpty
  String id;
  @NotEmpty
  String layoutUrl;

}
