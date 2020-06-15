package academy.greenfox.reboarding.entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class EntryServiceImplTest {
  private EntryServiceImpl service;
  private EntryRepository repo;

  @BeforeEach
  public void setUp() {
    repo = Mockito.mock(EntryRepository.class);
    service = new EntryServiceImpl(repo);
  }

  @Test
  public void testRead() {
    when(repo.findByUserIdAndDay(anyString(), any())).thenReturn(new Entry());

    String userId = "userId";
    service.read(userId);

    verify(repo).findByUserIdAndDay(eq(userId), any());
  }

  @Test
  public void testConvert() {
    Entry entry = EntryFactory.create();

    when(
      repo.countByDayAndStatus(
        eq(entry.getDay()),
        eq(EntryStatus.WAITLISTED),
        eq(entry.getCreatedAt())))
      .thenReturn(5);
    
    EntryDTO expected = EntryDTO.builder()
      .day(entry.getDay())
      .userId(entry.getUserId())
      .enteredAt(entry.getEnteredAt())
      .leftAt(entry.getLeftAt())
      .status(entry.getStatus())
      .waitListPosition(5)
      .build();
    
    EntryDTO actual = service.convert(entry);
    
    assertEquals(expected, actual);
  }

  @Test
  public void testGetWaitListSize() {
    Entry entry = EntryFactory.create();

    service.getWaitListSize(entry);

    verify(repo).countByDayAndStatus(
      eq(entry.getDay()),
      eq(EntryStatus.WAITLISTED),
      eq(entry.getCreatedAt()));
  }

  @Test
  public void testEnoughSpaceForWaitlisted() {
    Entry entry = EntryFactory.create();

    when(
      repo.countByDayAndStatus(
        eq(entry.getDay()),
        eq(EntryStatus.WAITLISTED),
        eq(entry.getCreatedAt())))
      .thenReturn(5);
    
    when(repo.countByDayAndEnterAtAndLeftAt(any(), any(), eq(null)))
      .thenReturn(10);

    assertTrue(service.enoughSpaceForWaitlisted(entry));
  }

  @Test
  public void testNotEnoughSpaceForWaitlisted() {
    Entry entry = EntryFactory.create();

    when(
      repo.countByDayAndStatus(
        eq(entry.getDay()),
        eq(EntryStatus.WAITLISTED),
        eq(entry.getCreatedAt())))
      .thenReturn(5);
    
    when(repo.countByDayAndEnterAtAndLeftAt(any(), any(), eq(null)))
      .thenReturn(24);

    assertFalse(service.enoughSpaceForWaitlisted(entry));
  }

  @Test
  public void testCreateWaitlisted() throws RegisterException {
    Entry entry = EntryFactory.createRequest();
    
    when(
      repo.countByDayAndStatus(
        eq(entry.getDay()),
        eq(EntryStatus.ACCEPTED),
        any()))
      .thenReturn(26);
    
    when(repo.save(eq(entry))).thenReturn(entry);
    
    assertEquals(EntryStatus.WAITLISTED, service.create(entry).getStatus());
  }

  @Test
  public void testCreateAccepted() throws RegisterException {
    Entry entry = EntryFactory.createRequest();

    when(
      repo.countByDayAndStatus(
        eq(entry.getDay()),
        eq(EntryStatus.ACCEPTED),
        any()))
      .thenReturn(1);
    
    when(repo.save(eq(entry))).thenReturn(entry);
    
    assertEquals(EntryStatus.ACCEPTED, service.create(entry).getStatus());
  }

  @Test
  public void testCreateAlreadyRegistered() throws RegisterException {
    Entry entry = EntryFactory.createRequest();

    when(
      repo.findByUserIdAndDay(
        eq(entry.getUserId()),
        eq(entry.getDay()))
      ).thenReturn(new Entry());
    
    Exception actual = assertThrows(RegisterException.class, () -> {
      service.create(entry);
    });

    assertEquals("This user is registered for the day.", actual.getMessage());
  }

  @Test
  public void testEnterAlreadyEntered() {
    Entry entry = EntryFactory.create(EntryStatus.USED);

    when(
      repo.findByUserIdAndDay(
        eq(entry.getUserId()),
        any())
    ).thenReturn(entry);

    Exception actual = assertThrows(EnterException.class, () -> {
      service.enter(entry.getUserId());
    });

    assertEquals("Try another day, you workaholic!", actual.getMessage());
  }

  @Test
  public void testEnterNotEnoughSpace() {
    Entry entry = EntryFactory.create(EntryStatus.WAITLISTED);

    when(
      repo.findByUserIdAndDay(
        eq(entry.getUserId()),
        any())
    ).thenReturn(entry);

    when(
      repo.countByDayAndStatus(
        eq(entry.getDay()),
        eq(EntryStatus.WAITLISTED),
        eq(entry.getCreatedAt())))
      .thenReturn(5);
    
    when(repo.countByDayAndEnterAtAndLeftAt(any(), any(), eq(null)))
      .thenReturn(24);

    Exception actual = assertThrows(EnterException.class, () -> {
      service.enter(entry.getUserId());
    });

    assertEquals("Not your turn, bitch.", actual.getMessage());
  }

  @Test
  public void testEnterSuccess() throws EnterException {
    Entry entry = EntryFactory.create(EntryStatus.WAITLISTED);

    when(
      repo.findByUserIdAndDay(
        eq(entry.getUserId()),
        any())
    ).thenReturn(entry);

    when(
      repo.countByDayAndStatus(
        eq(entry.getDay()),
        eq(EntryStatus.WAITLISTED),
        eq(entry.getCreatedAt())))
      .thenReturn(5);
    
    when(repo.countByDayAndEnterAtAndLeftAt(any(), any(), eq(null)))
      .thenReturn(10);

    when(repo.save(eq(entry))).thenReturn(entry);

    assertNotNull(service.enter(entry.getUserId()).getEnteredAt());
  }

  @Test
  public void testLeaveNoOther() throws EnterException {
    Entry entry = EntryFactory.create();

    when(repo.findByUserIdAndDay(
      eq(entry.getUserId()),
      any())
    ).thenReturn(entry);

    when(repo.save(eq(entry))).thenReturn(entry);

    assertNotNull(service.leave(entry.getUserId()).getLeftAt());
    assertEquals(EntryStatus.USED, entry.getStatus());
  }

  @Test
  public void testLeaveWithOther() throws EnterException {
    Entry entry = EntryFactory.create();
    Entry other = EntryFactory.create(EntryStatus.WAITLISTED);

    when(repo.findByUserIdAndDay(
      eq(entry.getUserId()),
      any())
    ).thenReturn(entry);

    when(repo.findFirstByUserIdAndDay(
      eq(entry.getDay()),
      eq(EntryStatus.WAITLISTED))
    ).thenReturn(Optional.of(other));

    when(repo.save(eq(entry))).thenReturn(entry);

    assertNotNull(service.leave(entry.getUserId()).getLeftAt());
    assertEquals(EntryStatus.USED, entry.getStatus());
    assertEquals(EntryStatus.ACCEPTED, other.getStatus());
  }

}
