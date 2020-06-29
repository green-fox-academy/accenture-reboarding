package academy.greenfox.reboarding.imagerecognition;

import org.apache.tomcat.jni.Library;
import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableEurekaClient
@EnableSwagger2
public class ImageProcessingApp {
  

  public static void main(String[] args) {
    Library.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    SpringApplication.run(ImageProcessingApp.class, args);
  }

}
