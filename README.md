# Introduction

The C Preprocessor is an interesting standard. It appears to be
derived from the de-facto behaviour of the first preprocessors, and
has evolved over the years. Implementation is therefore difficult.

JCPP is a complete, compliant, standalone, pure Java implementation
of the C preprocessor. It is intended to be of use to people writing
C-style compilers in Java using tools like sablecc, antlr, JLex,
CUP and so forth (although if you aren't using sablecc, you need your
head examined).

This project has has been used to successfully preprocess much of
the source code of the GNU C library. As of version 1.2.5, it can
also preprocess the Apple Objective C library.

# JogAmp Branch

This branch is modified for JogAmp 
to supply [GlueGen](http://jogamp.org/gluegen/www/) with JCPP.

This branch is only intended as a submodule for GlueGen
and hence must be [build from within GlueGen](http://jogamp.org/gluegen/doc/HowToBuild.html).

# Original JCPP Version
* [Homepage](http://www.anarres.org/projects/jcpp/)
* [GitHub](https://github.com/shevek/jcpp.git)

# Documentation

* [JavaDoc API](http://jogamp.org/deployment/jogamp-next/javadoc/gluegen/javadoc/)
