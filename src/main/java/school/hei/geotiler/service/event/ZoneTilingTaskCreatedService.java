package school.hei.geotiler.service.event;

import static school.hei.geotiler.file.FileUnzipper.getFilename;
import static school.hei.geotiler.file.FileUnzipper.getFolderPath;
import static school.hei.geotiler.file.FileUnzipper.stripExtension;
import static school.hei.geotiler.model.exception.ApiException.ExceptionType.SERVER_EXCEPTION;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.geotiler.endpoint.event.gen.ZoneTilingTaskCreated;
import school.hei.geotiler.endpoint.rest.model.TileCoordinates;
import school.hei.geotiler.file.BucketComponent;
import school.hei.geotiler.file.BucketConf;
import school.hei.geotiler.file.FileUnzipper;
import school.hei.geotiler.file.FileWriter;
import school.hei.geotiler.model.exception.ApiException;
import school.hei.geotiler.repository.model.Tile;
import school.hei.geotiler.repository.model.ZoneTilingTask;
import school.hei.geotiler.repository.model.geo.Parcel;
import school.hei.geotiler.service.ZoneTilingTaskStatusService;
import school.hei.geotiler.service.api.TilesDownloaderApi;

@Service
@AllArgsConstructor
public class ZoneTilingTaskCreatedService implements Consumer<ZoneTilingTaskCreated> {
  private final TilesDownloaderApi tilesDownloaderApi;
  private final FileWriter fileWriter;
  private final FileUnzipper fileUnzipper;
  private final BucketComponent bucketComponent;
  private final ZoneTilingTaskStatusService zoneTilingTaskStatusService;
  private final BucketConf bucketConf;

  @Override
  public void accept(ZoneTilingTaskCreated zoneTilingTaskCreated) {
    ZoneTilingTask task = zoneTilingTaskCreated.getTask();
    zoneTilingTaskStatusService.process(task);
    File downloadedTiles =
        fileWriter.apply(
            tilesDownloaderApi.downloadTiles(zoneTilingTaskCreated.getTask().getParcel()), null);
    try {
      File unzippedPathFile = unzip(downloadedTiles, zoneTilingTaskCreated);
      String bucketKey = unzippedPathFile.getName();
      bucketComponent.upload(unzippedPathFile, bucketKey);
      setParcelTile(new ZipFile(downloadedTiles), task.getParcel(), bucketKey);
      zoneTilingTaskStatusService.succeed(task);
    } catch (IOException e) {
      zoneTilingTaskStatusService.fail(task);
      throw new ApiException(SERVER_EXCEPTION, e);
    }
  }

  private File unzip(File downloadedTiles, ZoneTilingTaskCreated zoneTilingTaskCreated)
      throws IOException {
    ZipFile asZipFile = new ZipFile(downloadedTiles);
    String layer = zoneTilingTaskCreated.getTask().getParcel().getGeoServerParameter().getLayers();
    Path unzippedPath = fileUnzipper.apply(asZipFile, layer);
    return unzippedPath.toFile();
  }

  public void setParcelTile(ZipFile zipFile, Parcel parcel, String bucketKey) throws IOException {
    Enumeration<? extends ZipEntry> entries = zipFile.entries();
    List<Tile> tiles = new ArrayList<>();

    while (entries.hasMoreElements()) {
      ZipEntry entry = entries.nextElement();
      String entryPath = entry.getName();

      if (!entry.isDirectory()) {
        String entryParentPath = getFolderPath(entry);
        String[] dir = entryParentPath.split("/");
        String entryFilename = getFilename(entry);
        String extensionlessEntryFilename = stripExtension(entryFilename);
        String bucketName = bucketConf.getBucketName();
        tiles.add(
            Tile.builder()
                .id(UUID.randomUUID().toString())
                .creationDatetime(Instant.now().toString())
                .coordinates(
                    new TileCoordinates()
                        .x(Integer.valueOf(dir[1]))
                        .y(Integer.valueOf(extensionlessEntryFilename))
                        .z(Integer.valueOf(dir[0])))
                .bucketPath(bucketName + "/" + bucketKey + entryPath)
                .build());
      }
    }
    parcel.setTiles(tiles);
  }
}
