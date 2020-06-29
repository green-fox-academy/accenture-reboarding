package academy.greenfox.reboarding.imagerecognition.rest;

import org.apache.tomcat.jni.Library;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opencv.core.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import academy.greenfox.reboarding.imagerecognition.image.Position;
import academy.greenfox.reboarding.imagerecognition.rest.dto.MarkRequest;
import academy.greenfox.reboarding.imagerecognition.rest.dto.ProcessRequest;
import academy.greenfox.reboarding.imagerecognition.rest.dto.TemplateRequest;
import academy.greenfox.reboarding.imagerecognition.s3.S3Service;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Paths;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTests {

  private static ObjectMapper mapper;

  @Autowired
  MockMvc mockMvc;

  @MockBean
  S3Service s3service;

  @BeforeAll
  public static void setup() {
    Library.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    mapper = new ObjectMapper();
  }

  @Test
  public void testPostTemplateIsSuccessful() throws Exception {
    mockMvc
        .perform(post("/template").contentType(MediaType.APPLICATION_JSON).content(
            mapper.writeValueAsString(new TemplateRequest(Paths.get("examples/template4.jpg").toUri().toString()))))
        .andExpect(status().isOk()).andExpect(jsonPath("$.templateId").exists());
  }

  @Test
  public void testProcessLayoutIsSuccessful() throws Exception {
    mockMvc.perform(post("/template").contentType(MediaType.APPLICATION_JSON).content(
        mapper.writeValueAsString(new TemplateRequest(Paths.get("examples/template4.jpg").toUri().toString()))));
    mockMvc
        .perform(post("/layout").contentType(MediaType.APPLICATION_JSON)
            .content(mapper
                .writeValueAsString(new ProcessRequest(Paths.get("examples/layout.jpg").toUri().toString(), "1.jpg"))))
        .andExpect(status().isOk()).andExpect(jsonPath("$.matches", hasSize(192)));
  }

  @Test
  public void testProcessLayoutWrongTemplate() throws Exception {
    mockMvc
        .perform(post("/layout").contentType(MediaType.APPLICATION_JSON)
            .content(mapper
                .writeValueAsString(new ProcessRequest(Paths.get("examples/layout.jpg").toUri().toString(), "-1.jpg"))))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").exists());
  }

  @Test
  public void testMarkLayoutSuccessful() throws Exception {
    when(s3service.uploadFile(anyString())).thenReturn("real s3 url, but seriously...");
    mockMvc.perform(post("/template").contentType(MediaType.APPLICATION_JSON).content(
        mapper.writeValueAsString(new TemplateRequest(Paths.get("examples/template4.jpg").toUri().toString()))));
    mockMvc.perform(post("/layout").contentType(MediaType.APPLICATION_JSON).content(
        mapper.writeValueAsString(new ProcessRequest(Paths.get("examples/layout.jpg").toUri().toString(), "1.jpg"))));
    mockMvc
        .perform(put("/layout").contentType(MediaType.APPLICATION_JSON)
            .content(mapper
                .writeValueAsString(new MarkRequest("1.jpg", List.of(new Position(10, 10))))))
        .andExpect(status().isOk()).andExpect(jsonPath("$.url").exists());
  }

  @Test
  public void testMarkLayoutWrongLayout() throws Exception {
    mockMvc
        .perform(put("/layout").contentType(MediaType.APPLICATION_JSON)
            .content(mapper
                .writeValueAsString(new MarkRequest("-1.jpg", List.of(new Position(10, 10))))))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").exists());
  }
}
