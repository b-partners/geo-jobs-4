package app.bpartners.geojobs.job.service;

import static app.bpartners.geojobs.job.model.Status.HealthStatus.FAILED;
import static app.bpartners.geojobs.job.model.Status.HealthStatus.SUCCEEDED;
import static app.bpartners.geojobs.job.model.Status.HealthStatus.UNKNOWN;
import static app.bpartners.geojobs.job.model.Status.ProgressionStatus.FINISHED;
import static app.bpartners.geojobs.job.model.Status.ProgressionStatus.PROCESSING;
import static java.time.Instant.now;

import app.bpartners.geojobs.job.model.Job;
import app.bpartners.geojobs.job.model.Status.HealthStatus;
import app.bpartners.geojobs.job.model.Status.ProgressionStatus;
import app.bpartners.geojobs.job.model.Task;
import app.bpartners.geojobs.job.model.TaskStatus;
import app.bpartners.geojobs.job.repository.TaskStatusRepository;
import org.springframework.transaction.annotation.Transactional;

public class TaskStatusService<T extends Task, J extends Job> {

  protected final TaskStatusRepository taskStatusRepository;
  protected final JobService<T, J> jobService;

  public TaskStatusService(TaskStatusRepository taskStatusRepository, JobService<T, J> jobService) {
    this.taskStatusRepository = taskStatusRepository;
    this.jobService = jobService;
  }

  @Transactional
  public T process(T task) {
    return update(task, PROCESSING, UNKNOWN);
  }

  @Transactional
  public T succeed(T task) {
    return update(task, FINISHED, SUCCEEDED);
  }

  @Transactional
  public T fail(T task) {
    return update(task, FINISHED, FAILED);
  }

  private T update(T task, ProgressionStatus progression, HealthStatus health) {
    var jobIb = task.getJobId();
    var oldJob = jobService.findById(jobIb);

    var taskStatus =
        TaskStatus.builder()
            .creationDatetime(now())
            .progression(progression)
            .health(health)
            .taskId(task.getId())
            .build();
    task.hasNewStatus(taskStatus);
    taskStatusRepository.save(taskStatus);
    jobService.recomputeStatus(oldJob);

    return task;
  }
}
