package academy.greenfox.reboarding.error;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorMessageDTO {
  @JsonIgnore
  Integer status;
  @JsonIgnore
  String path;
  String message;

  public ErrorMessageDTO(String message) {
    this.message = message;
  }

  public static ErrorMessageDTO fromDefaultAttributeMap(final Map<String, Object> defaultErrorAttributes) {
    return new ErrorMessageDTO(
        (Integer) defaultErrorAttributes.get("status"),
        (String) defaultErrorAttributes.get("path"),
        (String) defaultErrorAttributes.get("message")
    );
  }

  public Map<String, Object> toAttributeMap() {
    return new HashMap<String, Object>(){{
      put("status", status);
      put("path", path);
      put("message", message);
    }};
  }

}
