package academy.greenfox.reboarding.entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import academy.greenfox.reboarding.office.OfficeReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;


public class EntryServiceImplTest {
  private EntryServiceImpl service;
  private OfficeReservationService officeReservationService;
  private WebClient imageService;
  private EntryRepository repo;
  private String userId;
  private String officeId;
  private List<String> vipList;

  @BeforeEach
  public void setUp() {
    vipList = Arrays.asList("aze", "kond", "tojas");
    repo = Mockito.mock(EntryRepository.class);
    imageService = Mockito.mock(WebClient.class);
    officeReservationService = Mockito.mock(OfficeReservationService.class);
    service = new EntryServiceImpl(repo, imageService, officeReservationService);
    ReflectionTestUtils.setField(service, "vipList", vipList);
    userId = "chuck";
    officeId = "A66";
  }

  @Test
  public void testRead() {
    when(repo.findByUserIdAndDay(anyString(), any())).thenReturn(Optional.of(EntryFactory.create()));

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
      .officeId(entry.getOfficeId())
      .seatId(entry.getSeat().getId())
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
  public void testCreateWaitlisted() throws RegisterException {
    EntryRequest req = EntryFactory.createRequest();
    Entry entry = EntryFactory.create(EntryStatus.WAITLISTED);
    
    when(
      repo.countByDayAndStatus(
        eq(req.getDay()),
        eq(EntryStatus.ACCEPTED),
        any()))
      .thenReturn(26);

    when(officeReservationService.reserveASeat(eq(officeId), eq(userId))).thenReturn(null);
    
    when(repo.save(any(Entry.class))).thenReturn(entry);
    
    assertEquals(EntryStatus.WAITLISTED, service.create(req).getStatus());
  }

  @Test
  public void testCreateAccepted() throws RegisterException {
    EntryRequest req = EntryFactory.createRequest();
    Entry entry = EntryFactory.create(EntryStatus.ACCEPTED);

    when(
      repo.countByDayAndStatus(
        eq(req.getDay()),
        eq(EntryStatus.ACCEPTED),
        any()))
      .thenReturn(1);
    
    when(repo.save(any(Entry.class))).thenReturn(entry);
    
    assertEquals(EntryStatus.ACCEPTED, service.create(req).getStatus());
  }

  @Test
  public void testCreateAlreadyRegistered() throws RegisterException {
    EntryRequest req = EntryFactory.createRequest();

    when(
      repo.findByUserIdAndDay(
        eq(req.getUserId()),
        eq(req.getDay()))
      ).thenReturn(Optional.of(new Entry()));
    
    Exception actual = assertThrows(RegisterException.class, () -> {
      service.create(req);
    });

    assertEquals(RegisterException.ALREADY_REGISTERED, actual.getMessage());
  }

  @Test
  public void testEnterAlreadyEntered() {
    Entry entry = EntryFactory.create(EntryStatus.USED);

    when(
      repo.findByUserIdAndDay(
        eq(entry.getUserId()),
        any())
    ).thenReturn(Optional.of(entry));

    Exception actual = assertThrows(EnterException.class, () -> {
      service.enter(entry.getUserId());
    });

    assertEquals(EnterException.ALREADY_USED, actual.getMessage());
  }

  @Test
  public void testEnterNotEnoughSpace() {
    Entry entry = EntryFactory.create(EntryStatus.WAITLISTED);

    when(
      repo.findByUserIdAndDay(
        eq(entry.getUserId()),
        any())
    ).thenReturn(Optional.of(entry));

    when(
      repo.countByDayAndStatus(
        eq(entry.getDay()),
        eq(EntryStatus.WAITLISTED),
        eq(entry.getCreatedAt())))
      .thenReturn(5);

    Exception actual = assertThrows(EnterException.class, () -> {
      service.enter(entry.getUserId());
    });

    assertEquals(EnterException.NOT_ENOUGH_SPACE, actual.getMessage());
  }

  @Test
  public void testEnterSuccess() throws EnterException {
    Entry entry = EntryFactory.create(EntryStatus.ACCEPTED);

    when(
      repo.findByUserIdAndDay(
        eq(entry.getUserId()),
        any())
    ).thenReturn(Optional.of(entry));

    when(repo.save(eq(entry))).thenReturn(entry);

    assertNotNull(service.enter(entry.getUserId()).getEnteredAt());
  }

  @Test
  public void testLeaveNoOther() throws EnterException {
    Entry entry = EntryFactory.create();

    when(repo.findByUserIdAndDay(
      eq(entry.getUserId()),
      any())
    ).thenReturn(Optional.of(entry));

    when(repo.save(eq(entry))).thenReturn(entry);

    assertNotNull(service.leave(entry.getUserId()).getLeftAt());
    assertEquals(EntryStatus.USED, entry.getStatus());
    assertTrue(entry.getEnteredAt().isBefore(entry.getLeftAt()));
  }

  @Test
  public void testLeaveWithOther() throws EnterException {
    Entry entry = EntryFactory.create();
    Entry other = EntryFactory.create(EntryStatus.WAITLISTED);

    when(repo.findByUserIdAndDay(
      eq(entry.getUserId()),
      any())
    ).thenReturn(Optional.of(entry));

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
