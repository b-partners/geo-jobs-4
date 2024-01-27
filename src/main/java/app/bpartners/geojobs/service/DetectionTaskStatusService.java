package app.bpartners.geojobs.service;

import app.bpartners.geojobs.model.exception.NotFoundException;
import app.bpartners.geojobs.repository.DetectionTaskRepository;
import app.bpartners.geojobs.repository.model.ZoneDetectionTask;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DetectionTaskStatusService {
  private final DetectionTaskRepository repository;
  private final ZoneDetectionJobService zoneDetectionJobService;
  private final TaskStatusService<ZoneDetectionTask> taskStatusService;

  public ZoneDetectionTask process(ZoneDetectionTask task) {
    return taskStatusService.process(task, this::update);
  }

  public ZoneDetectionTask succeed(ZoneDetectionTask task) {
    return taskStatusService.succeed(task, this::update);
  }

  public ZoneDetectionTask fail(ZoneDetectionTask task) {
    return taskStatusService.fail(task, this::update);
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
