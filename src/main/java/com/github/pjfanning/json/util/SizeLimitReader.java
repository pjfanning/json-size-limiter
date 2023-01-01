package com.github.pjfanning.json.util;


import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Reader that only allows a certain number of chars to be read.
 *
 * Current implementation only tracks read chars, and ignores any mark or reset
 * calls.
 */
public class SizeLimitReader extends FilterReader {

    /** Maximum number of chars to read. */
    private long limit;
    /** Number of chars already read. */
    private long read = 0L;

    /**
     * Construct a new SizeLimitReader.
     *
     * @param reader
     *            reader to limit.
     * @param limit
     *            maximum number of chars allowed to read.
     */
    public SizeLimitReader(final Reader reader, final long limit) {
        super(reader);
        this.limit = limit;
    }

    /**
     * Return number of chars read.
     * @return chars read
     */
    public long getRead() {
        return this.read;
    }

    /**
     * Read one char.
     */
    @Override
    public int read() throws IOException {
        int b = in.read();
        if (b != -1) {
            read++;
            checkLimit();
        }
        return b;
    }

    /**
     * Read into an array of chars.
     */
    @Override
    public int read(char[] c) throws IOException {
        return read(c, 0, c.length);
    }

    /**
     * Read into an array of chars.
     */
    @Override
    public int read(char[] c, int off, int len) throws IOException {
        final int total = in.read(c, off, len);
        if (total != -1) {
            read += total;
            checkLimit();
        }
        return total;
    }

    /**
     * Check how many chars have been read.
     *
     * @throws IOException
     *             if more chars than the limit allows have been read.
     */
    private void checkLimit() throws IOException {
        if (limit > 0 && read > limit) {
            throw new IOException("Read more than size limit (" + limit
                    + ") chars");
        }
    }

}
