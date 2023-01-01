package com.github.pjfanning.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.github.pjfanning.json.util.SizeLimitInputStream;
import com.github.pjfanning.json.util.SizeLimitReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class SizeLimiter {

    /**
     * Validates the total length of the input string.
     * @param constraints to apply
     * @param input string
     * @throws NullPointerException if input is <code>null</code>
     * @throws IllegalStateException if constraints fail
     * @throws IOException if I/O or JSON parse issues occur (or total size is too big)
     */
    public static void check(final StreamReadConstraints constraints, final String input) throws IOException {
        if (input == null) {
            throw new NullPointerException("null input");
        }
        constraints.validateTotalLength(input.length());
        final JsonFactory jfactory = new JsonFactory();
        try (JsonParser jsonParser = jfactory.createParser(input)) {
            validate(constraints, jsonParser);
        }
    }

    /**
     * Validates the total length of the input reader.
     * @param constraints to apply
     * @param input reader (that will only be closed if an exception is thrown)
     * @throws NullPointerException if input is <code>null</code>
     * @throws IllegalStateException if constraints fail
     * @throws IOException if I/O or JSON parse issues occur (or total size is too big)
     */
    public static void check(final StreamReadConstraints constraints, final Reader input) throws IOException {
        if (input == null) {
            throw new NullPointerException("null input");
        }
        final SizeLimitReader wrappedReader = new SizeLimitReader(input, constraints.getMaxTotalLength());
        final JsonFactory jfactory = new JsonFactory();
        try (JsonParser jsonParser = jfactory.createParser(wrappedReader)) {
            validate(constraints, jsonParser);
        } catch (Exception e) {
            wrappedReader.close();
            throw e;
        }
    }

    /**
     * Validates the total length of the <code>InputStream</code>.
     * @param constraints to apply
     * @param input stream (that will only be closed if an exception is thrown)
     * @throws NullPointerException if input is <code>null</code>
     * @throws IllegalStateException if constraints fail
     * @throws IOException if I/O or JSON parse issues occur (or total size is too big)
     */
    public static void check(final StreamReadConstraints constraints, final InputStream input) throws IOException{
        if (input == null) {
            throw new NullPointerException("null input");
        }
        final JsonFactory jfactory = new JsonFactory();
        final InputStream wrappedStream = new SizeLimitInputStream(input, constraints.getMaxTotalLength());
        try (JsonParser jsonParser = jfactory.createParser(wrappedStream)) {
            validate(constraints, jsonParser);
        } catch (Exception e) {
            wrappedStream.close();
            throw e;
        }
    }

    private static void validate(final StreamReadConstraints constraints, final JsonParser jsonParser)
            throws IOException, IllegalStateException {
        int depth = 0;
        JsonToken jsonToken;
        for (;;) {
            jsonToken = jsonParser.nextToken();
            if (jsonToken == JsonToken.START_OBJECT) {
                depth++;
            } else if (jsonToken == JsonToken.END_OBJECT) {
                depth--;
                if (depth == 0) {
                    break;
                }
            } else if (jsonToken == JsonToken.VALUE_NUMBER_INT) {
                constraints.validateIntegerLength(jsonParser.getValueAsString().length());
            } else if (jsonToken == JsonToken.VALUE_NUMBER_FLOAT) {
                constraints.validateFPLength(jsonParser.getValueAsString().length());
            } else if (jsonToken == JsonToken.VALUE_STRING) {
                constraints.validateStringLength(jsonParser.getValueAsString().length());
            }
        }
    }
}
