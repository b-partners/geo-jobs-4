package school.hei.geojobs.endpoint.rest.controller.mapper;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.geojobs.endpoint.rest.model.ZoneDetectionJob;
import school.hei.geojobs.repository.model.DetectionJobStatus;

@Component
@AllArgsConstructor
public class ZoneDetectionJobMapper {
  private final StatusMapper<DetectionJobStatus> statusMapper;

  public ZoneDetectionJob toRest(
      school.hei.geojobs.repository.model.ZoneDetectionJob domain,
      List<school.hei.geojobs.endpoint.rest.model.DetectableObjectType> objectTypesToDetect) {
    return new ZoneDetectionJob()
        .id(domain.getId())
        .zoneName(domain.getZoneName())
        .emailReceiver(domain.getEmailReceiver())
        .zoneTilingJobId(domain.getZoneTilingJob().getId())
        .objectTypesToDetect(objectTypesToDetect)
        .creationDatetime(domain.getSubmissionInstant())
        .status(statusMapper.statusConverter(domain.getStatus()));
  }
}
