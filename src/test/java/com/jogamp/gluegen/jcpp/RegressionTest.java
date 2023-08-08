/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jogamp.gluegen.jcpp;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.PatternFilenameFilter;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import com.jogamp.gluegen.Logging;
import com.jogamp.gluegen.Logging.LoggerIf;
import com.jogamp.gluegen.test.junit.generation.BuildEnvironment;
import com.jogamp.junit.util.SingletonJunitCase;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author shevek
 */
@RunWith(Parameterized.class)
public class RegressionTest extends SingletonJunitCase {

    private static final LoggerIf LOG = Logging.getLogger(RegressionTest.class);

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() throws Exception {
        final List<Object[]> out = new ArrayList<Object[]>();

        final File dir = new File(BuildEnvironment.gluegenRoot+"/jcpp/src/test/resources/regression");
        for (final File inFile : dir.listFiles(new PatternFilenameFilter(".*\\.in"))) {
            final String name = Files.getNameWithoutExtension(inFile.getName());
            final File outFile = new File(dir, name + ".out");
            out.add(new Object[]{name, inFile, outFile});
        }

        return out;
    }

    private final String name;
    private final File inFile;
    private final File outFile;

    public RegressionTest(final String name, final File inFile, final File outFile) {
        this.name = name;
        this.inFile = inFile;
        this.outFile = outFile;
    }

    @Test
    public void testRegression() throws Exception {
        LOG.setLevel(Level.INFO);
        @SuppressWarnings("deprecation")
        final String inText = Files.toString(inFile, Charsets.UTF_8);
        LOG.info("Read " + name + ":\n" + inText);
        final CppReader cppReader = new CppReader(new StringReader(inText));
        final String cppText = CharStreams.toString(cppReader);
        LOG.info("Generated " + name + ":\n" + cppText);
        if (outFile.exists()) {
            @SuppressWarnings("deprecation")
            final String outText = Files.toString(outFile, Charsets.UTF_8);
            LOG.info("Expected " + name + ":\n" + outText);
            assertEquals(outText, inText);
        }

    }

    public static void main(final String args[]) throws IOException {
        final String tstname = RegressionTest.class.getName();
        org.junit.runner.JUnitCore.main(tstname);
    }
}
