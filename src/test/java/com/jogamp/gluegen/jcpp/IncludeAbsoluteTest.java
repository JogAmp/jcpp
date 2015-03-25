package com.jogamp.gluegen.jcpp;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.jogamp.common.util.IOUtil;
import com.jogamp.gluegen.Logging;
import com.jogamp.gluegen.Logging.LoggerIf;
import com.jogamp.gluegen.test.junit.generation.BuildEnvironment;
import com.jogamp.junit.util.SingletonJunitCase;

import static org.junit.Assert.*;

/**
 *
 * @author shevek
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IncludeAbsoluteTest extends SingletonJunitCase {

    private static final LoggerIf LOG = Logging.getLogger(IncludeAbsoluteTest.class);

    @Test
    public void testAbsoluteInclude() throws Exception {
        final String filepath = BuildEnvironment.gluegenRoot + "/jcpp/src/test/resources/absolute.h" ;
        LOG.info("filepath: " + filepath);

        final File file = new File(filepath);
        assertTrue(file.exists());
        final String slashifiedFilePath = IOUtil.slashify(file.getAbsolutePath(), true, false);
        LOG.info("slashifiedFilePath: " + slashifiedFilePath);

        // Expects something like:
        //   WINDOWS: "/C:/projects/jogamp/gluegen/jcpp/src/test/resources/absolute.h"
        //   UNIX:    "/projects/jogamp/gluegen/jcpp/src/test/resources/absolute.h"
        final String input = "#include <" + slashifiedFilePath + ">\n";
        LOG.info("Input: " + input);
        final Preprocessor pp = new Preprocessor();
        pp.addInput(new StringLexerSource(input, true));
        final Reader r = new CppReader(pp);
        final String output = IOUtil.appendCharStream(new StringBuilder(), r).toString();
        r.close();
        LOG.info("Output: " + output);
        assertTrue(output.contains("absolute-result"));
    }
    public static void main(final String args[]) throws IOException {
        final String tstname = IncludeAbsoluteTest.class.getName();
        org.junit.runner.JUnitCore.main(tstname);
    }
}
