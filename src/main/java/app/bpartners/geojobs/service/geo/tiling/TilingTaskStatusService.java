package app.bpartners.geojobs.service.geo.tiling;

import app.bpartners.geojobs.model.exception.NotFoundException;
import app.bpartners.geojobs.repository.TilingTaskRepository;
import app.bpartners.geojobs.repository.model.geo.tiling.TilingTask;
import app.bpartners.geojobs.service.TaskStatusService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TilingTaskStatusService {

  private final TilingTaskRepository repository;
  private final ZoneTilingJobService zoneTilingJobService;
  private final TaskStatusService<TilingTask> taskStatusService;

  @Transactional(rollbackFor = IllegalArgumentException.class)
  public TilingTask pending(TilingTask task) {
    return taskStatusService.pending(task, this::update);
  }

  @Transactional(rollbackFor = IllegalArgumentException.class)
  public TilingTask process(TilingTask task) {
    return taskStatusService.process(task, this::update);
  }

  @Transactional(rollbackFor = IllegalArgumentException.class)
  public TilingTask succeed(TilingTask task) {
    return taskStatusService.succeed(task, this::update);
  }

  @Transactional(rollbackFor = IllegalArgumentException.class)
  public TilingTask fail(TilingTask task) {
    return taskStatusService.fail(task, this::update);
  }

  private TilingTask update(TilingTask tilingTask) {
    if (!repository.existsById(tilingTask.getId())) {
      throw new NotFoundException("tilingTask.Id = " + tilingTask.getId() + " not found");
    }
    var updated = repository.save(tilingTask);
    zoneTilingJobService.refreshStatus(tilingTask.getJobId());

    return updated;
  }
}
