package school.hei.geojobs.repository.model;

import static java.util.UUID.randomUUID;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@PrimaryKeyJoinColumn(name = "id")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString
@Table(name = "zone_detection_job_status")
public class DetectionJobStatus extends Status {
  @JoinColumn(referencedColumnName = "id")
  private String jobId;

  public static DetectionJobStatus from(String id, Status status) {
    return DetectionJobStatus.builder()
        .jobId(id)
        .id(randomUUID().toString())
        .progression(status.getProgression())
        .health(status.getHealth())
        .creationDatetime(status.getCreationDatetime())
        .build();
  }
}
