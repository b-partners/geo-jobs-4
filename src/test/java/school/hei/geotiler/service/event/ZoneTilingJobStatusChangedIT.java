package school.hei.geotiler.service.event;

import static java.time.Instant.now;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static school.hei.geotiler.repository.model.Status.HealthStatus.SUCCEEDED;
import static school.hei.geotiler.repository.model.Status.ProgressionStatus.FINISHED;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.geotiler.conf.FacadeIT;
import school.hei.geotiler.endpoint.event.gen.ZoneTilingJobStatusChanged;
import school.hei.geotiler.repository.model.JobStatus;
import school.hei.geotiler.repository.model.TaskStatus;
import school.hei.geotiler.repository.model.ZoneTilingJob;
import school.hei.geotiler.repository.model.ZoneTilingTask;
import school.hei.geotiler.repository.model.geo.Parcel;
import school.hei.geotiler.service.EmailService;

public class ZoneTilingJobStatusChangedIT extends FacadeIT {
  @Autowired private ZoneTilingJobStatusChangedService subject;
  @MockBean private EmailService emailService;

  @Test
  void send_email_ok() {
    String jobId = randomUUID().toString();
    String taskId = randomUUID().toString();
    ZoneTilingJob toCreate =
        ZoneTilingJob.builder()
            .id(jobId)
            .zoneName("mock")
            .emailReceiver("mock@gmail.com")
            .tasks(
                List.of(
                    ZoneTilingTask.builder()
                        .id(taskId)
                        .jobId(jobId)
                        .submissionInstant(now())
                        .parcel(Parcel.builder().id(randomUUID().toString()).build())
                        .statusHistory(
                            List.of(
                                TaskStatus.builder()
                                    .id(randomUUID().toString())
                                    .progression(FINISHED)
                                    .health(SUCCEEDED)
                                    .taskId(taskId)
                                    .creationDatetime(now())
                                    .build()))
                        .build()))
            .statusHistory(
                List.of(
                    JobStatus.builder()
                        .id(randomUUID().toString())
                        .jobId(jobId)
                        .progression(FINISHED)
                        .health(SUCCEEDED)
                        .build()))
            .build();
    ZoneTilingJobStatusChanged zoneTilingJobStatusChanged =
        ZoneTilingJobStatusChanged.builder().newJob(toCreate).build();

    subject.accept(zoneTilingJobStatusChanged);

    verify(emailService, times(1)).sendEmail(any());
  }
}
