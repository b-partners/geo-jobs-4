package app.bpartners.geojobs.service.geo.detection;

import app.bpartners.geojobs.repository.model.geo.detection.DetectionTask;
import app.bpartners.geojobs.repository.model.geo.detection.ZoneDetectionJob;
import app.bpartners.geojobs.service.TaskStatusService;
import app.bpartners.geojobs.service.ZoneJobService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class DetectionTaskStatusService extends TaskStatusService<DetectionTask, ZoneDetectionJob> {

  public DetectionTaskStatusService(
      JpaRepository<DetectionTask, String> repository,
      ZoneJobService<DetectionTask, ZoneDetectionJob> zoneJobService) {
    super(repository, zoneJobService);
  }
}
