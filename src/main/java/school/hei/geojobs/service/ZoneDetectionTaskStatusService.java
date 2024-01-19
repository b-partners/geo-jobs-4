package school.hei.geojobs.service;

import static java.time.Instant.now;
import static java.util.UUID.randomUUID;
import static school.hei.geojobs.repository.model.Status.HealthStatus.FAILED;
import static school.hei.geojobs.repository.model.Status.HealthStatus.SUCCEEDED;
import static school.hei.geojobs.repository.model.Status.HealthStatus.UNKNOWN;
import static school.hei.geojobs.repository.model.Status.ProgressionStatus.FINISHED;
import static school.hei.geojobs.repository.model.Status.ProgressionStatus.PROCESSING;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.geojobs.model.exception.NotFoundException;
import school.hei.geojobs.repository.ZoneDetectionTaskRepository;
import school.hei.geojobs.repository.model.DetectionTaskStatus;
import school.hei.geojobs.repository.model.Status.HealthStatus;
import school.hei.geojobs.repository.model.Status.ProgressionStatus;
import school.hei.geojobs.repository.model.ZoneDetectionTask;

@Service
@AllArgsConstructor
public class ZoneDetectionTaskStatusService {
  private final ZoneDetectionTaskRepository repository;
  private final ZoneDetectionJobService zoneDetectionJobService;

  public ZoneDetectionTask process(ZoneDetectionTask task) {
    return updateStatus(task, PROCESSING, UNKNOWN);
  }

  public ZoneDetectionTask succeed(ZoneDetectionTask task) {
    return updateStatus(task, FINISHED, SUCCEEDED);
  }

  public ZoneDetectionTask fail(ZoneDetectionTask task) {
    return updateStatus(task, FINISHED, FAILED);
  }

  private ZoneDetectionTask updateStatus(
      ZoneDetectionTask task, ProgressionStatus progression, HealthStatus health) {
    task.addStatus(
        DetectionTaskStatus.builder()
            .id(randomUUID().toString())
            .creationDatetime(now())
            .progression(progression)
            .health(health)
            .taskId(task.getId())
            .build());
    return update(task);
  }

  private ZoneDetectionTask update(ZoneDetectionTask zoneDetectionTask) {
    if (!repository.existsById(zoneDetectionTask.getId())) {
      throw new NotFoundException("ZoneDetection.Id = " + zoneDetectionTask.getId() + " not found");
    }

    var updated = repository.save(zoneDetectionTask);
    zoneDetectionJobService.refreshStatus(zoneDetectionTask.getJobId());

    return updated;
  }
}
