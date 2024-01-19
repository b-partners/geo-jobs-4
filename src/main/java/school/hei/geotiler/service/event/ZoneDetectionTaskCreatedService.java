package school.hei.geotiler.service.event;

import static school.hei.geotiler.model.exception.ApiException.ExceptionType.SERVER_EXCEPTION;
import static school.hei.geotiler.service.mapper.DetectionMapper.toDetectedTile;

import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.geotiler.endpoint.event.gen.ZoneDetectionTaskCreated;
import school.hei.geotiler.model.exception.ApiException;
import school.hei.geotiler.repository.DetectedTileRepository;
import school.hei.geotiler.repository.model.DetectedTile;
import school.hei.geotiler.service.ZoneDetectionTaskStatusService;
import school.hei.geotiler.service.geo.TilesDetectionApi;
import school.hei.geotiler.service.geo.response.DetectionResponse;

@Service
@AllArgsConstructor
public class ZoneDetectionTaskCreatedService implements Consumer<ZoneDetectionTaskCreated> {
  private final TilesDetectionApi tilesDetectionApi;
  private final ZoneDetectionTaskStatusService zoneDetectionTaskStatusService;
  private final DetectedTileRepository detectedTileRepository;

  @Override
  public void accept(ZoneDetectionTaskCreated zoneDetectionTaskCreated) {
    var task = zoneDetectionTaskCreated.getTask();
    zoneDetectionTaskStatusService.process(task);
    try {
      DetectionResponse response = tilesDetectionApi.detect(task);
      DetectedTile detectedTile = toDetectedTile(response, task.getTile());
      detectedTileRepository.save(detectedTile);
      zoneDetectionTaskStatusService.succeed(task);
    } catch (RuntimeException e) {
      zoneDetectionTaskStatusService.fail(task);
      throw new ApiException(SERVER_EXCEPTION, e);
    }
  }
}
