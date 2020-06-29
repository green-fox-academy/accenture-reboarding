package academy.greenfox.officeservice.office;

public class NoSuchOfficeException extends RuntimeException {
  public static final String MESSAGE = "No office found with the given id.";

  public NoSuchOfficeException() {
    super(MESSAGE);
  }
}
