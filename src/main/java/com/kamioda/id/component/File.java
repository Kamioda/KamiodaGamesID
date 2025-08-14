package com.kamioda.id.component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class File {
    public static String readAllText(String path) throws IOException {
        return Files.lines(Paths.get(path), StandardCharsets.UTF_8)
            .collect(Collectors.joining(System.getProperty("line.separator")));
    }
}
