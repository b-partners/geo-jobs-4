package app.bpartners.geojobs.service.tiling;

import static java.time.temporal.ChronoUnit.MINUTES;

import app.bpartners.geojobs.job.repository.TaskStatusRepository;
import app.bpartners.geojobs.job.service.JobService;
import app.bpartners.geojobs.job.service.RetryableTaskStatusService;
import app.bpartners.geojobs.job.service.TaskStatusService;
import app.bpartners.geojobs.repository.model.tiling.TilingTask;
import app.bpartners.geojobs.repository.model.tiling.ZoneTilingJob;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class TilingTaskStatusService extends TaskStatusService<TilingTask, ZoneTilingJob> {

  public TilingTaskStatusService(
      TaskStatusRepository taskStatusRepository,
      JobService<TilingTask, ZoneTilingJob> zoneJobService) {
    super(taskStatusRepository, zoneJobService);
  }

  @Bean
  public RetryableTaskStatusService<TilingTask, ZoneTilingJob> getRetryableTilingTaskStatusService(
      TilingTaskStatusService taskStatusService,
      @Value("${jobs.status.update.retry.max.sleep.minutes:1}") int maxSleepDurationInMinutes,
      @Value("${jobs.status.update.retry.max.attempt:5}") int maxRetry) {
    return new RetryableTaskStatusService<>(
        taskStatusService, Duration.of(maxSleepDurationInMinutes, MINUTES), maxRetry);
  }
}
