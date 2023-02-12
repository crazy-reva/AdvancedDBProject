package com.TwoPhaseMultiwayMergeSort.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CopyDirectoryUtility {

    public static void copyDirectory(Path source, Path target) throws IOException {
        Files.walk(source)
                .forEach(sourcePath -> {
                    Path targetPath = target.resolve(source.relativize(sourcePath));
                    try {
                        if (Files.isDirectory(sourcePath)) {
                            Files.createDirectories(targetPath);
                        } else {
                            Files.copy(sourcePath, targetPath);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
