package app.bpartners.geojobs.service.detection;

import app.bpartners.geojobs.mail.Email;
import app.bpartners.geojobs.mail.Mailer;
import app.bpartners.geojobs.repository.model.detection.ZoneDetectionJob;
import app.bpartners.geojobs.template.HTMLTemplateParser;
import jakarta.mail.internet.InternetAddress;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
public class DetectionFinishedMailer implements Consumer<ZoneDetectionJob> {
  private final Mailer mailer;
  public static final String ZDJ_FINISHED_TEMPLATE = "machine_zdj_finished";
  private final HTMLTemplateParser htmlTemplateParser;
  private final String env = System.getenv("ENV");

  @SneakyThrows
  @Override
  public void accept(ZoneDetectionJob job) {
    Context context = new Context();
    context.setVariable("job", job);
    context.setVariable("status", String.valueOf(job.getStatus()));
    String emailBody = htmlTemplateParser.apply(ZDJ_FINISHED_TEMPLATE, context);
    String zoneName = job.getZoneName();

    mailer.accept(
        new Email(
            new InternetAddress(job.getEmailReceiver()),
            List.of(),
            List.of(),
            "[GEO-JOBS/"
                + env
                + "] Tâche de détection-machine [ID="
                + job.getId()
                + ", Zone="
                + zoneName
                + "] terminée",
            emailBody,
            List.of()));
  }
}
