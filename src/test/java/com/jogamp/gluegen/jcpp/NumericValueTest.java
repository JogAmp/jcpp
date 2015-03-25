package com.jogamp.gluegen.jcpp;

import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.jogamp.junit.util.SingletonJunitCase;

import static com.jogamp.gluegen.jcpp.Token.*;
import static org.junit.Assert.*;

/**
 *
 * @author shevek
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NumericValueTest extends SingletonJunitCase {

    private Token testNumericValue(final String in) throws IOException, LexerException {
        final StringLexerSource s = new StringLexerSource(in);

        final Token tok = s.token();
        System.out.println("Token is " + tok);
        assertEquals(NUMBER, tok.getType());

        final Token eof = s.token();
        assertEquals("Didn't get EOF, but " + tok, EOF, eof.getType());

        return tok;
    }

    private void testNumericValue(final String in, final double out) throws IOException, LexerException {
        System.out.println("Testing '" + in + "' -> " + out);
        final Token tok = testNumericValue(in);
        assertEquals(in, tok.getText());
        final NumericValue value = (NumericValue) tok.getValue();
        assertEquals("Double mismatch", out, value.doubleValue(), 0.01d);
        assertEquals("Float mismatch", (float) out, value.floatValue(), 0.01f);
        assertEquals("Long mismatch", (long) out, value.longValue());
        assertEquals("Integer mismatch", (int) out, value.intValue());
    }

    @Test
    public void testNumericValue() throws Exception {

        // Zero
        testNumericValue("0", 0);

        // Decimal
        testNumericValue("1", 1);
        testNumericValue("1L", 1);
        testNumericValue("12", 12);
        testNumericValue("12L", 12);

        // Hex
        testNumericValue("0xf", 0xf);
        testNumericValue("0xfL", 0xf);
        testNumericValue("0x12", 0x12);
        testNumericValue("0x12L", 0x12);

        // Negative
        // testNumericValue("-0", 0);
        // testNumericValue("-1", -1);
        // Negative hex
        // testNumericValue("-0x56", -0x56);
        // testNumericValue("-0x102", -0x102);
        // Octal and negative octal
        testNumericValue("0673", Integer.parseInt("673", 8));
        // testNumericValue("-0673", Integer.parseInt("-673", 8));

        // Floating point
        testNumericValue(".0", 0);
        testNumericValue(".00", 0);
        testNumericValue("0.", 0);
        testNumericValue("0.0", 0);
        testNumericValue("00.0", 0);
        testNumericValue("00.", 0);

        // Sign on exponents
        testNumericValue("1e1", 1e1);
        // testNumericValue("-1e1", -1e1);
        testNumericValue("1e-1", 1e-1);

        // Hex numbers with decimal exponents
        testNumericValue("0x12e3", 0x12e3);
        testNumericValue("0x12p3", 0x12p3);

        // Octal numbers with decimal exponents
        testNumericValue("012e3", 012e3);    // Fails
        testNumericValue("067e4", 067e4);    // Fails

        // Issues a warning.
        try {
            testNumericValue("097", 97);
            fail("No warning.");
        } catch (final LexerException e) {
        }

    }
}
