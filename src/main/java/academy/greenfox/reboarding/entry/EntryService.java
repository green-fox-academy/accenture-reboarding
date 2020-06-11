package academy.greenfox.reboarding.entry;

public interface EntryService {
  EntryDTO create(Entry entry) throws RegisterException;
  EntryDTO read(String userId);
  EntryDTO convert(Entry entry);
  int getWaitListSize(Entry entry);
  EntryDTO enter(String userId) throws EnterException;
  EntryDTO leave(String userId);
}
