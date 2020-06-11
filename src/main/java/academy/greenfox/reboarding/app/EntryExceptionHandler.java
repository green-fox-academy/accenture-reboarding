package academy.greenfox.reboarding.app;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

import academy.greenfox.reboarding.entry.EnterException;
import academy.greenfox.reboarding.entry.RegisterException;
import academy.greenfox.reboarding.error.ErrorMessageDTO;

@Controller
public class EntryExceptionHandler {
  @ExceptionHandler(value = EnterException.class)
  public ResponseEntity<ErrorMessageDTO> notAllowedToEnter(EnterException exception) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMessageDTO(exception.getMessage()));
  }

  @ExceptionHandler(value = RegisterException.class)
  public ResponseEntity<ErrorMessageDTO> alreadyRegistered(EnterException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDTO(exception.getMessage()));
  }
}
