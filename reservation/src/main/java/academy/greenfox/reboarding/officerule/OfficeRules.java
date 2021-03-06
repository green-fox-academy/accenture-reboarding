package academy.greenfox.reboarding.officerule;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public final class OfficeRules {

  public List<OfficeRule> rules;

  public OfficeRules(MinimalDistanceRule minimalDistanceRule) {
    rules = Arrays.asList(
        minimalDistanceRule
    );
  }

}
