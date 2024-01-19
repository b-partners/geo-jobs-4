package school.hei.geotiler.endpoint.event.gen;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import javax.annotation.processing.Generated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import school.hei.geotiler.repository.model.ZoneDetectionJob;

@Generated("EventBridge")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@EqualsAndHashCode
@ToString
public class ZoneDetectionJobStatusChanged implements Serializable {
  @JsonProperty("oldJob")
  private ZoneDetectionJob oldJob;

  @JsonProperty("newJob")
  private ZoneDetectionJob newJob;
}
