package app.bpartners.geojobs.service.geo.detection;

import app.bpartners.geojobs.model.exception.NotFoundException;
import app.bpartners.geojobs.repository.DetectionTaskRepository;
import app.bpartners.geojobs.repository.model.geo.detection.DetectionTask;
import app.bpartners.geojobs.service.TaskStatusService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DetectionTaskStatusService {
  private final DetectionTaskRepository repository;
  private final ZoneDetectionJobService zoneDetectionJobService;
  private final TaskStatusService<DetectionTask> taskStatusService;

  public DetectionTask process(DetectionTask task) {
    return taskStatusService.process(task, this::update);
  }

  public DetectionTask succeed(DetectionTask task) {
    return taskStatusService.succeed(task, this::update);
  }

  public DetectionTask fail(DetectionTask task) {
    return taskStatusService.fail(task, this::update);
  }

  private DetectionTask update(DetectionTask detectionTask) {
    if (!repository.existsById(detectionTask.getId())) {
      throw new NotFoundException("detection.Id = " + detectionTask.getId() + " not found");
    }

    var updated = repository.save(detectionTask);
    zoneDetectionJobService.refreshStatus(detectionTask.getJobId());
    return updated;
  }
}
