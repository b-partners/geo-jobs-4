package school.hei.geotiler.service;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.hei.geotiler.repository.model.Status.HealthStatus.UNKNOWN;
import static school.hei.geotiler.repository.model.Status.ProgressionStatus.PENDING;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.geotiler.conf.FacadeIT;
import school.hei.geotiler.endpoint.event.EventProducer;
import school.hei.geotiler.repository.ZoneDetectionJobRepository;
import school.hei.geotiler.repository.model.DetectionJobStatus;
import school.hei.geotiler.repository.model.DetectionTaskStatus;
import school.hei.geotiler.repository.model.Tile;
import school.hei.geotiler.repository.model.ZoneDetectionJob;
import school.hei.geotiler.repository.model.ZoneDetectionTask;

class ZoneDetectionJobServiceIT extends FacadeIT {
  @Autowired ZoneDetectionJobService service;
  @MockBean ZoneDetectionJobRepository repository;
  @MockBean EventProducer eventProducer;

  public ZoneDetectionJob zoneDetectionJob() {
    return ZoneDetectionJob.builder()
        .id(randomUUID().toString())
        .statusHistory(
            new ArrayList<>() {
              {
                add(
                    DetectionJobStatus.builder()
                        .id(randomUUID().toString())
                        .jobId(randomUUID().toString())
                        .progression(PENDING)
                        .health(UNKNOWN)
                        .build());
              }
            })
        .tasks(
            List.of(
                ZoneDetectionTask.builder()
                    .id(String.valueOf(randomUUID()))
                    .jobId(String.valueOf(randomUUID()))
                    .submissionInstant(Instant.now())
                    .tile(
                        Tile.builder()
                            .id(String.valueOf(randomUUID()))
                            .bucketPath(String.valueOf(randomUUID()))
                            .build())
                    .statusHistory(
                        new ArrayList<>() {
                          {
                            add(
                                DetectionTaskStatus.builder()
                                    .id(randomUUID().toString())
                                    .progression(PENDING)
                                    .health(UNKNOWN)
                                    .build());
                          }
                        })
                    .build()))
        .build();
  }

  @Test
  void process_zdj() {
    when(repository.findById(any())).thenReturn(Optional.of(zoneDetectionJob()));
    when(repository.save(any())).thenReturn(zoneDetectionJob());

    List<ZoneDetectionJob> actual = service.process(zoneDetectionJob().getId());
    var actualJob = actual.get(0);

    assertEquals(1, actual.size());
    assertNotNull(actualJob.getId());

    verify(eventProducer, only()).accept(any());
  }
}
