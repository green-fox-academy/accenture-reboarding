package academy.greenfox.reboarding.app;

import academy.greenfox.reboarding.entry.EnterException;
import academy.greenfox.reboarding.entry.EntryController;
import academy.greenfox.reboarding.entry.EntryDTO;
import academy.greenfox.reboarding.entry.EntryDTOFactory;
import academy.greenfox.reboarding.entry.EntryFactory;
import academy.greenfox.reboarding.entry.EntryRequest;
import academy.greenfox.reboarding.entry.EntryService;
import academy.greenfox.reboarding.entry.EntryStatus;
import academy.greenfox.reboarding.entry.NoSuchEntryException;
import academy.greenfox.reboarding.entry.RegisterException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EntryControllerTest {

  private static ObjectMapper objectMapper;
  private static String userId;
  private static String officeId;

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private EntryService entryService;

  @BeforeAll
  public static void setUp() {
    objectMapper = new ObjectMapper();
    userId = "chuck";
    officeId = "A66";
  }

  @Test
  public void testStatusWhenValidUserId() throws Exception {
    EntryDTO acceptedEntry = EntryDTOFactory.create(userId, EntryStatus.ACCEPTED);

    when(entryService.read(anyString())).thenReturn(acceptedEntry);

    mockMvc.perform(get(EntryController.ENTRY_PATH + "/{userId}", userId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.userId", is(userId)))
        .andExpect(jsonPath("$.officeId", is(officeId)))
        .andExpect(jsonPath("$.status", is(EntryStatus.ACCEPTED.name())))
        .andExpect(jsonPath("$.day", is(LocalDate.now().toString())))
        .andExpect(jsonPath("$.waitListPosition", is(0)));
  }

  @Test
  public void testStatusWhenInvalidUserId() throws Exception {
    when(entryService.read(eq(userId))).thenThrow(new NoSuchEntryException(userId));

    mockMvc.perform(get(EntryController.ENTRY_PATH + "/{userId}", userId))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is(NoSuchEntryException.NO_SUCH_ENTRY + userId)));
  }

  @Test
  public void testRegisterWhenValidEntry() throws Exception {
    EntryRequest req = EntryFactory.createRequest();
    EntryDTO expectedResponse = EntryDTOFactory.create(req.getUserId(), EntryStatus.ACCEPTED, req.getDay());

    when(entryService.create(eq(req))).thenReturn(expectedResponse);

    mockMvc.perform(post(EntryController.ENTRY_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.userId", is(req.getUserId())))
        .andExpect(jsonPath("$.officeId", is(officeId)))
        .andExpect(jsonPath("$.seatId").exists())
        .andExpect(jsonPath("$.day", is(req.getDay().toString())));
  }

  @Test
  public void testRegisterWhenExistingEntry() throws Exception {
    EntryRequest entry = EntryFactory.createRequest();

    when(entryService.create(eq(entry)))
        .thenThrow(new RegisterException(RegisterException.ALREADY_REGISTERED));

    mockMvc.perform(post(EntryController.ENTRY_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(entry)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is(RegisterException.ALREADY_REGISTERED)));
  }

  @Test
  public void testEnterWhenValidUserId() throws Exception {
    EntryDTO acceptedEntry = EntryDTOFactory.createEntered(userId);

    when(entryService.enter(eq(userId))).thenReturn(acceptedEntry);

    mockMvc.perform(put(EntryController.ENTRY_PATH + "/{userId}/enter", userId))
        .andExpect(status().isAccepted())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.userId", is(userId)))
        .andExpect(jsonPath("$.officeId", is(officeId)))
        .andExpect(jsonPath("$.status", is(EntryStatus.ACCEPTED.name())))
        .andExpect(jsonPath("$.enteredAt").exists());
  }

  @Test
  public void testEnterWhenInvalidUserId() throws Exception {
    when(entryService.enter(eq(userId))).thenThrow(new NoSuchEntryException(userId));

    mockMvc.perform(put(EntryController.ENTRY_PATH + "/{userId}/enter", userId))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is(NoSuchEntryException.NO_SUCH_ENTRY + userId)));
  }

  @Test
  public void testEnterWhenAlreadyUsedUserId() throws Exception {
    when(entryService.enter(eq(userId))).thenThrow(new EnterException(EnterException.ALREADY_USED));

    mockMvc.perform(put(EntryController.ENTRY_PATH + "/{userId}/enter", userId))
        .andExpect(status().isForbidden())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is(EnterException.ALREADY_USED)));
  }

  @Test
  public void testEnterWhenOfficeIsFull() throws Exception {
    when(entryService.enter(eq(userId))).thenThrow(new EnterException(EnterException.NOT_ENOUGH_SPACE));

    mockMvc.perform(put(EntryController.ENTRY_PATH + "/{userId}/enter", userId))
        .andExpect(status().isForbidden())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is(EnterException.NOT_ENOUGH_SPACE)));
  }

  @Test
  public void testLeaveWhenValidUserId() throws Exception {
    EntryDTO usedEntry = EntryDTOFactory.createUsed(userId);

    when(entryService.leave(eq(userId))).thenReturn(usedEntry);

    mockMvc.perform(put(EntryController.ENTRY_PATH + "/{userId}/leave", userId))
        .andExpect(status().isAccepted())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.userId", is(userId)))
        .andExpect(jsonPath("$.officeId", is(officeId)))
        .andExpect(jsonPath("$.status", is(EntryStatus.USED.name())))
        .andExpect(jsonPath("$.enteredAt").exists())
        .andExpect(jsonPath("$.leftAt").exists());
  }

  @Test
  public void testLeaveWhenInvalidUserId() throws Exception {
    when(entryService.leave(eq(userId))).thenThrow(new NoSuchEntryException(userId));

    mockMvc.perform(put(EntryController.ENTRY_PATH + "/{userId}/leave", userId))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is(NoSuchEntryException.NO_SUCH_ENTRY + userId)));
  }
}
