package academy.greenfox.reboarding.entry;

public class NoSuchEntryException extends RuntimeException {
  public static String NO_SUCH_ENTRY = "No entry registered today for: ";
  public NoSuchEntryException(String userId) {
    super(String.format("%s%s", NO_SUCH_ENTRY, userId));
  }
}
