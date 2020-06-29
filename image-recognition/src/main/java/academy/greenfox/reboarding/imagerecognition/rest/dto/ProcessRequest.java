package academy.greenfox.reboarding.imagerecognition.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessRequest {
  String layoutUrl;
  String templateId;
}
