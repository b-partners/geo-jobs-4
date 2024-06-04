package app.bpartners.geojobs.endpoint.event.model;

import app.bpartners.geojobs.repository.model.tiling.ZoneTilingJob;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.processing.Generated;
import lombok.*;

@Generated("EventBridge")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@EqualsAndHashCode
@ToString
public class ZoneTilingJobWithoutTasksCreated {
  @JsonProperty("originalJob")
  private ZoneTilingJob originalJob;

  @JsonProperty("duplicatedJobId")
  private String duplicatedJobId;
}
