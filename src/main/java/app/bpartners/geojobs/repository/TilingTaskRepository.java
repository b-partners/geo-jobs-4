package app.bpartners.geojobs.repository;

import app.bpartners.geojobs.repository.model.geo.tiling.TilingTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TilingTaskRepository extends JpaRepository<TilingTask, String> {}
