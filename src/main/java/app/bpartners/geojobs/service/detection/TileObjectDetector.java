package app.bpartners.geojobs.service.detection;

import app.bpartners.geojobs.repository.model.TileTask;
import app.bpartners.geojobs.repository.model.detection.DetectableType;
import java.util.List;
import java.util.function.BiFunction;

public interface TileObjectDetector
    extends BiFunction<TileTask, List<DetectableType>, DetectionResponse> {}
