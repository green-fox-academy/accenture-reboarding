package academy.greenfox.reboarding.entry;

public class EnterException extends Exception {
  public static String ALREADY_USED = "Try another day, you workaholic!";
  public static String NOT_ENOUGH_SPACE = "Not your turn, rush B instead.";
  public EnterException(String message) {
    super(message);
  }
}
