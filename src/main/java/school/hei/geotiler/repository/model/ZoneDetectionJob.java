package school.hei.geotiler.repository.model;

import static java.util.stream.Collectors.toList;
import static javax.persistence.CascadeType.ALL;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ZoneDetectionJob extends AbstractZoneJob<DetectionJobStatus, ZoneDetectionTask>
    implements Serializable {
  @OneToOne(cascade = ALL)
  private ZoneTilingJob zoneTilingJob;

  public DetectionJobStatus getStatus() {
    return DetectionJobStatus.from(
        getId(),
        Status.reduce(
            getStatusHistory().stream().map(status -> (Status) status).collect(toList())));
  }

  public void addStatus(DetectionJobStatus status) {
    getStatusHistory().add(status);
  }
}
