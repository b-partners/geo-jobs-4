package app.bpartners.geojobs.service;

import app.bpartners.geojobs.endpoint.event.EventProducer;
import app.bpartners.geojobs.endpoint.event.gen.ZoneDetectionJobStatusChanged;
import app.bpartners.geojobs.endpoint.event.gen.ZoneDetectionTaskCreated;
import app.bpartners.geojobs.repository.model.geo.detection.DetectionTask;
import app.bpartners.geojobs.repository.model.geo.detection.ZoneDetectionJob;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ZoneDetectionJobService extends ZoneJobService<DetectionTask, ZoneDetectionJob> {

  public ZoneDetectionJobService(
      JpaRepository<ZoneDetectionJob, String> repository, EventProducer eventProducer) {
    super(repository, eventProducer);
  }

  public List<ZoneDetectionJob> fireTasks(String jobId) {
    var job = findById(jobId);
    job.getTasks()
        .forEach(task -> eventProducer.accept(List.of(new ZoneDetectionTaskCreated(task))));
    return List.of(job);
  }

  public ZoneDetectionJob refreshStatus(String jobId) {
    var oldJob = findById(jobId);
    var refreshed = super.refreshStatus(oldJob);

    eventProducer.accept(
        List.of(ZoneDetectionJobStatusChanged.builder().oldJob(oldJob).newJob(refreshed).build()));
    return refreshed;
  }
}
