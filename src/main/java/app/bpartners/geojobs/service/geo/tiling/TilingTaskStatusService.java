package app.bpartners.geojobs.service.geo.tiling;

import app.bpartners.geojobs.repository.model.geo.tiling.TilingTask;
import app.bpartners.geojobs.repository.model.geo.tiling.ZoneTilingJob;
import app.bpartners.geojobs.service.JobService;
import app.bpartners.geojobs.service.TaskStatusService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class TilingTaskStatusService extends TaskStatusService<TilingTask, ZoneTilingJob> {

  public TilingTaskStatusService(
      JpaRepository<TilingTask, String> repository,
      JobService<TilingTask, ZoneTilingJob> zoneJobService) {
    super(repository, zoneJobService);
  }
}
