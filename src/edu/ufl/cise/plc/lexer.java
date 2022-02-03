package edu.ufl.cise.plc;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

class lexer implements ILexer{
ArrayList<token> tokens = new ArrayList<token>(20);
String currTok  = "";


    public lexer(String input) {
        sepTok(input);
    }

    public void sepTok(String program) {
        int lineNum = 0;
        int column = 0;

        int i = 0;


        // this function will break the tokens up and store them in a container
        while (i < program.length()) {
            boolean continueFlag = true;
            int startLine = lineNum;
            int startCol = column;

            // STRING LITs (begin with open quotes ' " ')
            if (program.charAt(i) == '\"') {
                boolean danglingQuote = true;
                i++;
                column++;

                // while still within double quotes
                while (continueFlag) {
                    if (program.charAt(i) == '\\') {
                        // ESCAPE SEQUENCES
                        currTok += "\\";
                        switch (program.charAt(i + 1)) {
                            case 'b' -> {
                                currTok += "b";
                                column++;
                            }
                            case 't' -> currTok += "t";
                            case 'n' -> {
                                currTok += "n";
                                lineNum++;
                                column = 1;
                            }
                            case 'f' -> {
                                currTok += "f";
                                column++;
                            }
                            case 'r' -> {
                                // assuming all instances of '\r' are followed by '\n'
                                currTok += "r\\n";
                                lineNum++;
                                column = 1;
                            }
                            case '\"' -> {
                                currTok += "\"";
                                column++;
                            }
                            case '\'' -> {
                                currTok += "'";
                                column++;
                            }
                            case '\\' -> {
                                currTok += "\\";
                                column++;
                            }
                            default -> {
                                tokens.add(new token(currTok, startLine, startCol, IToken.Kind.ERROR));
                                // throw PLCException(row, column, "ERROR: invalid escape sequence");
                                continueFlag = false;
                            }
                        }
                    } else if (program.charAt(i) == '\"') {
                        danglingQuote = !danglingQuote;
                        column++;
                    }
                    // i++ to increment i within while loop
                    i++;

                    // if while loop iterates to end of program without finding end quote -> add ERROR token + break
                    // potentially requires throwing the error rather than adding ERROR token
                    if (i == program.length() - 1) {
                        if (danglingQuote) {
                            tokens.add(new token(currTok, startLine, startCol, IToken.Kind.ERROR));
                        } else {
                            tokens.add(new token(currTok, startLine, startCol, IToken.Kind.STRING_LIT));
                        }
                        continueFlag = false;
                    }

                    currTok += program.charAt(i);
                }
                currTok = "";
            }

            // HANDLING INVALID CHARACTER '@'
            if (program.charAt(i) == '@') {
                // handling invalid character
                currTok += '@';
                tokens.add(new token(currTok, lineNum, column, IToken.Kind.ERROR));
                // throw new LexicalException("exception"); // not sure how to throw exceptions properly
                currTok = "";
            }

            // HANDLING COMMENTS STARTING WITH CHARACTER '#'
            if (program.charAt(i) == '#') {
                // handling comments
                startLine = lineNum;
                startCol = column;
                continueFlag = true;
                while (continueFlag && i < program.length() - 1) {
                    i++;
                    if (program.charAt(i) == '\\') {
                        currTok += "\\";
                        switch (program.charAt(i + 1)) {
                            case 'n' -> {
                                currTok += "n";
                                lineNum++;
                                column = 1;
                            }
                            case 'r' -> {
                                // assuming all instances of '\r' are followed by '\n'
                                currTok += "r\\n";
                                lineNum++;
                                column = 1;
                            }
                            default -> {
                                tokens.add(new token(currTok, startLine, startCol, IToken.Kind.ERROR));
                                // throw PLCException(row, column, "ERROR: invalid escape sequence");
                            }
                        }
                        continueFlag = false;
                    }
                }
            }

            //ASSIGN AND EQUALS
            if (program.charAt(i) == '=') {

                if (program.charAt(i + 1) == '=') {
                    currTok = "==";
                    tokens.add(new token("==", lineNum, column, IToken.Kind.EQUALS));
                    i++;//skips the following character
                    column++;//accounts for the column that was skipped
                } else {
                    currTok = "=";
                    tokens.add(new token("=", lineNum, column, IToken.Kind.ASSIGN));
                }
                i++;
                column++;
            }

            //INT-LIT
            if (Character.isDigit(program.charAt(i))) {
                int startPos = column;
                while (Character.isDigit(program.charAt(i))) {
                    currTok += program.charAt(i);
                    i++;
                    column++;
                }
                tokens.add(new token(currTok, lineNum, startPos, IToken.Kind.INT_LIT));
                currTok = "";

            }


            //IDENTIFIERS AND RESERVED WORDS
            if (Character.isLetter(program.charAt(i))) {
                                //if the character is a letter
                int startPos = column;
                while (Character.isLetter(program.charAt(i)) || Character.isDigit(program.charAt(i))) {
                    currTok += program.charAt(i);
                    i++;
                    column++;
                }

                tokens.add(new token(currTok, lineNum, startPos, IToken.Kind.IDENT));
                currTok = "";

            }

            // HANDLING WHITE-SPACE
            switch(program.charAt(i)) {
                case ' ' -> {
                    i++;
                    column++;
                }
                case '\n' -> {
                    lineNum++;
                    column = 1;
                    i += 2;
                }
                case '\r' -> {
                    lineNum++;
                    column = 1;
                    i += 4; // to account for the assumption that '\r' will always be followed by '\n'
                }
            }
         }
       tokens.add(new token(null, lineNum, column, IToken.Kind.EOF));
    }

    public IToken next() throws LexicalException {
        token retTok = tokens.get(0);
        tokens.remove(0);
        if (retTok.kind == IToken.Kind.ERROR) {
            // throw error
        }

        return retTok;
    }


    public IToken peek(){
    //same as next just don't iterate to the next position in the container
        return tokens.get(0);
   }
}
