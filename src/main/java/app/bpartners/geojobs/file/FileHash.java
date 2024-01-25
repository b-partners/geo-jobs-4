package app.bpartners.geojobs.file;

import app.bpartners.geojobs.PojaGenerated;

@PojaGenerated
public record FileHash(FileHashAlgorithm algorithm, String value) {}
