package app.bpartners.geojobs.service.annotator;

import static java.util.UUID.randomUUID;

import app.bpartners.gen.annotator.endpoint.rest.model.Annotation;
import app.bpartners.gen.annotator.endpoint.rest.model.AnnotationBatch;
import app.bpartners.geojobs.repository.model.detection.DetectedObject;
import app.bpartners.geojobs.repository.model.detection.DetectedTile;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class AnnotationBatchExtractor {
  private final LabelExtractor labelExtractor;
  private final PolygonExtractor polygonExtractor;

  public AnnotationBatch apply(DetectedTile detectedTile, String annotatorId, String taskId) {
    AnnotationBatch annotations =
        new AnnotationBatch()
            .id(randomUUID().toString())
            .creationDatetime(Instant.now())
            .annotations(
                detectedTile.getDetectedObjects().stream()
                    .map(detectedObject -> extractAnnotation(detectedObject, annotatorId, taskId))
                    .toList());
    log.error(
        "[DEBUG] AnnotationBatchExtractor Annotations [{}]",
        annotations.getAnnotations().stream()
            .map(
                annotation ->
                    "Annotation(id="
                        + annotation.getId()
                        + ", label="
                        + annotation.getLabel()
                        + ", polygonPointsSize="
                        + annotation.getPolygon().getPoints().size()
                        + ")")
            .toList());
    return annotations;
  }

  private Annotation extractAnnotation(
      DetectedObject detectedObject, String annotatorId, String taskId) {
    return new Annotation()
        .id(randomUUID().toString())
        .userId(annotatorId)
        .taskId(taskId)
        .label(labelExtractor.apply(detectedObject.getDetectableObjectType()))
        .polygon(polygonExtractor.apply(detectedObject));
  }
}
