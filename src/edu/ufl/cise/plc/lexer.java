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
                                column = -1;
                            }
                            case 'f' -> {
                                currTok += "f";
                                column++;
                            }
                            case 'r' -> {
                                // assuming all instances of '\r' are followed by '\n'
                                currTok += "r\\n";
                                lineNum++;
                                column = -1;
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
                }
                case '*' -> {
                    tokens.add(new token("*", lineNum, column, IToken.Kind.TIMES));
                }
                case '+' -> {
                    tokens.add(new token("+", lineNum, column, IToken.Kind.PLUS));
                }
                case '-' -> {
                    if(program.charAt(i + 1) == '>') {
                        tokens.add(new token("->", lineNum, column, IToken.Kind.RARROW));
                        i++;
                        column++;
                    }
                    else {
                        tokens.add(new token("-", lineNum, column, IToken.Kind.MINUS));
                    }
                }
                case '/' -> {
                    tokens.add(new token("/", lineNum, column, IToken.Kind.DIV));
                }
                case '%' -> {
                    tokens.add(new token("%", lineNum, column, IToken.Kind.MOD));
                }
                case '|' -> {
                    tokens.add(new token("|", lineNum, column, IToken.Kind.OR));
                }
                case '!' -> {
                    if(program.charAt(i + 1) == '='){
                        tokens.add(new token("!=", lineNum, column, IToken.Kind.NOT_EQUALS));
                        i++;
                        column++;
                    }
                    else {
                        tokens.add(new token("!", lineNum, column, IToken.Kind.BANG));
                    }
                }
                case '<' -> {
                    if(program.charAt(i + 1) == '=') {
                        tokens.add(new token("<=", lineNum, column, IToken.Kind.LE));
                        i++;
                        column++;
                    }
                    else if(program.charAt(i + 1) == '<')    {
                        tokens.add(new token("<<", lineNum, column, IToken.Kind.LANGLE));
                        i++;
                        column++;
                    }
                    else if(program.charAt(i + 1) == '-'){
                        tokens.add(new token("<-", lineNum, column, IToken.Kind.LARROW));
                        i++;
                        column++;
                    }
                    else {
                        tokens.add(new token("<", lineNum, column, IToken.Kind.LT));
                    }
                }
                case '>' -> {
                    if(program.charAt(i + 1) == '=') {
                        tokens.add(new token(">=", lineNum, column, IToken.Kind.GE));
                        i++;
                        column++;
                    }
                    else if(program.charAt(i + 1) == '>')    {
                        tokens.add(new token(">>", lineNum, column, IToken.Kind.RANGLE));
                        i++;
                        column++;
                    }
                    else {
                        tokens.add(new token(">", lineNum, column, IToken.Kind.GT));
                    }
                }
                case ';' -> {
                    tokens.add(new token(";", lineNum, column, IToken.Kind.SEMI));
                }
                case ',' -> {
                    tokens.add(new token(",", lineNum, column, IToken.Kind.COMMA));
                }
                case '^' -> {
                    tokens.add(new token("^", lineNum, column, IToken.Kind.RETURN));
                }
                case '=' -> {
                    if(program.charAt(i + 1) == '='){
                        tokens.add(new token("==",lineNum, column, IToken.Kind.EQUALS));
                        i++;
                        column++;
                    }
                    else {
                        tokens.add(new token("=", lineNum, column, IToken.Kind.ASSIGN));
                    }
                }
                case '(' -> {
                    tokens.add(new token("(", lineNum, column, IToken.Kind.LPAREN));
                }
                case ')' -> {
                    tokens.add(new token(")", lineNum, column, IToken.Kind.RPAREN));
                }
                case '[' -> {
                    tokens.add(new token("[", lineNum, column, IToken.Kind.LSQUARE));
                }
                case ']' -> {
                    tokens.add(new token("]", lineNum, column, IToken.Kind.RSQUARE));
                }
                case '&' -> {
                    tokens.add(new token("&", lineNum, column, IToken.Kind.AND));
                }
            }

            /* //INT_LIT && FLOAT_LIT
            if (Character.isDigit(program.charAt(i)) || program.charAt(i) == '.') {
                startCol = column;
                char digitOrDot = program.charAt(i);
                currTok += digitOrDot;
                //column++ was here;

                if (currTok.equals(".")) {
                    // decimal point only -> ERROR
                    currTok = "";
                    tokens.add(new token (".", lineNum, startCol, IToken.Kind.ERROR));
                    //column++ was here;
                }
                else if (currTok.equals("0")) {
                    // 0 could be an INT_LIT or FLOAT_LIT
                    if (i < program.length() - 2) {
                        if (program.charAt(i) == '.') {
                            // 0 followed by . -> FLOAT_LIT
                            currTok += ".";
                            //column++ was here;
                            if (!Character.isDigit(program.charAt(i))) {
                                tokens.add(new token (".", lineNum, column, IToken.Kind.ERROR));
                            }
                            else {
                                while (Character.isDigit(program.charAt(i))) {
                                    currTok += program.charAt(i);
                                    //column++ was here;
                                }
                            }
                        }
                        else {
                            tokens.add(new token("0", lineNum, startCol, IToken.Kind.INT_LIT));
                        }
                    }
                    else {
                        tokens.add(new token("0", lineNum, startCol, IToken.Kind.INT_LIT));
                    }
                }
                else {
                    // either INT_LIT or FLOAT_LIT starting with any non-zero digit
                }
             }
            * */

            //INT-LIT && FLOAT LIT
            if (Character.isDigit(program.charAt(i)) || program.charAt(i) == '.') {
                boolean safeFloat = false;
                boolean Flo = false;
                boolean litZero = false;
                startCol = column;

                if(program.charAt(i) == '0' && program.charAt(i + 1) != '.') {
                    litZero = true;
                    tokens.add(new token("0", lineNum, startCol, IToken.Kind.INT_LIT));
                }
                else {
                    while (Character.isDigit(program.charAt(i)) || program.charAt(i) == '.') {

                        currTok += program.charAt(i);
                        //i++;
                        //column++;

                        if (program.charAt(i) == '.') {
                            Flo = true;
                            if (Character.isDigit(program.charAt(i + 1))) {
                                safeFloat = true;
                            } else{
                                tokens.add(new token(".", lineNum, startCol, IToken.Kind.ERROR));
                            }
                        }
                        i++;
                        column++;
                    }
                }

               if(!Flo && !litZero) {
                   try {
                       Integer.valueOf(currTok);
                       tokens.add(new token(currTok, lineNum, startCol, IToken.Kind.INT_LIT));
                   } catch (Exception e) {
                       tokens.add(new token(currTok, lineNum, startCol, IToken.Kind.ERROR));
                   }
               }

               if(Flo && safeFloat){
                   try{
                       Float.valueOf(currTok);
                       tokens.add(new token(currTok, lineNum, startCol, IToken.Kind.FLOAT_LIT));
                   }catch (Exception e) {
                       tokens.add(new token(currTok, lineNum, startCol, IToken.Kind.ERROR));
                   }
               }
               currTok = "";
            }

            //IDENTIFIERS AND RESERVED WORDS
            if (Character.isLetter(program.charAt(i))) {
                //if the character is a letter
                startCol = column;
                while (Character.isLetter(program.charAt(i)) || Character.isDigit(program.charAt(i)) || program.charAt(i) == '_') {
                    currTok += program.charAt(i);
                    i++;
                    column++;
                }
                switch(currTok){
                    case "true", "false" -> {
                        tokens.add(new token(currTok, lineNum, startCol, IToken.Kind.BOOLEAN_LIT));
                    }
                    case "BLACK", "BLUE", "CYAN", "DARK_GRAY", "GRAY", "GREEN", "LIGHT_GRAY", "MAGENTA", "ORANGE", "PINK",
                            "RED", "WHITE", "YELLOW" -> {
                        tokens.add(new token(currTok, lineNum, startCol, IToken.Kind.COLOR_CONST));
                    }
                    case "if" -> {
                        tokens.add(new token(currTok, lineNum, startCol, IToken.Kind.KW_IF));
                    }
                    case "fi" -> {
                        tokens.add(new token(currTok, lineNum, startCol, IToken.Kind.KW_FI));
                    }
                    case "else" -> {
                        tokens.add(new token(currTok, lineNum, startCol, IToken.Kind.KW_ELSE));
                    }
                    case "write" -> {
                        tokens.add(new token(currTok, lineNum, startCol, IToken.Kind.KW_WRITE));
                    }
                    case "console" -> {
                        tokens.add(new token(currTok, lineNum, startCol, IToken.Kind.KW_CONSOLE));
                    }
                    case "void" -> {
                        tokens.add(new token(currTok, lineNum, startCol, IToken.Kind.KW_VOID));
                    }
                    case "int", "float", "string", "boolean", "color", "image" -> {
                        tokens.add(new token(currTok, lineNum, startCol, IToken.Kind.TYPE));
                    }
                    case "getRed", "getGreen", "getBlue" -> {
                        tokens.add(new token(currTok, lineNum, startCol, IToken.Kind.COLOR_OP));
                    }
                    case "getWidth", "getHeight" -> {
                        tokens.add(new token(currTok, lineNum, startCol, IToken.Kind.IMAGE_OP));
                    }
                    default -> {
                        tokens.add(new token(currTok, lineNum, startCol, IToken.Kind.IDENT));
                    }
                }
                currTok = "";
            }

            // HANDLING COMMENTS STARTING WITH CHARACTER '#'
            if (program.charAt(i) == '#') {
                continueFlag = true;
                while (continueFlag) {
                    i++;
                    column++;
                    switch(program.charAt(i)) {
                        case '\n', '\r' -> {
                            continueFlag = false;
                        }
                    }
                    if (i == program.length() - 1) {
                        continueFlag = false;
                    }
                }
            }

            // HANDLING WHITE-SPACE
            switch (program.charAt(i)) {
                case '\n' -> {
                    lineNum++;
                    column = -1;
                }
                case '\r' -> {
                    lineNum++;
                    column = -1;
                    i++; // to account for the assumption that '\r' will always be followed by '\n'
                }
            }
            i++;
            column++;
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