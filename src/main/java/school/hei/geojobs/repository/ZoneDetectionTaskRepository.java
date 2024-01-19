package school.hei.geojobs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.geojobs.repository.model.ZoneDetectionTask;

public interface ZoneDetectionTaskRepository extends JpaRepository<ZoneDetectionTask, String> {}
