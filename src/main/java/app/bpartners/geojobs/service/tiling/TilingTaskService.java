package app.bpartners.geojobs.service.tiling;

import app.bpartners.geojobs.repository.TilingTaskRepository;
import app.bpartners.geojobs.repository.model.tiling.TilingTask;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TilingTaskService {
  private final TilingTaskRepository repository;

  @PersistenceContext EntityManager em;

  @Transactional
  public TilingTask save(TilingTask task) {
    var saved = repository.save(task);
    em.flush();
    return saved;
  }
}
