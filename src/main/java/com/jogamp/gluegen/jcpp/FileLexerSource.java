/*
 * Anarres C Preprocessor
 * Copyright (c) 2007-2015, Shevek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.jogamp.gluegen.jcpp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.annotation.Nonnull;

/**
 * A {@link Source} which lexes a file.
 *
 * The input is buffered.
 *
 * @see Source
 */
public class FileLexerSource extends InputLexerSource {

    private final String path;
    private final File file;

    /**
     * Creates a new Source for lexing the given File.
     *
     * Preprocessor directives are honoured within the file.
     */
    public FileLexerSource(@Nonnull File file, @Nonnull Charset charset, @Nonnull String path)
            throws IOException {
        super(new FileInputStream(file), charset);
        this.file = file;
        this.path = path;
    }

    public FileLexerSource(@Nonnull File file, @Nonnull String path)
            throws IOException {
        this(file, Charset.defaultCharset(), path);
    }

    public FileLexerSource(@Nonnull File file, @Nonnull Charset charset)
            throws IOException {
        this(file, charset, file.getPath());
    }

    @Deprecated
    public FileLexerSource(@Nonnull File file)
            throws IOException {
        this(file, Charset.defaultCharset());
    }

    public FileLexerSource(@Nonnull String path, @Nonnull Charset charset)
            throws IOException {
        this(new File(path), charset, path);
    }

    @Deprecated
    public FileLexerSource(@Nonnull String path)
            throws IOException {
        this(path, Charset.defaultCharset());
    }

    @Nonnull
    public File getFile() {
        return file;
    }

    /**
     * This is not necessarily the same as getFile().getPath() in case we are in a chroot.
     */
    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getName() {
        return getPath();
    }

    @Override
    public String toString() {
        return "file " + getPath();
    }
}
