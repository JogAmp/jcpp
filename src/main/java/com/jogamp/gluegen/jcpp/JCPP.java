/**
 * Copyright 2015 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */
package com.jogamp.gluegen.jcpp;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.jogamp.gluegen.ASTLocusTag;
import com.jogamp.gluegen.ConstantDefinition;
import com.jogamp.gluegen.GenericCPP;
import com.jogamp.gluegen.GlueGen;
import com.jogamp.gluegen.GlueGenException;
import com.jogamp.gluegen.Logging;
import com.jogamp.gluegen.Logging.LoggerIf;

public class JCPP implements GenericCPP {
    private static final LoggerIf LOG = Logging.getLogger(JCPP.class);

    public final Preprocessor cpp;
    private OutputStream out;
    private final List<String> includePaths;
    private final boolean enableCopyOutput2Stderr;

    public JCPP(final List<String> includePaths, final boolean debug, final boolean copyOutput2Stderr) {
        setOut(System.out);
        this.includePaths = includePaths;
        this.enableCopyOutput2Stderr = copyOutput2Stderr;

        cpp = new Preprocessor();
        cpp.addFeature(Feature.DIGRAPHS);
        cpp.addFeature(Feature.TRIGRAPHS);
        cpp.addFeature(Feature.LINEMARKERS);
        cpp.addFeature(Feature.CSYNTAX);
        cpp.addFeature(Feature.KEEPCOMMENTS);
        cpp.addWarning(Warning.IMPORT);
        cpp.setListener(new DefaultPreprocessorListener() {
            @Override
            public void handleError(final Source source, final int line, final int column,
                                    final String msg) throws LexerException {
                super.handleError(source, line, column, msg);
                throw new GlueGenException(msg, new ASTLocusTag(source.getPath(), line, column, null));
            }
        });
        if (debug) {
            cpp.addFeature(Feature.DEBUG);
        }
        cpp.setSystemIncludePath(includePaths);
        cpp.setQuoteIncludePath(includePaths);

        if (cpp.getFeature(Feature.DEBUG)) {
            LOG.info("#" + "include \"...\" search starts here:");
            for (final String dir : cpp.getQuoteIncludePath())
                LOG.info("  " + dir);
            LOG.info("#" + "include <...> search starts here:");
            for (final String dir : cpp.getSystemIncludePath())
                LOG.info("  " + dir);
            LOG.info("End of search list.");
        }
    }

    @Override
    public void addDefine(final String name, final String value) throws LexerException {
        cpp.addMacro(name, value);
    }

    @Override
    public List<ConstantDefinition> getConstantDefinitions() {
        final List<ConstantDefinition> constants = new ArrayList<ConstantDefinition>();
        final Map<String, Macro> macroMap = cpp.getMacros();
        final Collection<Macro> macros = macroMap.values();
        for(final Macro macro : macros) {
            final String name = macro.getName();
            if( !GlueGen.__GLUEGEN__.equals(name) ) {
                if( !macro.isFunctionLike() ) {
                    final String value = macro.getText();
                    if ( ConstantDefinition.isConstantExpression(value) ) {
                        final Source source = macro.getSource();
                        final ASTLocusTag locus = new ASTLocusTag(
                                                    null != source ? source.getPath() : "<programmatic>",
                                                    null != source ? source.getLine() : -1,
                                                    null != source ? source.getColumn() : -1,
                                                    macro.toString());
                        final ConstantDefinition c = new ConstantDefinition(macro.getName(), value, locus);
                        constants.add(c);
                    }
                }
            }
        }
        return constants;
    }

    @Override
    public String findFile(final String filename) {
        final String sep = File.separator;
        for (final String inclPath : includePaths) {
            final String fullPath = inclPath + sep + filename;
            final File file = new File(fullPath);
            if (file.exists()) {
                return fullPath;
            }
        }
        return null;
    }

    @Override
    public OutputStream out() {
        return out;
    }
    @Override
    public void setOut(final OutputStream out) {
        this.out = out;
    }

    @Override
    public void run(final Reader reader, final String filename) throws GlueGenException {
        final PrintWriter writer = new PrintWriter(out);
        cpp.addInput(new LexerSource(reader, true) {
                        @Override
                        public String getPath() { return filename; }
                        @Override
                        public String getName() { return filename; }
                        @Override
                        public String toString() { return "file " + filename; }
                   } );
        try {
            for (;;) {
                final Token tok = cpp.token();
                if (tok == null)
                    break;
                if (tok.getType() == Token.EOF)
                    break;
                final String s = tok.getText();
                writer.print(s);
                if (enableCopyOutput2Stderr) {
                    System.err.print(s);
                    System.err.flush();
                }
            }
            writer.flush();
        } catch (final Exception e) {
            final StringBuilder buf = new StringBuilder("Preprocessor failed:\n");
            Source s = cpp.getSource();
            while (s != null) {
                buf.append(" -> ").append(s).append("\n");
                s = s.getParent();
            }
            buf.append(" : {0}\n");
            LOG.log(Level.SEVERE, buf.toString(), e);
            if( e instanceof GlueGenException ) {
                throw (GlueGenException)e;
            } else {
                throw new GlueGenException("Preprocessor failed",
                                           new ASTLocusTag(null != s ? s.getPath() : "n/a",
                                                           null != s ? s.getLine() : -1,
                                                           null != s ? s.getColumn() : -1, null), e);
            }
        }
    }

}
