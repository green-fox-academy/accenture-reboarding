package academy.greenfox.officeservice.externalservices;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LayoutRequest {

  String layoutUrl;
  String templateId;

}
