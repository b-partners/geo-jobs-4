package school.hei.geojobs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.geojobs.repository.model.ZoneTilingTask;

public interface ZoneTilingTaskRepository extends JpaRepository<ZoneTilingTask, String> {}
