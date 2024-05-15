package app.bpartners.geojobs.service;

import static app.bpartners.geojobs.job.model.Status.HealthStatus.*;
import static app.bpartners.geojobs.job.model.Status.ProgressionStatus.*;
import static app.bpartners.geojobs.repository.model.GeoJobType.DETECTION;
import static java.time.Instant.now;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import app.bpartners.geojobs.endpoint.event.EventProducer;
import app.bpartners.geojobs.job.model.JobStatus;
import app.bpartners.geojobs.job.model.Status;
import app.bpartners.geojobs.job.model.TaskStatus;
import app.bpartners.geojobs.job.model.statistic.TaskStatistic;
import app.bpartners.geojobs.job.model.statistic.TaskStatusStatistic;
import app.bpartners.geojobs.job.repository.JobStatusRepository;
import app.bpartners.geojobs.job.repository.TaskRepository;
import app.bpartners.geojobs.model.exception.BadRequestException;
import app.bpartners.geojobs.model.exception.NotFoundException;
import app.bpartners.geojobs.repository.model.tiling.TilingTask;
import app.bpartners.geojobs.repository.model.tiling.ZoneTilingJob;
import app.bpartners.geojobs.service.detection.ZoneDetectionJobService;
import app.bpartners.geojobs.service.tiling.ZoneTilingJobService;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.stubbing.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

@Slf4j
public class ZoneTilingJobServiceTest {
  public static final String JOB_ID = "jobId";
  public static final String JOB2_ID = "job2Id";
  public static final String JOB_3_ID = "job3_id";
  JpaRepository<ZoneTilingJob, String> jobRepositoryMock = mock();
  JobStatusRepository jobStatusRepositoryMock = mock();
  TaskRepository<TilingTask> taskRepositoryMock = mock();
  EventProducer eventProducerMock = mock();
  ZoneDetectionJobService detectionJobServiceMock = mock();
  ZoneTilingJobService subject =
      new ZoneTilingJobService(
          jobRepositoryMock,
          jobStatusRepositoryMock,
          taskRepositoryMock,
          eventProducerMock,
          detectionJobServiceMock);

  @Test
  void read_task_statistics_ok() {
    when(jobRepositoryMock.findById(JOB_3_ID))
        .thenReturn(
            Optional.of(
                ZoneTilingJob.builder()
                    .statusHistory(
                        List.of(JobStatus.builder().progression(PROCESSING).health(FAILED).build()))
                    .build()));
    when(taskRepositoryMock.findAllByJobId(JOB_3_ID))
        .thenReturn(
            List.of(
                taskWithStatus(FINISHED, SUCCEEDED),
                taskWithStatus(FINISHED, SUCCEEDED),
                taskWithStatus(PENDING, UNKNOWN),
                taskWithStatus(PENDING, UNKNOWN),
                taskWithStatus(FINISHED, FAILED),
                taskWithStatus(PROCESSING, UNKNOWN)));

    TaskStatistic actual = subject.computeTaskStatistics(JOB_3_ID);

    assertEquals(JOB_3_ID, actual.getJobId());
    assertEquals(FAILED, actual.getActualJobStatus().getHealth());
    assertEquals(PROCESSING, actual.getActualJobStatus().getProgression());
    StatisticResult statisticResult = getResult(actual);
    assertEquals(2, statisticResult.unknownPendingTask().getCount());
    assertEquals(1, statisticResult.unknownProcessingTask().getCount());
    assertEquals(1, statisticResult.failedFinishedTask().getCount());
    assertEquals(2, statisticResult.succeededFinishedTask().getCount());
    assertEquals(0, statisticResult.unknownFinishedTask().getCount());
  }

  @Test
  void retry_failed_tasks_not_found_ko() {
    when(jobRepositoryMock.findById(JOB_ID)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> subject.retryFailedTask(JOB_ID));
  }

  @Test
  void retry_failed_tasks_all_tasks_not_finished_ko() {
    when(jobRepositoryMock.findById(JOB_ID))
        .thenReturn(Optional.of(ZoneTilingJob.builder().build()));
    when(jobRepositoryMock.findById(JOB2_ID))
        .thenReturn(Optional.of(ZoneTilingJob.builder().build()));
    when(taskRepositoryMock.findAllByJobId(JOB_ID))
        .thenReturn(
            List.of(taskWithStatus(FINISHED, SUCCEEDED), taskWithStatus(PROCESSING, UNKNOWN)));
    when(taskRepositoryMock.findAllByJobId(JOB2_ID))
        .thenReturn(
            List.of(taskWithStatus(FINISHED, SUCCEEDED), taskWithStatus(FINISHED, SUCCEEDED)));

    assertThrows(BadRequestException.class, () -> subject.retryFailedTask(JOB_ID));
    assertThrows(BadRequestException.class, () -> subject.retryFailedTask(JOB2_ID));
  }

  private static TilingTask taskWithStatus(
      Status.ProgressionStatus progressionStatus, Status.HealthStatus healthStatus) {
    return TilingTask.builder()
        .statusHistory(
            List.of(
                TaskStatus.builder()
                    .id(randomUUID().toString())
                    .progression(progressionStatus)
                    .jobType(DETECTION)
                    .health(healthStatus)
                    .build()))
        .build();
  }

  @Test
  void retry_failed_tasks_ok() {
    when(jobRepositoryMock.findById(JOB_ID)).thenReturn(Optional.of(new ZoneTilingJob()));
    when(jobRepositoryMock.save(ArgumentMatchers.any()))
        .thenAnswer(
            (Answer<ZoneTilingJob>)
                invocationOnMock -> {
                  Object[] args = invocationOnMock.getArguments();
                  return (ZoneTilingJob) args[0];
                });
    when(taskRepositoryMock.saveAll(ArgumentMatchers.any()))
        .thenReturn(
            List.of(
                TilingTask.builder()
                    .statusHistory(
                        List.of(
                            TaskStatus.builder()
                                .jobType(DETECTION)
                                .progression(FINISHED)
                                .health(FAILED)
                                .creationDatetime(now())
                                .build(),
                            TaskStatus.builder()
                                .jobType(DETECTION)
                                .progression(PENDING)
                                .health(RETRYING)
                                .creationDatetime(now())
                                .build()))
                    .build()));
    when(taskRepositoryMock.findAllByJobId(JOB_ID))
        .thenReturn(
            List.of(
                TilingTask.builder()
                    .statusHistory(
                        List.of(
                            TaskStatus.builder()
                                .id(randomUUID().toString())
                                .progression(FINISHED)
                                .jobType(DETECTION)
                                .health(SUCCEEDED)
                                .build()))
                    .build(),
                TilingTask.builder()
                    .statusHistory(
                        List.of(
                            TaskStatus.builder()
                                .id(randomUUID().toString())
                                .progression(FINISHED)
                                .jobType(DETECTION)
                                .health(FAILED)
                                .creationDatetime(now())
                                .build()))
                    .build()));

    ZoneTilingJob actual = subject.retryFailedTask(JOB_ID);

    assertEquals(PROCESSING, actual.getStatus().getProgression());
    assertEquals(RETRYING, actual.getStatus().getHealth());
  }

  private record StatisticResult(
      TaskStatusStatistic.HealthStatusStatistic unknownPendingTask,
      TaskStatusStatistic.HealthStatusStatistic unknownProcessingTask,
      TaskStatusStatistic.HealthStatusStatistic succeededFinishedTask,
      TaskStatusStatistic.HealthStatusStatistic failedFinishedTask,
      TaskStatusStatistic.HealthStatusStatistic unknownFinishedTask) {}

  @NonNull
  private ZoneTilingJobServiceTest.StatisticResult getResult(TaskStatistic actual) {
    var pendingTaskStatistic =
        actual.getTaskStatusStatistics().stream()
            .filter(statistic -> statistic.getProgressionStatus().equals(PENDING))
            .findFirst()
            .orElseThrow();
    var processingTaskStatistic =
        actual.getTaskStatusStatistics().stream()
            .filter(statistic -> statistic.getProgressionStatus().equals(PROCESSING))
            .findFirst()
            .orElseThrow();
    var finishedTaskStatistic =
        actual.getTaskStatusStatistics().stream()
            .filter(statistic -> statistic.getProgressionStatus().equals(FINISHED))
            .findFirst()
            .orElseThrow();
    var unknownPendingTask =
        pendingTaskStatistic.getHealthStatusStatistics().stream()
            .filter(healthStatistic -> healthStatistic.getHealthStatus().equals(UNKNOWN))
            .findFirst()
            .orElseThrow();
    var unknownProcessingTask =
        processingTaskStatistic.getHealthStatusStatistics().stream()
            .filter(healthStatistic -> healthStatistic.getHealthStatus().equals(UNKNOWN))
            .findFirst()
            .orElseThrow();
    var succeededFinishedTask =
        finishedTaskStatistic.getHealthStatusStatistics().stream()
            .filter(healthStatistic -> healthStatistic.getHealthStatus().equals(SUCCEEDED))
            .findFirst()
            .orElseThrow();
    var failedFinishedTask =
        finishedTaskStatistic.getHealthStatusStatistics().stream()
            .filter(healthStatistic -> healthStatistic.getHealthStatus().equals(FAILED))
            .findFirst()
            .orElseThrow();
    var unknownFinishedTask =
        finishedTaskStatistic.getHealthStatusStatistics().stream()
            .filter(healthStatistic -> healthStatistic.getHealthStatus().equals(UNKNOWN))
            .findFirst()
            .orElseThrow();
    return new StatisticResult(
        unknownPendingTask,
        unknownProcessingTask,
        succeededFinishedTask,
        failedFinishedTask,
        unknownFinishedTask);
  }
}
