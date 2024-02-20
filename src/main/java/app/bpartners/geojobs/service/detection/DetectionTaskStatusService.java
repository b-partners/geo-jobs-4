package app.bpartners.geojobs.service.detection;

import static java.time.temporal.ChronoUnit.MINUTES;

import app.bpartners.geojobs.job.repository.TaskStatusRepository;
import app.bpartners.geojobs.job.service.JobService;
import app.bpartners.geojobs.job.service.RetryableTaskStatusService;
import app.bpartners.geojobs.job.service.TaskStatusService;
import app.bpartners.geojobs.repository.model.detection.DetectionTask;
import app.bpartners.geojobs.repository.model.detection.ZoneDetectionJob;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class DetectionTaskStatusService extends TaskStatusService<DetectionTask, ZoneDetectionJob> {

  public DetectionTaskStatusService(
      TaskStatusRepository taskStatusRepository,
      JobService<DetectionTask, ZoneDetectionJob> zoneJobService) {
    super(taskStatusRepository, zoneJobService);
  }

  @Bean
  public RetryableTaskStatusService<DetectionTask, ZoneDetectionJob>
      getRetryableDetectionTaskStatusService(
          DetectionTaskStatusService taskStatusService,
          @Value("${jobs.status.update.max.sleep.minutes:1}") int maxSleepDurationInMinutes,
          @Value("${jobs.status.update.retry.max.attempt:5}") int maxRetry) {
    return new RetryableTaskStatusService<>(
        taskStatusService, Duration.of(maxSleepDurationInMinutes, MINUTES), maxRetry);
  }
}
