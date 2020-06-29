package academy.greenfox.reboarding.imagerecognition.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import academy.greenfox.reboarding.imagerecognition.image.ImageService;
import academy.greenfox.reboarding.imagerecognition.rest.dto.MarkRequest;
import academy.greenfox.reboarding.imagerecognition.rest.dto.MarkedResponse;
import academy.greenfox.reboarding.imagerecognition.rest.dto.ProcessRequest;
import academy.greenfox.reboarding.imagerecognition.rest.dto.SeatSetup;
import academy.greenfox.reboarding.imagerecognition.s3.S3Service;

@RestController
@RequestMapping("/layout")
public class LayoutController {

  ImageService service;
  S3Service s3service;
  
  public LayoutController(ImageService service, S3Service s3Service) {
    this.service = service;
    this.s3service = s3Service;
  }
  
  @PostMapping
  public SeatSetup processLayout(@RequestBody ProcessRequest request) {
    String layoutPath = service.storeLayout(request.getLayoutUrl());
    return new SeatSetup(service.processLayout(layoutPath, request.getTemplateId()), layoutPath);
  }

  @PutMapping
  public MarkedResponse markLayout(@RequestBody MarkRequest request) {
    String markedImagePath = service.markLayout(request);
    return new MarkedResponse(s3service.uploadFile(markedImagePath));
  }
}
