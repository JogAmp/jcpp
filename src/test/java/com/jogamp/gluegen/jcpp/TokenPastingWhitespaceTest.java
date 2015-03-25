package com.jogamp.gluegen.jcpp;

import com.jogamp.common.util.IOUtil;

import java.io.IOException;
import java.io.Reader;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.jogamp.gluegen.Logging;
import com.jogamp.gluegen.Logging.LoggerIf;
import com.jogamp.junit.util.SingletonJunitCase;

import static org.junit.Assert.*;

/**
 * https://github.com/shevek/jcpp/issues/25
 *
 * @author shevek
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TokenPastingWhitespaceTest extends SingletonJunitCase {

    private static final LoggerIf LOG = Logging.getLogger(TokenPastingWhitespaceTest.class);

    @Test
    public void test01WhitespacePasting() throws IOException {
        final Preprocessor pp = new Preprocessor();
        testWhitespacePastingImpl(pp);
    }
    void testWhitespacePastingImpl(final Preprocessor pp) throws IOException {
        pp.addInput(new StringLexerSource(
                "#define ONE(arg) one_##arg\n"
                + "#define TWO(arg) ONE(two_##arg)\n"
                + "\n"
                + "TWO(good)\n"
                + "TWO(     /* evil newline */\n"
                + "    bad)\n"
                + "\n"
                + "ONE(good)\n"
                + "ONE(     /* evil newline */\n"
                + "    bad)\n", true));
        final Reader r = new CppReader(pp);
        final String text = IOUtil.appendCharStream(new StringBuilder(), r).toString().trim();
        LOG.info("Output is:\n" + text);
        assertEquals("one_two_good\n"
                + "one_two_bad\n"
                + "\n"
                + "one_good\n"
                + "one_bad", text);
    }

    public static void main(final String args[]) throws IOException {
        final String tstname = TokenPastingWhitespaceTest.class.getName();
        org.junit.runner.JUnitCore.main(tstname);
    }
}
