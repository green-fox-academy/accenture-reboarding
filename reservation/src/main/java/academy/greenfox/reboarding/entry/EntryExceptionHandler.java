package academy.greenfox.reboarding.entry;

import academy.greenfox.reboarding.error.ErrorMessageDTO;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

  @ExceptionHandler({NoSuchEntryException.class})
  public ResponseEntity<ErrorMessageDTO> noSuchEntry(NoSuchEntryException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDTO(exception.getMessage()));
  }

  @ExceptionHandler({HttpMessageNotReadableException.class})
  public ResponseEntity<ErrorMessageDTO> invalidJsonRequest(HttpMessageNotReadableException e) {
    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorMessageDTO(e.getRootCause().getMessage()));
  }

  @ExceptionHandler({MethodArgumentNotValidException.class})
  public ResponseEntity<ErrorMessageDTO> handleConstraintViolations(MethodArgumentNotValidException e) {
    List<ObjectError> errors = e.getBindingResult().getAllErrors();
    Map<String, List<ObjectError>> errorsByType = errors.stream()
        .collect(Collectors.groupingBy(ObjectError::getCode, Collectors.toList()));

    String requiredFieldErrorMessage = requiredFieldsError(errorsByType, "NotNull", "NotEmpty");
    if (!requiredFieldErrorMessage.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDTO(requiredFieldErrorMessage));
    }

    String validationErrorMessage = validationErrorMessage(errorsByType);
    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorMessageDTO(validationErrorMessage));
  }

  private String requiredFieldsError(Map<String, List<ObjectError>> errorsByType, String... types) {
    List<ObjectError> requiredFieldErrors = errorsOfTypes(errorsByType, types);
    String errorMessage = requiredFieldErrorMessage(requiredFieldErrors);
    return errorMessage;
  }

  private List<ObjectError> errorsOfTypes(Map<String, List<ObjectError>> errorsByType, String... types) {
    return Arrays.stream(types)
        .filter(type -> errorsByType.containsKey(type))
        .map(type -> errorsByType.get(type))
        .flatMap(errors -> errors.stream())
        .collect(Collectors.toList());
  }

  private String requiredFieldErrorMessage(List<ObjectError> requiredFieldErrors) {
    if (requiredFieldErrors.isEmpty()) return "";
    if (requiredFieldErrors.size() == 1) {
      return StringUtils.capitalize(fieldName(requiredFieldErrors.get(0)) + " is required.");
    }

    String errorMessage = requiredFieldErrors.stream()
        .map(requiredField -> fieldName(requiredField))
        .collect(Collectors.joining(","));
    int lastSeparatorIndex = errorMessage.lastIndexOf(',');
    errorMessage = StringUtils.capitalize(errorMessage.substring(0, lastSeparatorIndex) + " and "
        + errorMessage.substring(lastSeparatorIndex + 1) + " are required.");
    return errorMessage;
  }

  private String validationErrorMessage(Map<String, List<ObjectError>> errors) {
    return errors.entrySet().stream()
        .flatMap(error -> error.getValue().stream())
        .map(error -> StringUtils.capitalize(error.getDefaultMessage()))
        .collect(Collectors.joining(" | "));
  }

  private String fieldName(ObjectError error) {
    return ((DefaultMessageSourceResolvable) error.getArguments()[0]).getDefaultMessage();
  }
}
