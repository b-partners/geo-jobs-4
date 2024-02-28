package app.bpartners.geojobs.repository.conf;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@Configuration
public class HikariConf {
  private final HikariDataSource hikariDataSource;

  @PostConstruct
  private void setHikariDatasource() {
    hikariDataSource.setMaximumPoolSize(1);
    hikariDataSource.setLeakDetectionThreshold(3_000L);
    /* Close db connection 1s after inactivity.
    Indeed, computing resources are ephemeral.
    This applies to frontal computers and workers.
    However, it is particularly true for workers
    as Spring Boot is re-run at after each non-empty poll.
    The problem is that connections might remain open while
    corresponding computing resources are no longer accessible to us. */
    hikariDataSource.setMaxLifetime(3_000L);
  }
}
