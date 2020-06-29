package academy.greenfox.reboarding.imagerecognition.image;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import academy.greenfox.reboarding.imagerecognition.rest.dto.MarkRequest;

public class ImageServiceImplTest {
  private ImageServiceImpl service;
  private OpenCvWrapper opencv;

  @BeforeEach
  public void setup() throws IOException {
    opencv = Mockito.mock(OpenCvWrapper.class);
    service = new ImageServiceImpl(opencv);
  }

  @Test
  public void storeLayoutSavesFile() {
    service.storeLayout(Paths.get("examples/layout.jpg").toUri().toString());
    assertTrue(Files.exists(Paths.get("layouts/1.jpg")));
  }

  @Test
  public void storeTemplateSavesFile() {
    service.storeTemplate(Paths.get("examples/template4.jpg").toUri().toString());
    assertTrue(Files.exists(Paths.get("templates/1.jpg")));
  }

  @Test
  public void markLayoutCreatesDrawsRectAndWritesFile() {
    when(opencv.read("layout")).thenReturn(any());
    service.markLayout(new MarkRequest("layout", List.of(new Position(10, 10)), null, null));
    verify(opencv).rect(any(), any(), any(), any(), anyInt());
    verify(opencv).write(anyString(), any());
  }

  @Test
  public void storeImageLocally() throws IOException {
    service.storeImageLocally(Paths.get("examples/template4.jpg").toUri().toString(), "result.jpg");
    assertTrue(Files.exists(Paths.get("result.jpg")));
    Files.deleteIfExists(Paths.get("result.jpg"));
  }

  @Test
  public void processLayout() {
    service.processLayout("layout", "template");

    verify(opencv).read(endsWith("layout"));
    verify(opencv).read(endsWith("template"));
    verify(opencv).copy(any());
    verify(opencv).rotate(any());
    verify(opencv, times(2)).createMatches(any(), any(), any(), any());
  }
}
