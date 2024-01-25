package app.bpartners.geojobs.service.event;

import app.bpartners.geojobs.endpoint.event.gen.ZoneDetectionTaskCreated;
import app.bpartners.geojobs.model.exception.ApiException;
import app.bpartners.geojobs.repository.DetectedTileRepository;
import app.bpartners.geojobs.repository.model.DetectedTile;
import app.bpartners.geojobs.service.ZoneDetectionTaskStatusService;
import app.bpartners.geojobs.service.geo.TilesDetectionApi;
import app.bpartners.geojobs.service.geo.response.DetectionResponse;
import app.bpartners.geojobs.service.mapper.DetectionMapper;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
      DetectedTile detectedTile = DetectionMapper.toDetectedTile(response, task.getTile());
      detectedTileRepository.save(detectedTile);
      zoneDetectionTaskStatusService.succeed(task);
    } catch (RuntimeException e) {
      zoneDetectionTaskStatusService.fail(task);
      throw new ApiException(ApiException.ExceptionType.SERVER_EXCEPTION, e);
    }
  }
}
