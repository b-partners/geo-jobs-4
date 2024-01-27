package app.bpartners.geojobs.repository.model;

import static app.bpartners.geojobs.repository.model.Status.ProgressionStatus.PENDING;
import static app.bpartners.geojobs.repository.model.types.PostgresEnumType.PGSQL_ENUM_NAME;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static org.hibernate.annotations.FetchMode.SELECT;

import app.bpartners.geojobs.repository.model.geo.JobType;
import app.bpartners.geojobs.repository.model.types.PostgresEnumType;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.TypeDef;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@ToString
@MappedSuperclass
@TypeDef(name = PGSQL_ENUM_NAME, typeClass = PostgresEnumType.class)
public abstract class Job<T extends Task> implements Serializable {
  @Id protected String id;
  protected String zoneName;
  protected String emailReceiver;
  @CreationTimestamp protected Instant submissionInstant;

  // note(LazyInitializationException): thrown when fetch type is LAZY, hence using EAGER
  @OneToMany(cascade = ALL, mappedBy = "jobId", fetch = EAGER)
  @Fetch(SELECT)
  protected List<JobStatus> statusHistory;

  // note(LazyInitializationException)
  @OneToMany(mappedBy = "jobId", cascade = ALL, fetch = EAGER)
  @Fetch(SELECT)
  protected List<T> tasks = new ArrayList<>();

  public void refreshStatusHistory() {
    statusHistory.add(getStatus());
  }

  public JobStatus getStatus() {
    return JobStatus.from(
        id,
        Status.reduce(tasks.stream().map(Task::getStatus).map(status -> (Status) status).toList()),
        getJobType());
  }

  public boolean isPending() {
    return PENDING.equals(getStatus().getProgression());
  }

  public abstract JobType getJobType();
}