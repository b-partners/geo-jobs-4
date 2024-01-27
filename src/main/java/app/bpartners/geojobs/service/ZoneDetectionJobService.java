package app.bpartners.geojobs.service;

import static app.bpartners.geojobs.repository.model.JobType.DETECTION;

import app.bpartners.geojobs.endpoint.event.gen.ZoneDetectionJobStatusChanged;
import app.bpartners.geojobs.endpoint.event.gen.ZoneDetectionTaskCreated;
import app.bpartners.geojobs.repository.ZoneDetectionJobRepository;
import app.bpartners.geojobs.repository.model.ZoneDetectionJob;
import app.bpartners.geojobs.repository.model.ZoneDetectionTask;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ZoneDetectionJobService {
  private final ZoneDetectionJobRepository repository;
  private final ZoneJobService<ZoneDetectionTask, ZoneDetectionJob> zoneJobService;

  public List<ZoneDetectionJob> process(String jobId) {
    var optionalZDJ = zoneJobService.findById(jobId, repository);
    var toProcess = zoneJobService.process(optionalZDJ, DETECTION, repository);
    toProcess
        .getTasks()
        .forEach(
            task ->
                zoneJobService
                    .getEventProducer()
                    .accept(List.of(new ZoneDetectionTaskCreated(task))));
    return List.of(optionalZDJ);
  }

  public ZoneDetectionJob refreshStatus(String jobId) {
    var oldJob = zoneJobService.findById(jobId, repository);
    var refreshed = zoneJobService.updateStatus(oldJob, oldJob.getStatus(), repository);

    zoneJobService
        .getEventProducer()
        .accept(
            List.of(
                ZoneDetectionJobStatusChanged.builder().oldJob(oldJob).newJob(refreshed).build()));
    return refreshed;
  }

  public ZoneDetectionJob findById(String id) {
    return zoneJobService.findById(id, repository);
  }
}
