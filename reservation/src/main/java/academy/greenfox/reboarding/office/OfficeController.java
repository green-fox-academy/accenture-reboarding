package academy.greenfox.reboarding.office;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/office")
public class OfficeController {

  private OfficeReservationService officeReservationService;

  public OfficeController(OfficeReservationService officeReservationService) {
    this.officeReservationService = officeReservationService;
  }

  @PutMapping("/{officeId}")
  public ResponseEntity<Office> updateOffice(@PathVariable String officeId) {
    return ResponseEntity.status(HttpStatus.CREATED).body(officeReservationService.updateOffice(officeId));
  }

}
