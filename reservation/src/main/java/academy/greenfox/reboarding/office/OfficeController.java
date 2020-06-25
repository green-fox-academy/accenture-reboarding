package academy.greenfox.reboarding.office;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/office")
public class OfficeController {

  private OfficeService officeService;

  public OfficeController(OfficeService officeService) {
    this.officeService = officeService;
  }

  @PostMapping
  public ResponseEntity<Office> registerOffice(@RequestBody Office office) {
    return ResponseEntity.status(HttpStatus.CREATED).body(officeService.registerOffice(office));
  }

  @PutMapping("/{officeId}")
  public ResponseEntity<Office> setUpSeats(@PathVariable String officeId, @RequestBody SeatSetupDTO seatSetup) {
    return ResponseEntity.status(HttpStatus.OK).body(officeService.setupSeats(officeId, seatSetup));
  }

}
