package academy.greenfox.reboarding.app;

import academy.greenfox.reboarding.entry.EnterException;
import academy.greenfox.reboarding.entry.RegisterException;
import academy.greenfox.reboarding.error.ErrorMessageDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EntryExceptionHandler {

  @ExceptionHandler({EnterException.class})
  public ResponseEntity<ErrorMessageDTO> notAllowedToEnter(EnterException exception) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMessageDTO(exception.getMessage()));
  }

  @ExceptionHandler({RegisterException.class})
  public ResponseEntity<ErrorMessageDTO> alreadyRegistered(RegisterException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDTO(exception.getMessage()));
  }

}
