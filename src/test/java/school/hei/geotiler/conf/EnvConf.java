package school.hei.geotiler.conf;

import org.springframework.test.context.DynamicPropertyRegistry;

public class EnvConf {

  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add(
        "tiles.downloader.api.url",
        () -> "https://hds4hpeacrrder7h32qy4lmy4a0wrewx.lambda-url.eu-west-3.on.aws");
  }
}
