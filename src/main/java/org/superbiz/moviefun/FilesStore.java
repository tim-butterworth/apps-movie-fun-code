package org.superbiz.moviefun;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;

public class FilesStore implements BlobStore {
    @Override
    public void put(Blob blob) throws IOException {
        saveUploadToFile(getCoverFile(blob.name), blob.inputStream);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        try {
            Path existingCoverPath = getExistingCoverPath(name);
            return Optional.of(new Blob(name, new FileInputStream(existingCoverPath.toFile()), new Tika().detect(existingCoverPath)));
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }

    private void saveUploadToFile(File targetFile, InputStream inputStream) throws IOException {
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            IOUtils.copy(inputStream, outputStream);
        }
    }

    private File getCoverFile(String coverFileName) {
        return new File(coverFileName);
    }

    private Path getExistingCoverPath(String coverFileName) throws URISyntaxException {
        File coverFile = getCoverFile(coverFileName);
        Path coverFilePath;

        if (coverFile.exists()) {
            coverFilePath = coverFile.toPath();
        } else {
            coverFilePath = Paths.get(getSystemResource("default-cover.jpg").toURI());
        }

        return coverFilePath;
    }
}
