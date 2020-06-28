package academy.greenfox.reboarding.imagerecognition.rest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProcessRequest {
  String layoutUrl;
  String templateId;
}
