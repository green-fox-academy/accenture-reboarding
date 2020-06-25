package academy.greenfox.reboarding.entry;

public class RegisterException extends Exception {
  public static String ALREADY_REGISTERED = "This user is registered for the day.";
  public RegisterException(String message) {
    super(message);
  }
}
