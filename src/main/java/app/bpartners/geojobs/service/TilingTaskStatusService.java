package app.bpartners.geojobs.service;

import app.bpartners.geojobs.model.exception.NotFoundException;
import app.bpartners.geojobs.repository.TilingTaskRepository;
import app.bpartners.geojobs.repository.model.ZoneTilingTask;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TilingTaskStatusService {

  private final TilingTaskRepository repository;
  private final ZoneTilingJobService zoneTilingJobService;
  private final TaskStatusService<ZoneTilingTask> taskStatusService;

  @Transactional(rollbackFor = IllegalArgumentException.class)
  public ZoneTilingTask pending(ZoneTilingTask task) {
    return taskStatusService.pending(task, this::update);
  }

  @Transactional(rollbackFor = IllegalArgumentException.class)
  public ZoneTilingTask process(ZoneTilingTask task) {
    return taskStatusService.process(task, this::update);
  }

  @Transactional(rollbackFor = IllegalArgumentException.class)
  public ZoneTilingTask succeed(ZoneTilingTask task) {
    return taskStatusService.succeed(task, this::update);
  }

  @Transactional(rollbackFor = IllegalArgumentException.class)
  public ZoneTilingTask fail(ZoneTilingTask task) {
    return taskStatusService.fail(task, this::update);
  }

  private ZoneTilingTask update(ZoneTilingTask zoneTilingTask) {
    if (!repository.existsById(zoneTilingTask.getId())) {
      throw new NotFoundException("ZoneTilingTask.Id = " + zoneTilingTask.getId() + " not found");
    }
    var updated = repository.save(zoneTilingTask);
    zoneTilingJobService.refreshStatus(zoneTilingTask.getJobId());

    return updated;
  }
}
