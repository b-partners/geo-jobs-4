package app.bpartners.geojobs.repository;

import app.bpartners.geojobs.job.repository.TaskRepository;
import app.bpartners.geojobs.repository.model.tiling.TilingTask;
import org.springframework.stereotype.Repository;

@Repository
public interface TilingTaskRepository extends TaskRepository<TilingTask> {}
