package academy.greenfox.reboarding.entry;

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
@RequestMapping({EntryController.ENTRY_PATH})
public class EntryController {
  public static final String ENTRY_PATH = "/entry";

  private EntryService service;

  Logger logger;

  public EntryController(EntryService service) {
    this.service = service;
    logger = LoggerFactory.getLogger(getClass());
  }

  @GetMapping("/{userId}")
  public ResponseEntity<EntryDTO> status(@PathVariable String userId) throws NoSuchEntryException {
    logger.info("GET /entry/" + userId);
    return ResponseEntity.ok(service.read(userId));
  }

  @PostMapping
  public ResponseEntity<EntryDTO> register(@RequestBody @Valid EntryRequest entry) throws RegisterException {
    logger.info("POST /entry");
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(entry));
  }

  @PutMapping("/{userId}/enter")
  public ResponseEntity<EntryDTO> enter(@PathVariable String userId) throws EnterException, NoSuchEntryException {
    logger.info("PUT /entry/" + userId + "/enter");
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.enter(userId));
  }
  
  @PutMapping("/{userId}/leave")
  public ResponseEntity<EntryDTO> leave(@PathVariable String userId) throws NoSuchEntryException {
    logger.info("PUT /entry/" + userId + "/leave");
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.leave(userId));
  }
}