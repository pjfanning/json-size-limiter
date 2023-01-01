package com.github.pjfanning.json;

/**
 * The constraints to use for streaming reads: used to guard against malicious
 * input by preventing processing of "too big" input constructs (values,
 * structures).
 *
 * Based on the equivalent class in jackson-core v2.15 but with extra features.
 */
public class StreamReadConstraints
    implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Default setting for maximum number length: see {@link Builder#maxNumberLength(int)} for details.
     */
    public static final int DEFAULT_MAX_NUM_LEN = 1000;

    /**
     * Default setting for maximum string length: see {@link Builder#maxStringLength(int)} for details.
     */
    public static final int DEFAULT_MAX_STRING_LEN = 1_000_000;

    /**
     * Default setting for maximum total length: see {@link Builder#maxTotalLength(int)} for details.
     */
    public static final int DEFAULT_MAX_TOTAL_LEN = 100_000_000;

    protected final int _maxTotalLen;
    protected final int _maxNumLen;
    protected final int _maxStringLen;

    private static final StreamReadConstraints DEFAULT =
        new StreamReadConstraints(DEFAULT_MAX_TOTAL_LEN,
                DEFAULT_MAX_NUM_LEN,
                DEFAULT_MAX_STRING_LEN);

    public static final class Builder {
        private int maxTotalLen;
        private int maxNumLen;
        private int maxStringLen;

        /**
         * Sets the maximum total length (in chars or bytes, depending on input context).
         * The default is 100,000,000.
         *
         * @param maxTotalLen the maximum total length (in chars or bytes, depending on input context)
         *
         * @return this builder
         * @throws IllegalArgumentException if the maxTotalLen is set to a negative value
         */
        public Builder maxTotalLength(final int maxTotalLen) {
            if (maxTotalLen < 0) {
                throw new IllegalArgumentException("Cannot set maxTotalLen to a negative value");
            }
            this.maxTotalLen = maxTotalLen;
            return this;
        }

        /**
         * Sets the maximum number length (in chars or bytes, depending on input context).
         * The default is 1000.
         *
         * @param maxNumLen the maximum number length (in chars or bytes, depending on input context)
         *
         * @return this builder
         * @throws IllegalArgumentException if the maxNumLen is set to a negative value
         */
        public Builder maxNumberLength(final int maxNumLen) {
            if (maxNumLen < 0) {
                throw new IllegalArgumentException("Cannot set maxNumberLength to a negative value");
            }
            this.maxNumLen = maxNumLen;
            return this;
        }

        /**
         * Sets the maximum string length (in chars or bytes, depending on input context).
         * The default is 1,000,000.
         * Setting this value to lower than the {@link #maxNumberLength(int)} is not recommended.
         *
         * @param maxStringLen the maximum string length (in chars or bytes, depending on input context)
         *
         * @return this builder
         * @throws IllegalArgumentException if the maxStringLen is set to a negative value
         */
        public Builder maxStringLength(final int maxStringLen) {
            if (maxStringLen < 0) {
                throw new IllegalArgumentException("Cannot set maxStringLen to a negative value");
            }
            this.maxStringLen = maxStringLen;
            return this;
        }

        Builder() {
            this(DEFAULT_MAX_TOTAL_LEN, DEFAULT_MAX_NUM_LEN, DEFAULT_MAX_STRING_LEN);
        }

        Builder(final int maxTotalLen, final int maxNumLen, final int maxStringLen) {
            this.maxTotalLen = maxTotalLen;
            this.maxNumLen = maxNumLen;
            this.maxStringLen = maxStringLen;
        }

        Builder(StreamReadConstraints src) {
            maxTotalLen = src._maxTotalLen;
            maxNumLen = src._maxNumLen;
            maxStringLen = src._maxStringLen;
        }

        public StreamReadConstraints build() {
            return new StreamReadConstraints(
                    maxTotalLen, maxNumLen, maxStringLen);
        }
    }

    /*
    /**********************************************************************
    /* Life-cycle
    /**********************************************************************
     */

    StreamReadConstraints(final int maxTotalLen, final int maxNumLen, final int maxStringLen) {
        _maxTotalLen = maxTotalLen;
        _maxNumLen = maxNumLen;
        _maxStringLen = maxStringLen;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static StreamReadConstraints defaults() {
        return DEFAULT;
    }

    /**
     * @return New {@link Builder} initialized with settings of this constraints
     *   instance
     */
    public Builder rebuild() {
        return new Builder(this);
    }

    /*
    /**********************************************************************
    /* Accessors
    /**********************************************************************
     */

    /**
     * Accessor for maximum length of bytes or chars to decode.
     * see {@link Builder#maxNumberLength(int)} for details.
     *
     * @return Maximum allowed total length
     */
    public int getMaxTotalLength() {
        return _maxTotalLen;
    }

    /**
     * Accessor for maximum length of numbers to decode.
     * see {@link Builder#maxNumberLength(int)} for details.
     *
     * @return Maximum allowed number length
     */
    public int getMaxNumberLength() {
        return _maxNumLen;
    }

    /**
     * Accessor for maximum length of strings to decode.
     * see {@link Builder#maxStringLength(int)} for details.
     *
     * @return Maximum allowed string length
     */
    public int getMaxStringLength() {
        return _maxStringLen;
    }

    /*
    /**********************************************************************
    /* Convenience methods for validation
    /**********************************************************************
     */

    /**
     * Convenience method that can be used to verify that a floating-point
     * number of specified length does not exceed maximum specific by this
     * constraints object: if it does, a
     * {@link NumberFormatException}
     * is thrown.
     *
     * @param length Length of number in input units
     *
     * @throws NumberFormatException If length exceeds maximum
     */
    public void validateFPLength(int length) throws NumberFormatException
    {
        if (length > _maxNumLen) {
            throw new NumberFormatException(String.format("Number length (%d) exceeds the maximum length (%d)",
                    length, _maxNumLen));
        }
    }

    /**
     * Convenience method that can be used to verify that an integer
     * number of specified length does not exceed maximum specific by this
     * constraints object: if it does, a
     * {@link NumberFormatException}
     * is thrown.
     *
     * @param length Length of number in input units
     *
     * @throws NumberFormatException If length exceeds maximum
     */
    public void validateIntegerLength(int length) throws NumberFormatException
    {
        if (length > _maxNumLen) {
            throw new NumberFormatException(String.format("Number length (%d) exceeds the maximum length (%d)",
                    length, _maxNumLen));
        }
    }

    /**
     * Convenience method that can be used to verify that a String
     * of specified length does not exceed maximum specific by this
     * constraints object: if it does, an
     * {@link IllegalStateException}
     * is thrown.
     *
     * @param length Length of string in input units
     *
     * @throws IllegalStateException If length exceeds maximum
     */
    public void validateStringLength(int length) throws IllegalStateException
    {
        if (length > _maxStringLen) {
            throw new IllegalStateException(String.format("String length (%d) exceeds the maximum length (%d)",
                    length, _maxStringLen));
        }
    }
}
