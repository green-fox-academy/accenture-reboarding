package academy.greenfox.reboarding.imagerecognition;

import org.apache.tomcat.jni.Library;
import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImageProcessingApp {
  

  public static void main(String[] args) {
    Library.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    SpringApplication.run(ImageProcessingApp.class, args);
  }

}
