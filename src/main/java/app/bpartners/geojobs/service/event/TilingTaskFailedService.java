package app.bpartners.geojobs.service.event;

import static app.bpartners.geojobs.job.model.Status.HealthStatus.SUCCEEDED;
import static app.bpartners.geojobs.job.model.Status.HealthStatus.UNKNOWN;
import static app.bpartners.geojobs.job.model.Status.ProgressionStatus.FINISHED;
import static app.bpartners.geojobs.job.model.Status.ProgressionStatus.PROCESSING;
import static app.bpartners.geojobs.service.event.TilingTaskConsumer.withNewStatus;

import app.bpartners.geojobs.endpoint.event.EventProducer;
import app.bpartners.geojobs.endpoint.event.gen.TilingTaskFailed;
import app.bpartners.geojobs.endpoint.event.gen.TilingTaskSucceeded;
import app.bpartners.geojobs.job.service.RetryableTaskStatusService;
import app.bpartners.geojobs.repository.model.tiling.TilingTask;
import app.bpartners.geojobs.repository.model.tiling.ZoneTilingJob;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class TilingTaskFailedService implements Consumer<TilingTaskFailed> {

  private final RetryableTaskStatusService<TilingTask, ZoneTilingJob> taskStatusService;
  private final TilingTaskConsumer tilingTaskConsumer;
  private final EventProducer eventProducer;

  private static final int MAX_ATTEMPT = 3;

  @Override
  public void accept(TilingTaskFailed tilingTaskFailed) {
    var task = tilingTaskFailed.getTask();
    var attemptNb = tilingTaskFailed.getAttemptNb();
    if (attemptNb > MAX_ATTEMPT) {
      taskStatusService.fail(task);
      log.error("Max attempt reached for tilingTaskFailed={}", tilingTaskFailed);
      return;
    }

    try {
      tilingTaskConsumer.accept(task);
    } catch (Exception e) {
      var errorMessage = e.getMessage() + ", stackTrace=" + stackTraceToString(e);
      eventProducer.accept(
          List.of(
              new TilingTaskFailed(
                  withNewStatus(task, PROCESSING, UNKNOWN, errorMessage), attemptNb + 1)));
      return;
    }

    eventProducer.accept(
        List.of(new TilingTaskSucceeded(withNewStatus(task, FINISHED, SUCCEEDED, null))));
  }

  private String stackTraceToString(Exception e) {
    var sw = new StringWriter();
    var pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    return pw.toString();
  }
}
