package app.bpartners.geojobs.conf;

import org.springframework.test.context.DynamicPropertyRegistry;

public class EnvConf {

  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add(
        "tiles.downloader.api.url",
        () -> "https://hds4hpeacrrder7h32qy4lmy4a0wrewx.lambda-url.eu-west-3.on.aws");
    registry.add(
        "tile.detection.api.url",
        () -> "https://parjrau5niz7tsnr5v6mqayjty0avzco.lambda-url.eu-west-3.on.aws");
  }
}
