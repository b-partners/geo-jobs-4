package app.bpartners.geojobs.endpoint.rest.controller;

import app.bpartners.geojobs.endpoint.rest.controller.mapper.ZoneDetectionJobMapper;
import app.bpartners.geojobs.endpoint.rest.model.DetectableObjectType;
import app.bpartners.geojobs.endpoint.rest.model.ZoneDetectionJob;
import app.bpartners.geojobs.service.ZoneDetectionJobService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class ZoneDetectionController {
  private final ZoneDetectionJobService service;
  private final ZoneDetectionJobMapper mapper;

  @PostMapping("/detectionJobs/{id}/process")
  public List<ZoneDetectionJob> processZDJ(
      @PathVariable("id") String jobId,
      @RequestBody List<DetectableObjectType> objectTypesToDetect) {

    List<app.bpartners.geojobs.repository.model.ZoneDetectionJob> processedZDJ =
        service.process(jobId);

    return processedZDJ.stream().map(job -> mapper.toRest(job, objectTypesToDetect)).toList();
  }
}
