package app.bpartners.geojobs.service.tiling.downloader;

import static java.nio.file.Files.createTempDirectory;
import static java.util.UUID.randomUUID;

import app.bpartners.geojobs.repository.model.ParcelContent;
import app.bpartners.geojobs.service.tiling.TilesDownloader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "tiles.downloader.mock", havingValue = "true")
public class MockedTilesDownloader implements TilesDownloader {

  @SneakyThrows
  @Override
  public File apply(ParcelContent parcelContent) {
    var rootDir = createTempDirectory("tiles").toFile();
    var zoomAndXDir = new File(rootDir.getAbsolutePath() + "/20/1");
    zoomAndXDir.mkdirs();
    var yFile = new File(zoomAndXDir.getAbsolutePath() + "/" + randomUUID());
    writeRandomContent(yFile);
    return rootDir;
  }

  private void writeRandomContent(File file) throws IOException {
    FileWriter writer = new FileWriter(file);
    var content = randomUUID().toString();
    writer.write(content);
    writer.close();
  }
}
