package academy.greenfox.reboarding.imagerecognition.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import academy.greenfox.reboarding.imagerecognition.image.ImageService;
import academy.greenfox.reboarding.imagerecognition.rest.dto.TemplateIdResponse;
import academy.greenfox.reboarding.imagerecognition.rest.dto.TemplateRequest;

@RestController
@RequestMapping("/template")
public class TemplateController {
  ImageService service;

  public TemplateController(ImageService service) {
    this.service = service;
  }

  @PostMapping
  public TemplateIdResponse createTemplate(@RequestBody TemplateRequest request) {
    return new TemplateIdResponse(service.storeTemplate(request.getUrl()));
  }

}
