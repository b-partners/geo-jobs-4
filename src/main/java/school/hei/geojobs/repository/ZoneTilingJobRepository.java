package school.hei.geojobs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.geojobs.repository.model.ZoneTilingJob;

public interface ZoneTilingJobRepository extends JpaRepository<ZoneTilingJob, String> {}
