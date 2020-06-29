package academy.greenfox.reboarding.externalservices;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ExternalServiceConfiguration {

  @Bean
  @LoadBalanced
  WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }

  @Bean(name = "OfficeService")
  public WebClient getOfficeService(@LoadBalanced WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl("http://office-service")
        .build();
  }

  @Bean(name = "ImageService")
  public WebClient getImageService(@LoadBalanced WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl("http://image-recognition")
        .build();
  }

}