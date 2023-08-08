/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jogamp.gluegen.jcpp;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.junit.Test;
import com.jogamp.gluegen.Logging;
import com.jogamp.gluegen.Logging.LoggerIf;
import com.jogamp.gluegen.test.junit.generation.BuildEnvironment;
import com.jogamp.junit.util.SingletonJunitCase;
import static org.junit.Assert.*;

/**
 *
 * @author shevek
 */
public class PragmaTest extends SingletonJunitCase {

    private static final LoggerIf LOG = Logging.getLogger(PragmaTest.class);

    @Test
    public void testPragma() throws Exception {
        LOG.setLevel(Level.INFO);
        final File file = new File(BuildEnvironment.gluegenRoot+"/jcpp/src/test/resources/pragma.c");
        assertTrue(file.exists());

        final CharSource source = Files.asCharSource(file, Charsets.UTF_8);
        final CppReader r = new CppReader(source.openBufferedStream());
        r.getPreprocessor().setListener(new DefaultPreprocessorListener());
        final String output = CharStreams.toString(r);
        r.close();
        LOG.info("Output: " + output);
        // assertTrue(output.contains("absolute-result"));
    }

    public static void main(final String args[]) throws IOException {
        final String tstname = PragmaTest.class.getName();
        org.junit.runner.JUnitCore.main(tstname);
    }
}
