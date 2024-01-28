package app.bpartners.geojobs.service;

import static app.bpartners.geojobs.repository.model.Status.HealthStatus.FAILED;
import static app.bpartners.geojobs.repository.model.Status.HealthStatus.SUCCEEDED;
import static app.bpartners.geojobs.repository.model.Status.HealthStatus.UNKNOWN;
import static app.bpartners.geojobs.repository.model.Status.ProgressionStatus.FINISHED;
import static app.bpartners.geojobs.repository.model.Status.ProgressionStatus.PROCESSING;
import static java.time.Instant.now;
import static java.util.UUID.randomUUID;

import app.bpartners.geojobs.model.exception.NotFoundException;
import app.bpartners.geojobs.repository.model.Job;
import app.bpartners.geojobs.repository.model.Status.HealthStatus;
import app.bpartners.geojobs.repository.model.Status.ProgressionStatus;
import app.bpartners.geojobs.repository.model.Task;
import app.bpartners.geojobs.repository.model.TaskStatus;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class TaskStatusService<T extends Task, J extends Job<T>> {

  protected final JpaRepository<T, String> repository;
  protected final JobService<T, J> jobService;

  @Transactional
  public T process(T task) {
    return updateStatus(task, PROCESSING, UNKNOWN);
  }

  @Transactional
  public T succeed(T task) {
    return updateStatus(task, FINISHED, SUCCEEDED);
  }

  @Transactional
  public T fail(T task) {
    return updateStatus(task, FINISHED, FAILED);
  }

  private T updateStatus(T task, ProgressionStatus progression, HealthStatus health) {
    task.addStatus(
        TaskStatus.builder()
            .id(randomUUID().toString())
            .creationDatetime(now())
            .progression(progression)
            .health(health)
            .taskId(task.getId())
            .build());
    return update(task);
  }

  private T update(T task) {
    var taskId = task.getId();
    if (!repository.existsById(taskId)) {
      throw new NotFoundException("task.id=" + taskId);
    }

    var updated = repository.save(task);
    var job = jobService.findById(task.getJobId());
    jobService.refreshStatus(job);
    return updated;
  }
}
