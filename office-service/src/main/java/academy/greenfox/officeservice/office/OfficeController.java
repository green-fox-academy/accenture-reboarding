package academy.greenfox.officeservice.office;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
  public ResponseEntity<OfficeDTO> registerOffice(@RequestBody OfficeRequest req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(officeService.registerOffice(req));
  }

  @GetMapping("/{officeId}")
  public ResponseEntity<OfficeDTO> modifyOffice(@PathVariable String officeId) {
    return ResponseEntity.status(HttpStatus.OK).body(officeService.findOffice(officeId));
  }

}
