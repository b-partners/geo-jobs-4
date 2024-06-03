package app.bpartners.geojobs.service.tiling;

import app.bpartners.geojobs.mail.Email;
import app.bpartners.geojobs.mail.Mailer;
import app.bpartners.geojobs.repository.model.FilteredTilingJob;
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
public class TilingFilteredMailer implements Consumer<FilteredTilingJob> {
  private final Mailer mailer;
  private static final String TEMPLATE_NAME = "job_filtering";
  private final HTMLTemplateParser htmlTemplateParser;
  private final String env = System.getenv("ENV");

  @SneakyThrows
  @Override
  public void accept(FilteredTilingJob filteredTilingJob) {
    var initialJobId = filteredTilingJob.getInitialJobId();
    var succeededJob = filteredTilingJob.getSucceededJob();
    var notSucceededJob = filteredTilingJob.getNotSucceededJob();
    Context context = new Context();
    context.setVariable("initialJobId", initialJobId);
    context.setVariable("succeededJob", succeededJob);
    context.setVariable("notSucceededJob", notSucceededJob);
    String emailBody = htmlTemplateParser.apply(TEMPLATE_NAME, context);

    mailer.accept(
        new Email(
            new InternetAddress(succeededJob.getEmailReceiver()),
            List.of(),
            List.of(),
            "[geo-jobs/" + env + "] Séparation des tâches du job [ID=" + initialJobId + " términée",
            emailBody,
            List.of()));
  }
}
