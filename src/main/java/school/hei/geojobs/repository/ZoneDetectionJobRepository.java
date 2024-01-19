package school.hei.geojobs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.geojobs.repository.model.ZoneDetectionJob;

@Repository
public interface ZoneDetectionJobRepository extends JpaRepository<ZoneDetectionJob, String> {}
