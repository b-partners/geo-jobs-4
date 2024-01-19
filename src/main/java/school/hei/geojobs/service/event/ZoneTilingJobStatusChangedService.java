package school.hei.geojobs.service.event;

import static school.hei.geojobs.repository.model.Status.HealthStatus.SUCCEEDED;
import static school.hei.geojobs.repository.model.Status.ProgressionStatus.FINISHED;

import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.geojobs.endpoint.event.gen.ZoneTilingJobStatusChanged;
import school.hei.geojobs.repository.model.TilingJobStatus;
import school.hei.geojobs.repository.model.ZoneTilingJob;
import school.hei.geojobs.service.EmailService;

@Service
@AllArgsConstructor
public class ZoneTilingJobStatusChangedService implements Consumer<ZoneTilingJobStatusChanged> {

  private EmailService emailService;

  @Override
  public void accept(ZoneTilingJobStatusChanged zoneTilingJobStatusChanged) {
    ZoneTilingJob newJob = zoneTilingJobStatusChanged.getNewJob();

    TilingJobStatus status = newJob.getStatus();

    if (FINISHED.equals(status.getProgression()) && SUCCEEDED.equals(status.getHealth())) {
      emailService.sendEmail(newJob);
    }
  }
}
