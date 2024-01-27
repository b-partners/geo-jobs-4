package app.bpartners.geojobs.service.event;

import app.bpartners.geojobs.endpoint.event.gen.ZoneTilingJobStatusChanged;
import app.bpartners.geojobs.repository.model.JobStatus;
import app.bpartners.geojobs.repository.model.Status;
import app.bpartners.geojobs.repository.model.geo.tiling.ZoneTilingJob;
import app.bpartners.geojobs.service.geo.tiling.TilingMailer;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ZoneTilingJobStatusChangedService implements Consumer<ZoneTilingJobStatusChanged> {

  private TilingMailer tilingMailer;

  @Override
  public void accept(ZoneTilingJobStatusChanged zoneTilingJobStatusChanged) {
    ZoneTilingJob newJob = zoneTilingJobStatusChanged.getNewJob();

    JobStatus status = newJob.getStatus();

    if (Status.ProgressionStatus.FINISHED.equals(status.getProgression())
        && Status.HealthStatus.SUCCEEDED.equals(status.getHealth())) {
      tilingMailer.accept(newJob);
    }
  }
}
