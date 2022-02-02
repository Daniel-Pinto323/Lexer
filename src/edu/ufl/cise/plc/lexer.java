package edu.ufl.cise.plc;
import java.util.ArrayList;

class lexer implements ILexer{
ArrayList<token> tokens = new ArrayList<token>(20);
String currTok  = "";


    public lexer(String input) {
        sepTok(input);
    }

    public void sepTok(String program){

        int row = 0;
        int column = 0;

        //this function will break the tokens up and store them in a container
        for(int i = 0; i < program.length(); i++) {
            boolean continueFlag = true;

            // STRING LITs (begin with open quotes ' " ')
            if (program.charAt(i) == '\"') {
                int startRow = row;
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
                            case 'b':
                                currTok += "b";
                                break;
                            case 't':
                                currTok += "t";
                                break;
                            case 'n':
                                currTok += "n";
                                row++;
                                column = 0;
                                break;
                            case 'f':
                                currTok += "f";
                                break;
                            case 'r':
                                currTok += "r";
                                row++;
                                column = 0;
                                break;
                            case '\"':
                                currTok += "\"";
                                break;
                            case '\'':
                                currTok += "\'";
                                break;
                            case '\\':
                                currTok += "\\";
                                break;
                            default:
                                tokens.add(new token(currTok, startRow, startCol, IToken.Kind.ERROR));
                                // throw PLCException(row, column, "ERROR: invalid escape sequence");
                                continueFlag = false;
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
                            tokens.add(new token(currTok, startRow, startCol, IToken.Kind.ERROR));
                        }
                        else {
                            tokens.add(new token(currTok, row, column, IToken.Kind.STRING_LIT));
                        }
                        continueFlag = false;
                    }

                    currTok += program.charAt(i);
                }
                currTok = "";
            }

            if (program.charAt(i) == '@') {
                // handling invalid character
                tokens.add(new token(currTok, row, column, IToken.Kind.ERROR));
                // throw new LexicalException("exception"); // not sure how to throw exceptions properly
            }
            if (program.charAt(i) == '#') {
                // handling comments
                while(program.charAt(i) != '\\') {
                    i++;
                    column++;
                }
            }

            //IDENTIFIERS AND RESERVED WORDS
            if (Character.isLetter(program.charAt(i))) {
                //if the character is a letter

                while(Character.isLetter(program.charAt(i)) || Character.isDigit(program.charAt(i))){
                    currTok += program.charAt(i);
                    i++;
                 }

                tokens.add(new token(currTok, row, column, IToken.Kind.IDENT));
            }

            //INT-LIT

            if(Character.isDigit(program.charAt(i))){

                while(Character.isDigit(program.charAt(i))){
                    

                }
            }



            //ASSIGN AND EQUALS
            if (program.charAt(i) == '=') {
                if (program.charAt(i + 1) == '=') {
                    currTok = "==";
                    tokens.add(new token(currTok, row, column, IToken.Kind.EQUALS));
                    i++;//skips the following character
                    column++;//accounts for the column that was skipped
                }
                else{
                    currTok = "=";
                    tokens.add(new token(currTok, row, column, IToken.Kind.ASSIGN));
                }
             }

          column++;
        }
       tokens.add(new token(null, row, column, IToken.Kind.EOF));
    }

    public IToken next(){

        token retTok = tokens.get(0);
        tokens.remove(0);

        return retTok;
}


    public IToken peek(){
    //same as next just don't iterate to the next position in the container
        return tokens.get(0);
   }
}
