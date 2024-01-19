package school.hei.geojobs.service.event;

import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.geojobs.endpoint.event.gen.ZoneDetectionJobCreated;
import school.hei.geojobs.service.ZoneDetectionJobService;

@Service
@AllArgsConstructor
public class ZoneDetectionJobCreatedService implements Consumer<ZoneDetectionJobCreated> {
  private final ZoneDetectionJobService zoneDetectionJobService;

  @Override
  public void accept(ZoneDetectionJobCreated zoneDetectionJobCreated) {
    var zoneDetectionJob = zoneDetectionJobCreated.getZoneDetectionJob();
    zoneDetectionJobService.process(zoneDetectionJob.getId());
  }
}
