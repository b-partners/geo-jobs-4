package school.hei.geojobs.endpoint.rest.controller.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.geojobs.endpoint.rest.model.Status;

@Component
@AllArgsConstructor
public class StatusMapper<T extends school.hei.geojobs.repository.model.Status> {

  public static Status.ProgressionEnum toProgressionEnum(
      school.hei.geojobs.repository.model.Status.ProgressionStatus domain) {
    return switch (domain) {
      case PENDING -> Status.ProgressionEnum.PENDING;
      case PROCESSING -> Status.ProgressionEnum.PROCESSING;
      case FINISHED -> Status.ProgressionEnum.FINISHED;
    };
  }

  public static Status.HealthEnum toHealthStatus(
      school.hei.geojobs.repository.model.Status.HealthStatus domain) {
    return switch (domain) {
      case SUCCEEDED -> Status.HealthEnum.SUCCEEDED;
      case FAILED -> Status.HealthEnum.FAILED;
      case UNKNOWN -> Status.HealthEnum.UNKNOWN;
    };
  }

  public Status statusConverter(T tilingJobStatus) {
    return new Status()
        .progression(toProgressionEnum(tilingJobStatus.getProgression()))
        .health(toHealthStatus(tilingJobStatus.getHealth()))
        .creationDatetime(tilingJobStatus.getCreationDatetime());
  }
}
