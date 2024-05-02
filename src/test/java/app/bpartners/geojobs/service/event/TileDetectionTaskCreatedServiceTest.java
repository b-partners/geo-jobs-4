package app.bpartners.geojobs.service.event;

import static app.bpartners.geojobs.job.model.Status.HealthStatus.SUCCEEDED;
import static app.bpartners.geojobs.job.model.Status.HealthStatus.UNKNOWN;
import static app.bpartners.geojobs.job.model.Status.ProgressionStatus.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import app.bpartners.geojobs.endpoint.event.EventProducer;
import app.bpartners.geojobs.endpoint.event.gen.TileDetectionTaskCreated;
import app.bpartners.geojobs.job.model.TaskStatus;
import app.bpartners.geojobs.job.service.RetryableTaskToTaskStatusService;
import app.bpartners.geojobs.model.exception.ApiException;
import app.bpartners.geojobs.repository.model.TileDetectionTask;
import app.bpartners.geojobs.repository.model.detection.DetectableType;
import app.bpartners.geojobs.repository.model.detection.DetectionTask;
import app.bpartners.geojobs.repository.model.detection.ZoneDetectionJob;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class TileDetectionTaskCreatedServiceTest {
  RetryableTaskToTaskStatusService<TileDetectionTask, DetectionTask, ZoneDetectionJob>
      taskToTaskStatusServiceMock = mock();
  TileDetectionTaskCreatedConsumer tileDetectionTaskConsumerMock = mock();
  EventProducer eventProducerMock = mock();
  TileDetectionTaskCreatedService subject =
      new TileDetectionTaskCreatedService(
          taskToTaskStatusServiceMock, tileDetectionTaskConsumerMock, eventProducerMock);

  @Test
  void accept_ok() {
    doNothing().when(tileDetectionTaskConsumerMock).accept(any());
    when(taskToTaskStatusServiceMock.process(any()))
        .thenReturn(
            TileDetectionTask.builder()
                .statusHistory(
                    List.of(TaskStatus.builder().progression(PROCESSING).health(UNKNOWN).build()))
                .build());
    when(taskToTaskStatusServiceMock.succeed(any()))
        .thenReturn(
            TileDetectionTask.builder()
                .statusHistory(
                    List.of(TaskStatus.builder().progression(FINISHED).health(SUCCEEDED).build()))
                .build());

    assertDoesNotThrow(
        () ->
            subject.accept(
                new TileDetectionTaskCreated(
                    TileDetectionTask.builder()
                        .statusHistory(
                            List.of(
                                TaskStatus.builder()
                                    .progression(FINISHED)
                                    .health(SUCCEEDED)
                                    .build()))
                        .build(),
                    List.of(DetectableType.PATHWAY))));
    var tileDetectionTaskCaptor = ArgumentCaptor.forClass(TileDetectionTask.class);
    verify(taskToTaskStatusServiceMock, times(1)).succeed(tileDetectionTaskCaptor.capture());
    TileDetectionTask capturedTileDetectionTask = tileDetectionTaskCaptor.getValue();
    assertEquals(FINISHED, capturedTileDetectionTask.getStatus().getProgression());
    assertEquals(SUCCEEDED, capturedTileDetectionTask.getStatus().getHealth());
  }

  @Test
  void accept_ko() {
    doThrow(ApiException.class).when(tileDetectionTaskConsumerMock).accept(any());
    when(taskToTaskStatusServiceMock.process(any()))
        .thenReturn(
            TileDetectionTask.builder()
                .statusHistory(
                    List.of(TaskStatus.builder().progression(PROCESSING).health(UNKNOWN).build()))
                .build());
    when(taskToTaskStatusServiceMock.succeed(any()))
        .thenReturn(
            TileDetectionTask.builder()
                .statusHistory(
                    List.of(TaskStatus.builder().progression(FINISHED).health(SUCCEEDED).build()))
                .build());

    assertDoesNotThrow(
        () ->
            subject.accept(
                new TileDetectionTaskCreated(
                    TileDetectionTask.builder().build(), List.of(DetectableType.PATHWAY))));
    var eventCaptor = ArgumentCaptor.forClass(List.class);
    var tileDetectionTaskCaptor = ArgumentCaptor.forClass(TileDetectionTask.class);
    verify(taskToTaskStatusServiceMock, times(0)).succeed(tileDetectionTaskCaptor.capture());
    verify(eventProducerMock, times(1)).accept(eventCaptor.capture());
  }
}
