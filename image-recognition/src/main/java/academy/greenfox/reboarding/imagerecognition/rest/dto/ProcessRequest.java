package academy.greenfox.reboarding.imagerecognition.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProcessRequest {
  String layoutUrl;
  String templateId;
}
