package com.jogamp.gluegen.jcpp;

import java.util.Arrays;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.jogamp.gluegen.Logging;
import com.jogamp.gluegen.Logging.LoggerIf;
import com.jogamp.junit.util.SingletonJunitCase;

import static com.jogamp.gluegen.jcpp.PreprocessorTest.assertType;
import static com.jogamp.gluegen.jcpp.Token.*;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LexerSourceTest extends SingletonJunitCase {

    private static final LoggerIf LOG = Logging.getLogger(LexerSourceTest.class);

    public static void testLexerSource(final String in, final boolean textmatch, final int... out)
            throws Exception {
        LOG.info("Testing '" + in + "' => "
                + Arrays.toString(out));
        final StringLexerSource s = new StringLexerSource(in);

        final StringBuilder buf = new StringBuilder();
        for (int i = 0; i < out.length; i++) {
            final Token tok = s.token();
            LOG.info("Token is " + tok);
            assertType(out[i], tok);
            // assertEquals(col, tok.getColumn());
            buf.append(tok.getText());
        }

        final Token tok = s.token();
        LOG.info("Token is " + tok);
        assertType(EOF, tok);

        if (textmatch)
            assertEquals(in, buf.toString());
    }

    @Test
    public void testLexerSource()
            throws Exception {

        testLexerSource("int a = 5;", true,
                IDENTIFIER, WHITESPACE, IDENTIFIER, WHITESPACE,
                '=', WHITESPACE, NUMBER, ';'
        );

        // \n is WHITESPACE because ppvalid = false
        testLexerSource("# #   \r\n\n\r \rfoo", true,
                HASH, WHITESPACE, '#', WHITESPACE, IDENTIFIER
        );

        // No match - trigraphs
        testLexerSource("%:%:", false, PASTE);
        testLexerSource("%:?", false, '#', '?');
        testLexerSource("%:%=", false, '#', MOD_EQ);

        testLexerSource("0x1234ffdUL 0765I", true,
                NUMBER, WHITESPACE, NUMBER);

        testLexerSource("+= -= *= /= %= <= >= >>= <<= &= |= ^= x", true,
                PLUS_EQ, WHITESPACE,
                SUB_EQ, WHITESPACE,
                MULT_EQ, WHITESPACE,
                DIV_EQ, WHITESPACE,
                MOD_EQ, WHITESPACE,
                LE, WHITESPACE,
                GE, WHITESPACE,
                RSH_EQ, WHITESPACE,
                LSH_EQ, WHITESPACE,
                AND_EQ, WHITESPACE,
                OR_EQ, WHITESPACE,
                XOR_EQ, WHITESPACE,
                IDENTIFIER);

        testLexerSource("/**/", true, CCOMMENT);
        testLexerSource("/* /**/ */", true, CCOMMENT, WHITESPACE, '*', '/');
        testLexerSource("/** ** **/", true, CCOMMENT);
        testLexerSource("//* ** **/", true, CPPCOMMENT);
        testLexerSource("'\\r' '\\xf' '\\xff' 'x' 'aa' ''", true,
                CHARACTER, WHITESPACE,
                CHARACTER, WHITESPACE,
                CHARACTER, WHITESPACE,
                CHARACTER, WHITESPACE,
                SQSTRING, WHITESPACE,
                SQSTRING);

        if (false)  // Actually, I think this is illegal.
            testLexerSource("1i1I1l1L1ui1ul", true,
                    NUMBER, NUMBER,
                    NUMBER, NUMBER,
                    NUMBER, NUMBER);

        testLexerSource("'' 'x' 'xx'", true,
                SQSTRING, WHITESPACE, CHARACTER, WHITESPACE, SQSTRING);
    }

    @Test
    public void testNumbers() throws Exception {
        testLexerSource("0", true, NUMBER);
        testLexerSource("045", true, NUMBER);
        testLexerSource("45", true, NUMBER);
        testLexerSource("0.45", true, NUMBER);
        testLexerSource("1.45", true, NUMBER);
        testLexerSource("1e6", true, NUMBER);
        testLexerSource("1.45e6", true, NUMBER);
        testLexerSource(".45e6", true, NUMBER);
        testLexerSource("-6", true, '-', NUMBER);
    }

    @Test
    public void testNumbersSuffix() throws Exception {
        testLexerSource("6f", true, NUMBER);
        testLexerSource("6d", true, NUMBER);
        testLexerSource("6l", true, NUMBER);
        testLexerSource("6ll", true, NUMBER);
        testLexerSource("6ul", true, NUMBER);
        testLexerSource("6ull", true, NUMBER);
        testLexerSource("6e3f", true, NUMBER);
        testLexerSource("6e3d", true, NUMBER);
        testLexerSource("6e3l", true, NUMBER);
        testLexerSource("6e3ll", true, NUMBER);
        testLexerSource("6e3ul", true, NUMBER);
        testLexerSource("6e3ull", true, NUMBER);
    }

    @Test
    public void testNumbersInvalid() throws Exception {
        // testLexerSource("0x foo", true, INVALID, WHITESPACE, IDENTIFIER);   // FAIL
        testLexerSource("6x foo", true, INVALID, WHITESPACE, IDENTIFIER);
        testLexerSource("6g foo", true, INVALID, WHITESPACE, IDENTIFIER);
        testLexerSource("6xsd foo", true, INVALID, WHITESPACE, IDENTIFIER);
        testLexerSource("6gsd foo", true, INVALID, WHITESPACE, IDENTIFIER);
    }

    @Test
    public void testUnterminatedComment() throws Exception {
        testLexerSource("5 /*", false, NUMBER, WHITESPACE, INVALID);    // Bug #15
        testLexerSource("5 //", false, NUMBER, WHITESPACE, CPPCOMMENT);
    }

    @Test
    public void testUnicode()throws Exception{
        testLexerSource("foo \u2018bar\u2019 baz", true, IDENTIFIER, WHITESPACE, 8216, IDENTIFIER, 8217, WHITESPACE, IDENTIFIER);
    }
}
