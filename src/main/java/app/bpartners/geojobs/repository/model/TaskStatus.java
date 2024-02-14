package app.bpartners.geojobs.repository.model;

import static java.util.UUID.randomUUID;

import app.bpartners.geojobs.repository.conf.JobTypeConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@PrimaryKeyJoinColumn(name = "id")
@Entity
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Table(name = "task_status")
public class TaskStatus extends Status {
  @JoinColumn private String taskId;

  @JsonIgnore // TODO(status.jobType-serialization)
  @Convert(converter = JobTypeConverter.class)
  private JobType jobType;

  public static TaskStatus from(String id, Status status, JobType jobType) {
    return TaskStatus.builder()
        .taskId(id)
        .id(randomUUID().toString())
        .jobType(jobType)
        .progression(status.getProgression())
        .health(status.getHealth())
        .creationDatetime(status.getCreationDatetime())
        .build();
  }
}
