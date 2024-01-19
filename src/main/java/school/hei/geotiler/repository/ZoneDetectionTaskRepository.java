package school.hei.geotiler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.geotiler.repository.model.ZoneDetectionTask;

public interface ZoneDetectionTaskRepository extends JpaRepository<ZoneDetectionTask, String> {}
