package com.jogamp.gluegen.jcpp;

import java.io.StringReader;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.jogamp.junit.util.SingletonJunitCase;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JoinReaderTest extends SingletonJunitCase {

    private void testJoinReader(final String in, final String out, final boolean tg)
            throws Exception {
        System.out.println("Testing " + in + " => " + out);
        final StringReader r = new StringReader(in);
        final JoinReader j = new JoinReader(r, tg);

        for (int i = 0; i < out.length(); i++) {
            final int c = j.read();
            System.out.println("At offset " + i + ": " + (char) c);
            assertEquals(out.charAt(i), c);
        }
        assertEquals(-1, j.read());
        assertEquals(-1, j.read());
    }

    private void testJoinReader(final String in, final String out)
            throws Exception {
        testJoinReader(in, out, true);
        testJoinReader(in, out, false);
    }

    @Test
    public void testJoinReader()
            throws Exception {
        testJoinReader("ab", "ab");
        testJoinReader("a\\b", "a\\b");
        testJoinReader("a\nb", "a\nb");
        testJoinReader("a\\\nb", "ab\n");
        testJoinReader("foo??(bar", "foo[bar", true);
        testJoinReader("foo??/\nbar", "foobar\n", true);
    }

}
