package app.bpartners.geojobs.repository;

import app.bpartners.geojobs.repository.model.ZoneTilingTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TilingTaskRepository extends JpaRepository<ZoneTilingTask, String> {}
