package academy.greenfox.reboarding.entry;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class EntryDTO {

  public EntryDTO() {
  }

  public EntryDTO(String userId, EntryStatus status, LocalDate day, LocalDateTime enteredAt, LocalDateTime leftAt, int waitListPosition) {
    this.userId = userId;
    this.status = status;
    this.day = day;
    this.enteredAt = enteredAt;
    this.leftAt = leftAt;
    this.waitListPosition = waitListPosition;
  }

  public String getUserId() {
    return this.userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public EntryStatus getStatus() {
    return this.status;
  }

  public void setStatus(EntryStatus status) {
    this.status = status;
  }

  public LocalDate getDay() {
    return this.day;
  }

  public void setDay(LocalDate day) {
    this.day = day;
  }

  public LocalDateTime getEnteredAt() {
    return this.enteredAt;
  }

  public void setEnteredAt(LocalDateTime enteredAt) {
    this.enteredAt = enteredAt;
  }

  public LocalDateTime getLeftAt() {
    return this.leftAt;
  }

  public void setLeftAt(LocalDateTime leftAt) {
    this.leftAt = leftAt;
  }

  public int getWaitListPosition() {
    return this.waitListPosition;
  }

  public void setWaitListPosition(int waitListPosition) {
    this.waitListPosition = waitListPosition;
  }

  public EntryDTO userId(String userId) {
    this.userId = userId;
    return this;
  }

  public EntryDTO status(EntryStatus status) {
    this.status = status;
    return this;
  }

  public EntryDTO day(LocalDate day) {
    this.day = day;
    return this;
  }

  public EntryDTO enteredAt(LocalDateTime enteredAt) {
    this.enteredAt = enteredAt;
    return this;
  }

  public EntryDTO leftAt(LocalDateTime leftAt) {
    this.leftAt = leftAt;
    return this;
  }

  public EntryDTO waitListPosition(int waitListPosition) {
    this.waitListPosition = waitListPosition;
    return this;
  }


  @Override
  public String toString() {
    return "{" +
      " userId='" + getUserId() + "'" +
      ", status='" + getStatus() + "'" +
      ", day='" + getDay() + "'" +
      ", enteredAt='" + getEnteredAt() + "'" +
      ", leftAt='" + getLeftAt() + "'" +
      ", waitListPosition='" + getWaitListPosition() + "'" +
      "}";
  }
  
  String userId;
  EntryStatus status;

  LocalDate day;
  LocalDateTime enteredAt;
  LocalDateTime leftAt;
  
  int waitListPosition;


}
