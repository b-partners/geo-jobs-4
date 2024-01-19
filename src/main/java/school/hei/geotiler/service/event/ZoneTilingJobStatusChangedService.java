package school.hei.geotiler.service.event;

import static school.hei.geotiler.repository.model.Status.HealthStatus.SUCCEEDED;
import static school.hei.geotiler.repository.model.Status.ProgressionStatus.FINISHED;

import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.geotiler.endpoint.event.gen.ZoneTilingJobStatusChanged;
import school.hei.geotiler.repository.model.TilingJobStatus;
import school.hei.geotiler.repository.model.ZoneTilingJob;
import school.hei.geotiler.service.EmailService;

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
