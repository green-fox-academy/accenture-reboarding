package academy.greenfox.reboarding.entry;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntryRequest {

  @NotEmpty
  String userId;
  @NotEmpty
  String officeId;
  @NotNull
  @JsonSerialize(using = ToStringSerializer.class)
  LocalDate day;

}
