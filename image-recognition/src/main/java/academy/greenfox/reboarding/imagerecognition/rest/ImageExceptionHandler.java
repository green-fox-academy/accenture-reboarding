package academy.greenfox.reboarding.imagerecognition.rest;

import java.io.IOException;

import org.opencv.core.CvException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import academy.greenfox.reboarding.imagerecognition.rest.dto.ErrorMessageDTO;

import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class ImageExceptionHandler {
  
  @ExceptionHandler({CvException.class})
  public ResponseEntity<ErrorMessageDTO> handleOpencvError(CvException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDTO(exception.getMessage()));
  }

  @ExceptionHandler({IOException.class})
  public ResponseEntity<ErrorMessageDTO> handleIoError(IOException exception) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessageDTO(exception.getMessage()));
  }
}
