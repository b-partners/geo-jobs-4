package school.hei.geojobs.file;

import school.hei.geojobs.PojaGenerated;

@PojaGenerated
public record FileHash(FileHashAlgorithm algorithm, String value) {}
