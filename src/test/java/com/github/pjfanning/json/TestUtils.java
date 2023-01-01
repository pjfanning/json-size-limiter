package com.github.pjfanning.json;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestUtils {
    public static String readResource(final String fileName) throws IOException {
        try (final InputStream inputStream = TestUtils.class.getResourceAsStream(fileName)) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }
}
