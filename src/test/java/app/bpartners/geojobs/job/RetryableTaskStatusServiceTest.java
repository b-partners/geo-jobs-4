package app.bpartners.geojobs.job;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.bpartners.geojobs.job.model.JobType;
import app.bpartners.geojobs.job.model.Task;
import app.bpartners.geojobs.job.service.RetryableTaskStatusService;
import app.bpartners.geojobs.job.service.TaskStatusService;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class RetryableTaskStatusServiceTest {
  TaskStatusService service = mock();
  RetryableTaskStatusService subject =
      new RetryableTaskStatusService(service, Duration.of(5, SECONDS), 3);

  @Test
  void do_all_retries() {
    var task = aTask();
    when(service.process(task)).thenThrow(new RuntimeException());

    assertThrows(RuntimeException.class, () -> subject.process(task));

    verify(service, times(1 /*try*/ + 3 /*retry*/)).process(task);
  }

  private static Task aTask() {
    return new Task() {
      @Override
      public JobType getJobType() {
        return null;
      }
    };
  }
}
