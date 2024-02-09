package app.bpartners.geojobs.repository.model;

import static app.bpartners.geojobs.repository.model.Status.HealthStatus.FAILED;
import static app.bpartners.geojobs.repository.model.Status.HealthStatus.UNKNOWN;
import static app.bpartners.geojobs.repository.model.Status.ProgressionStatus.FINISHED;
import static app.bpartners.geojobs.repository.model.Status.ProgressionStatus.PENDING;
import static app.bpartners.geojobs.repository.model.Status.ProgressionStatus.PROCESSING;
import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import app.bpartners.geojobs.repository.model.Status.HealthStatus;
import app.bpartners.geojobs.repository.model.Status.ProgressionStatus;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class StatusTest {

  @Test
  void huge_reduction() {
    var statuses = new ArrayList<Status>();
    for (int i = 0; i < 2_000_000; i++) {
      Status aProcessing = aStatus(PROCESSING, UNKNOWN);
      statuses.add(aProcessing);
    }

    var reduced = Status.reduce(statuses);

    assertEquals(PROCESSING, reduced.getProgression());
    assertEquals(UNKNOWN, reduced.getHealth());
  }

  @Test
  void finished_is_terminal() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Status.reduce(List.of(aStatus(FINISHED, FAILED), aStatus(PROCESSING, UNKNOWN))));
    assertThrows(
        IllegalArgumentException.class,
        () -> Status.reduce(List.of(aStatus(FINISHED, FAILED), aStatus(PENDING, UNKNOWN))));
  }

  private static Status aStatus(ProgressionStatus progression, HealthStatus health) {
    var status = new Status();
    status.setProgression(progression);
    status.setHealth(health);
    status.setCreationDatetime(now());
    return status;
  }
}
