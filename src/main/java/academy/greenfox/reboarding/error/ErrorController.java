package academy.greenfox.reboarding.error;

import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({ErrorController.ERROR_PATH})
public class ErrorController extends AbstractErrorController {

  static final String ERROR_PATH = "/error";
  static final Map<Integer, String> ERROR_CODE_MESSAGES = new HashMap<Integer, String>(){{
    put(404, "No such url: ");
  }};

  public ErrorController(final ErrorAttributes errorAttributes) {
    super(errorAttributes, Collections.emptyList());
  }

  @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ErrorMessageDTO> error(HttpServletRequest request) {
    ErrorMessageDTO errorMessage = new ErrorMessageDTO(constructErrorMessage(request));
    HttpStatus status = this.getStatus(request);
    return new ResponseEntity<>(errorMessage, status);
  }

  public String constructErrorMessage(HttpServletRequest request) {
    Map<String, Object> errorAttributes = this.getErrorAttributes(request, ErrorAttributeOptions.defaults());
    String errorMessage = (String) errorAttributes.get("message");
    if (errorMessage.isEmpty()) {
      Integer code = (Integer) errorAttributes.get("status");
      String path = (String) errorAttributes.get("path");
      errorMessage = ERROR_CODE_MESSAGES.getOrDefault(code, "Error on path: ") + path;
    }
    return errorMessage;
  }

  @Override
  public String getErrorPath() {
    return ERROR_PATH;
  }

}
