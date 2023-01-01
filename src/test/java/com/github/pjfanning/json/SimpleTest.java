package com.github.pjfanning.json;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleTest {

    @Test
    void testValidString() throws IOException {
        String text = TestUtils.readResource("/simple.json");
        SizeLimiter.check(StreamReadConstraints.defaults(), text);
    }

    @Test
    void testStringWithLowTotalLimit() throws IOException {
        String text = TestUtils.readResource("/simple.json");
        assertThrows(IllegalStateException.class, () ->
            SizeLimiter.check(StreamReadConstraints.builder().maxTotalLength(5).build(), text),
                "Total length (80) exceeds the maximum length (5)"
        );
    }

    @Test
    void testStringWithLowNumberLimit() throws IOException {
        String text = TestUtils.readResource("/simple.json");
        assertThrows(NumberFormatException.class, () ->
                        SizeLimiter.check(StreamReadConstraints.builder().maxNumberLength(1).build(), text),
                "Number length (2) exceeds the maximum length (1)"
        );
    }

    @Test
    void testStringWithLowStringLimit() throws IOException {
        String text = TestUtils.readResource("/simple.json");
        assertThrows(IllegalStateException.class, () ->
                        SizeLimiter.check(StreamReadConstraints.builder().maxStringLength(1).build(), text),
                "String length (3) exceeds the maximum length (1)"
        );
    }

}
