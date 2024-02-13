package app.bpartners.geojobs.service.event;

import static app.bpartners.annotator.endpoint.rest.model.JobStatus.TO_REVIEW;
import static app.bpartners.geojobs.model.exception.ApiException.ExceptionType.SERVER_EXCEPTION;
import static java.util.UUID.randomUUID;

import app.bpartners.annotator.endpoint.rest.api.AnnotatedJobsApi;
import app.bpartners.annotator.endpoint.rest.model.AnnotatedTask;
import app.bpartners.annotator.endpoint.rest.model.Annotation;
import app.bpartners.annotator.endpoint.rest.model.AnnotationBatch;
import app.bpartners.annotator.endpoint.rest.model.CrupdateAnnotatedJob;
import app.bpartners.annotator.endpoint.rest.model.Label;
import app.bpartners.annotator.endpoint.rest.model.Point;
import app.bpartners.annotator.endpoint.rest.model.Polygon;
import app.bpartners.geojobs.endpoint.event.gen.InDoubtTilesDetected;
import app.bpartners.geojobs.model.exception.ApiException;
import app.bpartners.geojobs.repository.DetectedTileRepository;
import app.bpartners.geojobs.repository.model.detection.DetectedObject;
import app.bpartners.geojobs.repository.model.detection.DetectedTile;
import app.bpartners.geojobs.service.annotator.AnnotatorApiConf;
import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;

@Service
public class InDoubtTileDetectedService implements Consumer<InDoubtTilesDetected> {
  private final DetectedTileRepository detectedTileRepository;
  private final AnnotatedJobsApi annotatedJobsApi;

  public InDoubtTileDetectedService(
      DetectedTileRepository detectedTileRepository, AnnotatorApiConf annotatorApiConf) {
    this.detectedTileRepository = detectedTileRepository;
    this.annotatedJobsApi = new AnnotatedJobsApi(annotatorApiConf.newApiClientWithApiKey());
  }

  @Override
  public void accept(InDoubtTilesDetected event) {
    String jobId = event.getJobId();
    Instant updatedAt = Instant.now();
    List<DetectedTile> detectedTiles = detectedTileRepository.findAllByJobId(jobId);
    List<DetectedTile> detectedInDoubtTiles =
        detectedTiles.stream()
            .filter(
                detectedTile ->
                    detectedTile.getDetectedObjects().stream().anyMatch(DetectedObject::isInDoubt))
            .toList();
    // /!\ TODO: complete TODO by converting detected in-doubt tiles

    Label label = new Label().id(randomUUID().toString()).name("TODO").color("TODO");

    String crupdateJobId = randomUUID().toString();
    try {
      annotatedJobsApi.crupdateAnnotatedJob(
          crupdateJobId,
          new CrupdateAnnotatedJob()
              .id(crupdateJobId)
              .name("TODO")
              .bucketName("TODO")
              .folderPath("TODO")
              .labels(List.of(label))
              .ownerEmail(null)
              .status(TO_REVIEW)
              .annotatedTasks(
                  List.of(
                      new AnnotatedTask()
                          .id(randomUUID().toString())
                          .annotatorId("TODO")
                          .filename("TODO")
                          .annotationBatch(
                              new AnnotationBatch()
                                  .id(randomUUID().toString())
                                  .creationDatetime(updatedAt)
                                  .annotations(
                                      List.of(
                                          new Annotation()
                                              .id(randomUUID().toString())
                                              .userId("TODO")
                                              .taskId("TODO")
                                              .label(label)
                                              .polygon(
                                                  new Polygon()
                                                      .points(
                                                          List.of(
                                                              new Point()
                                                                  .y(0.0) // TODO
                                                                  .y(0.0) // TODO
                                                              ))))))))
              .teamId("TODO"));
    } catch (app.bpartners.annotator.endpoint.rest.client.ApiException e) {
      throw new ApiException(SERVER_EXCEPTION, e);
    }
  }
}
