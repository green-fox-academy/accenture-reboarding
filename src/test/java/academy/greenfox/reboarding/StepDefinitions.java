package academy.greenfox.reboarding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import io.cucumber.spring.CucumberContextConfiguration;
import io.cucumber.spring.CucumberContextConfiguration;


public class StepDefinitions {


  @When("the client {int} requests an entry on {string}")
  public void whenStatement(int clientId, String date){
    assertEquals("2020-06-15", date);
  }
}
