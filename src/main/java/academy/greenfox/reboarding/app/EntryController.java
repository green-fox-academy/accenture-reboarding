package academy.greenfox.reboarding.app;

import academy.greenfox.reboarding.entry.EnterException;
import academy.greenfox.reboarding.entry.Entry;
import academy.greenfox.reboarding.entry.EntryDTO;
import academy.greenfox.reboarding.entry.EntryService;
import academy.greenfox.reboarding.entry.RegisterException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;

@RestController
@RequestMapping("/entry")
public class EntryController {
  EntryService service;

  Logger logger;

  public EntryController(EntryService service) {
    this.service = service;
    logger = LoggerFactory.getLogger(getClass());
  }

  @GetMapping("/{userId}")
  public ResponseEntity<EntryDTO> status(@PathVariable String userId) {
    logger.info("GET /entry/" + userId);
    return ResponseEntity.ok(service.read(userId));
  }

  @PostMapping
  public ResponseEntity<EntryDTO> register(@RequestBody @Valid Entry entry) throws RegisterException {
    logger.info("POST /entry");
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(entry));
  }
  
  @PutMapping("/{userId}/enterDate")
  public ResponseEntity<EntryDTO> enter(@PathVariable String userId) throws EnterException {
    logger.info("PUT /entry/" + userId + "/enterDate");
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.enter(userId));
  }
  
  @PutMapping("/{userId}/leaveDate")
  public ResponseEntity<EntryDTO> leave(@PathVariable String userId) {
    logger.info("PUT /entry/" + userId + "/leaveDate");
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.leave(userId));
  }

}
