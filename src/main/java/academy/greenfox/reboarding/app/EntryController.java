package academy.greenfox.reboarding.app;

import org.springframework.web.bind.annotation.RestController;

import academy.greenfox.reboarding.entry.EnterException;
import academy.greenfox.reboarding.entry.Entry;
import academy.greenfox.reboarding.entry.EntryDTO;
import academy.greenfox.reboarding.entry.EntryService;
import academy.greenfox.reboarding.entry.RegisterException;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/entry")
public class EntryController {
  EntryService service;

  public EntryController(EntryService service) {
    this.service = service;    
  }

  @GetMapping("/{userId}")
  public ResponseEntity<EntryDTO> status(@PathVariable String userId) {
    System.out.println("status");
    return ResponseEntity.ok(service.read(userId));
  }

  @PostMapping
  public ResponseEntity<EntryDTO> register(@RequestBody Entry entry) throws RegisterException {
    System.out.println("register");
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(entry));
  }
  
  @PutMapping("/{userId}/enterDate")
  public ResponseEntity<EntryDTO> enter(@PathVariable String userId) throws EnterException {  
    System.out.println("enterDate");
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.enter(userId));
  }
  
  @PutMapping("/{userId}/leaveDate")
  public ResponseEntity<EntryDTO> leave(@PathVariable String userId) {  
    System.out.println("leaveDate");
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.leave(userId));
  }
}
