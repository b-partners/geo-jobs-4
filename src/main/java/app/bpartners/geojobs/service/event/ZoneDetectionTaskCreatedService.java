package app.bpartners.geojobs.service.event;

import app.bpartners.geojobs.endpoint.event.gen.ZoneDetectionTaskCreated;
import app.bpartners.geojobs.model.exception.ApiException;
import app.bpartners.geojobs.repository.DetectedTileRepository;
import app.bpartners.geojobs.repository.model.geo.detection.DetectedTile;
import app.bpartners.geojobs.service.geo.detection.DetectionResponse;
import app.bpartners.geojobs.service.geo.detection.DetectionTaskStatusService;
import app.bpartners.geojobs.service.geo.detection.TilesDetectionApi;
import app.bpartners.geojobs.service.mapper.DetectionMapper;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ZoneDetectionTaskCreatedService implements Consumer<ZoneDetectionTaskCreated> {
  private final TilesDetectionApi tilesDetectionApi;
  private final DetectionTaskStatusService detectionTaskStatusService;
  private final DetectedTileRepository detectedTileRepository;

  @Override
  public void accept(ZoneDetectionTaskCreated zoneDetectionTaskCreated) {
    var task = zoneDetectionTaskCreated.getTask();
    detectionTaskStatusService.process(task);
    try {
      DetectionResponse response = tilesDetectionApi.detect(task);
      DetectedTile detectedTile = DetectionMapper.toDetectedTile(response, task.getTile());
      detectedTileRepository.save(detectedTile);
      detectionTaskStatusService.succeed(task);
    } catch (RuntimeException e) {
      detectionTaskStatusService.fail(task);
      throw new ApiException(ApiException.ExceptionType.SERVER_EXCEPTION, e);
    }
  }
}
