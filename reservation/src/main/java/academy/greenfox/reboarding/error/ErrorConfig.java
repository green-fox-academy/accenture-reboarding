package academy.greenfox.reboarding.error;

import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ErrorConfig {

  @Bean
  public ErrorAttributes errorAttributes() {
    return new ReboardingErrorAttributes();
  }

}
