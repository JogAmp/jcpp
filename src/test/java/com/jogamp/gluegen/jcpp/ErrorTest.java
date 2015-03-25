package com.jogamp.gluegen.jcpp;

import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.jogamp.junit.util.SingletonJunitCase;

import static com.jogamp.gluegen.jcpp.Token.*;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ErrorTest extends SingletonJunitCase {

    private boolean testError(final Preprocessor p)
            throws LexerException,
            IOException {
        for (;;) {
            final Token tok = p.token();
            if (tok.getType() == EOF)
                break;
            if (tok.getType() == INVALID)
                return true;
        }
        return false;
    }

    private void testError(final String input) throws Exception {
        StringLexerSource sl;
        DefaultPreprocessorListener pl;
        Preprocessor p;

        /* Without a PreprocessorListener, throws an exception. */
        sl = new StringLexerSource(input, true);
        p = new Preprocessor();
        p.addFeature(Feature.CSYNTAX);
        p.addInput(sl);
        try {
            assertTrue(testError(p));
            fail("Lexing unexpectedly succeeded without listener.");
        } catch (final LexerException e) {
            /* required */
        }

        /* With a PreprocessorListener, records the error. */
        sl = new StringLexerSource(input, true);
        p = new Preprocessor();
        p.addFeature(Feature.CSYNTAX);
        p.addInput(sl);
        pl = new DefaultPreprocessorListener();
        p.setListener(pl);
        assertNotNull("CPP has listener", p.getListener());
        assertTrue(testError(p));
        assertTrue("Listener has errors", pl.getErrors() > 0);

        /* Without CSYNTAX, works happily. */
        sl = new StringLexerSource(input, true);
        p = new Preprocessor();
        p.addInput(sl);
        assertTrue(testError(p));
    }

    @Test
    public void testErrors() throws Exception {
        testError("\"");
        testError("'");
        // testError("''");
    }

}
