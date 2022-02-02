package edu.ufl.cise.plc;
import java.util.ArrayList;

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

        //this function will break the tokens up and store them in a container
        while (i < program.length() - 1) {
            boolean continueFlag = true;

            // STRING LITs (begin with open quotes ' " ')
            if (program.charAt(i) == '\"') {
                int startLine = lineNum;
                int startCol = column;
                boolean danglingQuote = true;
                i++;
                column++;

                // while still within double quotes
                while (continueFlag) {
                    if (program.charAt(i) == '\\') {
                        // ESCAPE SEQUENCES
                        currTok += "\\";
                        switch (program.charAt(i + 1)) {
                            case 'b' -> currTok += "b";
                            case 't' -> currTok += "t";
                            case 'n' -> {
                                currTok += "n";
                                lineNum++;
                                column = 0;
                            }
                            case 'f' -> currTok += "f";
                            case 'r' -> {
                                // assuming all instances of '\r' are followed by '\n'
                                currTok += "r\\n";
                                lineNum += 3;
                                column = 0;
                            }
                            case '\"' -> currTok += "\"";
                            case '\'' -> currTok += "'";
                            case '\\' -> currTok += "\\";
                            default -> {
                                tokens.add(new token(currTok, startLine, startCol, IToken.Kind.ERROR));
                                // throw PLCException(row, column, "ERROR: invalid escape sequence");
                                continueFlag = false;
                            }
                        }
                        // adding to i and column to account for escape sequences being 2 characters
                        i++;
                        column++;
                    }
                    else if (program.charAt(i) == '\"') {
                        danglingQuote = !danglingQuote;
                        i++;
                        column++;
                    }

                    // if while loop iterates to end of program without finding end quote -> add ERROR token + break
                    // potentially requires throwing the error rather than adding ERROR token
                    if (i == program.length() - 1) {
                        if (danglingQuote) {
                            tokens.add(new token(currTok, startLine, startCol, IToken.Kind.ERROR));
                        }
                        else {
                            tokens.add(new token(currTok, lineNum, column, IToken.Kind.STRING_LIT));
                        }
                        continueFlag = false;
                    }

                    currTok += program.charAt(i);
                }
                currTok = "";
            }

            if (program.charAt(i) == '@') {
                // handling invalid character
                tokens.add(new token(currTok, lineNum, column, IToken.Kind.ERROR));
                // throw new LexicalException("exception"); // not sure how to throw exceptions properly
            }
            if (program.charAt(i) == '#') {
                // handling comments
                while(program.charAt(i) != '\\') {
                    i++;
                    column++;
                }
            }

            //ASSIGN AND EQUALS
            if (program.charAt(i) == '=') {

                if (program.charAt(i + 1) == '=') {
                    currTok = "==";
                    tokens.add(new token("==", lineNum, column, IToken.Kind.EQUALS));
                    i++;//skips the following character
                    column++;//accounts for the column that was skipped
                }else{
                    currTok = "=";
                    tokens.add(new token("=", lineNum, column, IToken.Kind.ASSIGN));
                }
                i++;
                column++;
            }

            //INT-LIT
            if(Character.isDigit(program.charAt(i))){
                int startPos = column;
                while(Character.isDigit(program.charAt(i))){
                    currTok+= program.charAt(i);
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
                while(Character.isLetter(program.charAt(i)) || Character.isDigit(program.charAt(i))){
                    currTok += program.charAt(i);
                    i++;
                    column++;
                }

                tokens.add(new token(currTok, lineNum, startPos, IToken.Kind.IDENT));
                currTok = "";
            }


            if(program.charAt(i) == ' '){
                i++;
                column++;
            }
        }
       tokens.add(new token(null, lineNum, column, IToken.Kind.EOF));
    }

    public IToken next(){
        tokens.add(new token(IToken.Kind.EOF));
        token retTok = tokens.get(0);
        tokens.remove(0);

        return retTok;
    }


    public IToken peek(){
    //same as next just don't iterate to the next position in the container
        return tokens.get(0);
   }
}
