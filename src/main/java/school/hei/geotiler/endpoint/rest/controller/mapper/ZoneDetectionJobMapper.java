package school.hei.geotiler.endpoint.rest.controller.mapper;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.geotiler.endpoint.rest.model.ZoneDetectionJob;
import school.hei.geotiler.repository.model.DetectionJobStatus;

@Component
@AllArgsConstructor
public class ZoneDetectionJobMapper {
  private final StatusMapper<DetectionJobStatus> statusMapper;

  public ZoneDetectionJob toRest(
      school.hei.geotiler.repository.model.ZoneDetectionJob domain,
      List<school.hei.geotiler.endpoint.rest.model.DetectableObjectType> objectTypesToDetect) {
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
