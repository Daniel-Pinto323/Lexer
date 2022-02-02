# Lexer
COP4020 - Creating a Lexer

Files:
ILexer.java
  - Interface for the lexer itself
  - lexer class should implement this interface

IToken.java
  - Interface for the tokens
  - token class should implement this interface
  - includes Enum ‘Kind’ which distinguishes the different tokens is defined in this interface.
  - When determining the position of a token in the input source code, start counting both lines and columns at 0

CompilerComponentFactory.java
  - Factory class to provide instantiations of your lexer

PLCException.java
  - The superclass of all the Exceptions that we throw in the compiler

LexicalException.java
  - The exception that should be thrown for all error that are discovered during lexing

LexerTests.java
  - Example JUnit tests
  - This class will be expanded with additional tests and used for grading so it is essential that your lexer runs with this code and passes these tests
  - The examples also demonstrate how to write tests, including tests where an exception is expected.
