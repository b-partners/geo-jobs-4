package school.hei.geotiler.endpoint.rest.controller.mapper;

import static school.hei.geotiler.endpoint.rest.model.Status.HealthEnum.FAILED;
import static school.hei.geotiler.endpoint.rest.model.Status.HealthEnum.SUCCEEDED;
import static school.hei.geotiler.endpoint.rest.model.Status.HealthEnum.UNKNOWN;
import static school.hei.geotiler.endpoint.rest.model.Status.ProgressionEnum.FINISHED;
import static school.hei.geotiler.endpoint.rest.model.Status.ProgressionEnum.PENDING;
import static school.hei.geotiler.endpoint.rest.model.Status.ProgressionEnum.PROCESSING;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.geotiler.endpoint.rest.model.Status;
import school.hei.geotiler.repository.model.JobStatus;

@Component
@AllArgsConstructor
public class StatusMapper {

  public Status statusConverter(JobStatus jobStatus) {
    return new Status()
        .progression(toProgressionEnum(jobStatus.getProgression()))
        .health(toHealthStatus(jobStatus.getHealth()))
        .creationDatetime(jobStatus.getCreationDatetime());
  }

  public static Status.ProgressionEnum toProgressionEnum(
      school.hei.geotiler.repository.model.Status.ProgressionStatus status) {
    if (status.toString().equals("FINISHED")) {
      return FINISHED;
    }
    if (status.toString().equals("PENDING")) {
      return PROCESSING;
    }
    return PENDING;
  }

  public static Status.HealthEnum toHealthStatus(
      school.hei.geotiler.repository.model.Status.HealthStatus healthEnum) {
    if (healthEnum.toString().equals("SUCCEEDED")) {
      return SUCCEEDED;
    }
    if (healthEnum.toString().equals("UNKNOWN")) {
      return UNKNOWN;
    }
    return FAILED;
  }
}
