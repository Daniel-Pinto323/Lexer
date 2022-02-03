package edu.ufl.cise.plc;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

class lexer implements ILexer{
    ArrayList<token> tokens = new ArrayList<token>(20);
    String currTok  = "";


    public lexer(String input) {
        sepTok(input);
    }

    public void sepTok(String program) /*throws LexicalException*/ {
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

                // while still within double quotes
                while (continueFlag) {
                    i++;
                    column++;
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
                                column = 0;
                            }
                            case 'f' -> {
                                currTok += "f";
                                column++;
                            }
                            case 'r' -> {
                                // assuming all instances of '\r' are followed by '\n'
                                currTok += "r\\n";
                                lineNum++;
                                column = 0;
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
                        tokens.add(new token(currTok, startLine, startCol, IToken.Kind.STRING_LIT));
                        continueFlag = false;
                        i++;
                        column++;
                    }

                    // if while loop iterates to end of program without finding end quote -> add ERROR token + break
                    // potentially requires throwing the error rather than adding ERROR token
                    if (i == program.length() - 1 && danglingQuote) {
                        tokens.add(new token(currTok, startLine, startCol, IToken.Kind.ERROR));
                        continueFlag = false;
                    }

                    currTok += program.charAt(i);
                }
                currTok = "";
            }

            // @ SYMBOL, OPERATORS, AND OTHER CHARACTERS
            switch (program.charAt(i)) {
                case '@' -> {
                    currTok += '@';
                    tokens.add(new token(currTok, lineNum, column, IToken.Kind.ERROR));
                    currTok = "";
                    i++;
                    column++;
                }
                case '*' -> {
                    tokens.add(new token("*", lineNum, column, IToken.Kind.TIMES));
                    i++;
                    column++;
                }
                case '+' -> {
                    tokens.add(new token("+", lineNum, column, IToken.Kind.PLUS));
                    i++;
                    column++;
                }
                case '-' -> {
                    if(program.charAt(i + 1) == '>') {
                        tokens.add(new token("->", lineNum, column, IToken.Kind.RARROW));
                        i += 2;
                        column += 2;
                    }
                    else {
                        tokens.add(new token("-", lineNum, column, IToken.Kind.MINUS));
                        i++;
                        column++;
                    }
                }
                case '/' -> {
                    tokens.add(new token("/", lineNum, column, IToken.Kind.DIV));
                    i++;
                    column++;
                }
                case '%' -> {
                    tokens.add(new token("%", lineNum, column, IToken.Kind.MOD));
                    i++;
                    column++;
                }
                case '|' -> {
                    tokens.add(new token("|", lineNum, column, IToken.Kind.OR));
                    i++;
                    column++;
                }
                case '!' -> {
                    if(program.charAt(i + 1) == '='){
                        tokens.add(new token("!=", lineNum, column, IToken.Kind.NOT_EQUALS));
                        i += 2;
                        column += 2;
                    }
                    else {
                        tokens.add(new token("!", lineNum, column, IToken.Kind.BANG));
                        i++;
                        column++;
                    }
                }
                case '<' -> {
                    if(program.charAt(i + 1) == '=') {
                        tokens.add(new token("<=", lineNum, column, IToken.Kind.LE));
                        i += 2;
                        column += 2;
                    }
                    else if(program.charAt(i + 1) == '<')    {
                        tokens.add(new token("<<", lineNum, column, IToken.Kind.LANGLE));
                        i += 2;
                        column += 2;
                    }
                    else if(program.charAt(i + 1) == '-'){
                        tokens.add(new token("<-", lineNum, column, IToken.Kind.LARROW));
                        i += 2;
                        column += 2;
                    }
                    else {
                        tokens.add(new token("<", lineNum, column, IToken.Kind.LT));
                        i++;
                        column++;
                    }
                }
                case '>' -> {
                    if(program.charAt(i + 1) == '=') {
                        tokens.add(new token(">=", lineNum, column, IToken.Kind.GE));
                        i += 2;
                        column += 2;
                    }
                    else if(program.charAt(i + 1) == '>')    {
                        tokens.add(new token(">>", lineNum, column, IToken.Kind.RANGLE));
                        i += 2;
                        column += 2;
                    }
                    else {
                        tokens.add(new token(">", lineNum, column, IToken.Kind.GT));
                        i++;
                        column++;
                    }
                }
                case ';' -> {
                    tokens.add(new token(";", lineNum, column, IToken.Kind.SEMI));
                    i++;
                    column++;
                }
                case ',' -> {
                    tokens.add(new token(",", lineNum, column, IToken.Kind.COMMA));
                    i++;
                    column++;
                }
                case '^' -> {
                    tokens.add(new token("^", lineNum, column, IToken.Kind.RETURN));
                    i++;
                    column++;
                }
                case '=' -> {
                    if(program.charAt(i + 1) == '='){
                        tokens.add(new token("==",lineNum, column, IToken.Kind.EQUALS));
                        i += 2;
                        column += 2;
                    }
                    else {
                        tokens.add(new token("=", lineNum, column, IToken.Kind.ASSIGN));
                        i++;
                        column++;
                    }
                }
                case '(' -> {
                    tokens.add(new token("(", lineNum, column, IToken.Kind.LPAREN));

                    i++;
                    column++;
                }
                case ')' -> {
                    tokens.add(new token(")", lineNum, column, IToken.Kind.RPAREN));

                    i++;
                    column++;
                }
                case '[' -> {
                    tokens.add(new token("[", lineNum, column, IToken.Kind.LSQUARE));
                    i++;
                    column++;
                }
                case ']' -> {
                    tokens.add(new token("]", lineNum, column, IToken.Kind.RSQUARE));
                    i++;
                    column++;
                }
                case '&' -> {
                    tokens.add(new token("&", lineNum, column, IToken.Kind.AND));
                    i++;
                    column++;
                }

            }

            // HANDLING COMMENTS STARTING WITH CHARACTER '#'
            if (program.charAt(i) == '#') {
                continueFlag = true;
                while (continueFlag) {
                    i++;
                    column++;
                    switch(program.charAt(i)) {
                        case '\n' -> {
                            lineNum++;
                            column = 0;
                            i++;

                            continueFlag = false;
                        }
                        case '\r' -> {
                            lineNum++;
                            column = 0;
                            i += 2; // accounting for the assumption that '\r' is always followed by '\n'

                            continueFlag = false;
                        }
                    }
                    if (i == program.length() - 1) {
                        continueFlag = false;
                    }
                }
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
                switch(currTok){
                    case "true", "false" -> {
                        tokens.add(new token(currTok, lineNum, startPos, IToken.Kind.BOOLEAN_LIT));
                    }
                    case "BLACK", "BLUE", "CYAN", "DARK_GRAY", "GRAY", "GREEN", "LIGHT_GRAY", "MAGENTA", "ORANGE", "PINK",
                            "RED", "WHITE" -> {
                        tokens.add(new token(currTok, lineNum, startPos, IToken.Kind.COLOR_CONST));
                    }
                    case "if" -> {
                        tokens.add(new token(currTok, lineNum, startPos, IToken.Kind.KW_IF));
                    }
                    case "fi" -> {
                        tokens.add(new token(currTok, lineNum, startPos, IToken.Kind.KW_FI));
                    }
                    case "else" -> {
                        tokens.add(new token(currTok, lineNum, startPos, IToken.Kind.KW_ELSE));
                    }
                    case "write" -> {
                        tokens.add(new token(currTok, lineNum, startPos, IToken.Kind.KW_WRITE));
                    }
                    case "console" -> {
                        tokens.add(new token(currTok, lineNum, startPos, IToken.Kind.KW_CONSOLE));
                    }
                    case "void" -> {
                        tokens.add(new token(currTok, lineNum, startPos, IToken.Kind.KW_VOID));
                    }
                    case "int", "float", "string", "boolean", "color", "image" -> {
                        tokens.add(new token(currTok, lineNum, startPos, IToken.Kind.TYPE));
                    }
                    case "getRed", "getGreen", "getBlue" -> {
                        tokens.add(new token(currTok, lineNum, startPos, IToken.Kind.COLOR_OP));
                    }
                    case "getWidth", "getHeight" -> {
                        tokens.add(new token(currTok, lineNum, startPos, IToken.Kind.IMAGE_OP));
                    }
                    default -> {
                        tokens.add(new token(currTok, lineNum, startPos, IToken.Kind.IDENT));
                    }
                }
                currTok = "";
            }

            // HANDLING WHITE-SPACE
            switch(program.charAt(i)) {
                case '\n' -> {
                    lineNum++;
                    column = 0;
                    i++;
                }
                case '\r' -> {
                    lineNum++;
                    column = 0;
                    i += 2; // to account for the assumption that '\r' will always be followed by '\n'
                }
                case ' ' -> {
                    i++;
                    column++;
                }
            }
        }
        tokens.add(new token(null, lineNum, column, IToken.Kind.EOF));
    }


    public IToken next() throws LexicalException {

        token retTok = tokens.get(0);
        tokens.remove(0);
        if (retTok.kind == IToken.Kind.ERROR) {
            throw new LexicalException("ERROR: ERROR token encountered at line: " + retTok.line + " column: " + retTok.col);
        }

        return retTok;
    }


    public IToken peek(){
        //same as next just don't iterate to the next position in the container
        return tokens.get(0);
    }
}