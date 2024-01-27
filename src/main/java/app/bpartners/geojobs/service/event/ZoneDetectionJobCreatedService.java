package app.bpartners.geojobs.service.event;

import app.bpartners.geojobs.endpoint.event.gen.ZoneDetectionJobCreated;
import app.bpartners.geojobs.service.ZoneDetectionJobService;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ZoneDetectionJobCreatedService implements Consumer<ZoneDetectionJobCreated> {
  private final ZoneDetectionJobService zoneDetectionJobService;

  @Override
  public void accept(ZoneDetectionJobCreated zoneDetectionJobCreated) {
    var zoneDetectionJob = zoneDetectionJobCreated.getZoneDetectionJob();
    zoneDetectionJobService.fireTasks(zoneDetectionJob.getId());
  }
}
