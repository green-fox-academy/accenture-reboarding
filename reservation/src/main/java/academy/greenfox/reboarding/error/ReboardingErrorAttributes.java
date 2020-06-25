package academy.greenfox.reboarding.error;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

public class ReboardingErrorAttributes extends DefaultErrorAttributes {

  @Override
  public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
    final Map<String, Object> defaultErrorAttributes = super.getErrorAttributes(webRequest, options);
    final ErrorMessageDTO errorResponse = ErrorMessageDTO.fromDefaultAttributeMap(defaultErrorAttributes);
    return errorResponse.toAttributeMap();
  }

}
