package app.bpartners.geojobs.job.service;

import static java.lang.Thread.sleep;

import app.bpartners.geojobs.job.model.Job;
import app.bpartners.geojobs.job.model.Task;
import java.time.Duration;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class RetryableTaskStatusService<T extends Task, J extends Job> {

  private final TaskStatusService<T, J> taskStatusService;
  private final Duration maxSleepDuration;
  private final int maxRetry;

  public T process(T task) {
    return retry(taskStatusService::process, task, 0);
  }

  public T succeed(T task) {
    return retry(taskStatusService::succeed, task, 0);
  }

  public T fail(T task) {
    return retry(taskStatusService::fail, task, 0);
  }

  @SneakyThrows
  private T retry(Function<T, T> taskFunction, T task, int attemptNb) {
    try {
      return taskFunction.apply(task);
    } catch (Exception e) {
      if (attemptNb >= maxRetry) {
        throw e;
      }
      var sleepDuration = (long) (maxSleepDuration.toMillis() * Math.random());
      log.info("Retry: attemptNb={}/{}, sleep(ms)={}", attemptNb, maxRetry, sleepDuration, e);
      sleep(sleepDuration);
      return retry(taskFunction, task, attemptNb + 1);
    }
  }
}
