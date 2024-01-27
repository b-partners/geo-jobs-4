package app.bpartners.geojobs.repository;

import app.bpartners.geojobs.repository.model.ZoneDetectionTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetectionTaskRepository extends JpaRepository<ZoneDetectionTask, String> {}
