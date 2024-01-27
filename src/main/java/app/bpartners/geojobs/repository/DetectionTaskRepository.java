package app.bpartners.geojobs.repository;

import app.bpartners.geojobs.repository.model.geo.detection.DetectionTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetectionTaskRepository extends JpaRepository<DetectionTask, String> {}
