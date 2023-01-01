package com.github.pjfanning.json;

import com.github.pjfanning.json.util.SizeLimitInputStream;
import com.github.pjfanning.json.util.SizeLimitReader;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

public class StringBuildingLimiter {

    /**
     * Validates the total length of the input reader.
     * @param constraints to apply
     * @param input reader (that will only be closed if an exception is thrown)
     * @return the valid JSON as a <code>String</code>
     * @throws NullPointerException if input is <code>null</code>
     * @throws IllegalStateException if constraints fail
     * @throws IOException if I/O or JSON parse issues occur (or total size is too big)
     */
    public static String check(final StreamReadConstraints constraints, final Reader input) throws IOException {
        if (input == null) {
            throw new NullPointerException("null input");
        }
        final SizeLimitReader wrappedReader = new SizeLimitReader(input, constraints.getMaxTotalLength());
        try {
            final String text = IOUtils.toString(wrappedReader);
            SizeLimiter.check(constraints, text);
            return text;
        } catch (Exception e) {
            wrappedReader.close();
            throw e;
        }
    }

    /**
     * Validates the total length of the <code>InputStream</code>.
     * @param constraints to apply
     * @param input stream (that will only be closed if an exception is thrown)
     * @param charset {@link Charset} to apply
     * @return the valid JSON as a <code>String</code>
     * @throws NullPointerException if input is <code>null</code>
     * @throws IllegalStateException if constraints fail
     * @throws IOException if I/O or JSON parse issues occur (or total size is too big)
     */
    public static String check(final StreamReadConstraints constraints,
                               final InputStream input,
                               final Charset charset) throws IOException{
        if (input == null) {
            throw new NullPointerException("null input");
        }
        final InputStream wrappedStream = new SizeLimitInputStream(input, constraints.getMaxTotalLength());
        try {
            final String text = IOUtils.toString(wrappedStream, charset);
            SizeLimiter.check(constraints, text);
            return text;
        } catch (Exception e) {
            wrappedStream.close();
            throw e;
        }
    }
}
