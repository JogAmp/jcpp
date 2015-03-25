package com.jogamp.gluegen.jcpp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;

import javax.annotation.Nonnull;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.jogamp.gluegen.test.junit.generation.BuildEnvironment;
import com.jogamp.junit.util.SingletonJunitCase;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CppReaderTest extends SingletonJunitCase {

    public static String testCppReader(@Nonnull final String in, final Feature... f) throws Exception {
        final String inclpath = BuildEnvironment.gluegenRoot + "/jcpp/src/test/resources" ;

        System.out.println("Testing " + in);
        final StringReader r = new StringReader(in);
        final CppReader p = new CppReader(r);
        p.getPreprocessor().setSystemIncludePath(
                Collections.singletonList(inclpath)
        );
        p.getPreprocessor().addFeatures(f);
        final BufferedReader b = new BufferedReader(p);

        final StringBuilder out = new StringBuilder();
        String line;
        while ((line = b.readLine()) != null) {
            System.out.println(" >> " + line);
            out.append(line).append("\n");
        }

        return out.toString();
    }

    @Test
    public void testCppReader()
            throws Exception {
        testCppReader("#include <test0.h>\n", Feature.LINEMARKERS);
    }

    @Test
    public void testVarargs()
            throws Exception {
        // The newlines are irrelevant, We want exactly one "foo"
        testCppReader("#include <varargs.c>\n");
    }

    @Test
    public void testPragmaOnce()
            throws Exception {
        // The newlines are irrelevant, We want exactly one "foo"
        final String out = testCppReader("#include <once.c>\n", Feature.PRAGMA_ONCE);
        assertEquals("foo", out.trim());
    }

    @Test
    public void testPragmaOnceWithMarkers()
            throws Exception {
        // The newlines are irrelevant, We want exactly one "foo"
        testCppReader("#include <once.c>\n", Feature.PRAGMA_ONCE, Feature.LINEMARKERS);
    }

    public static void main(final String args[]) throws IOException {
        final String tstname = CppReaderTest.class.getName();
        org.junit.runner.JUnitCore.main(tstname);
    }

}
