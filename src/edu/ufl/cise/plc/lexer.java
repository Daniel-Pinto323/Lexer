package edu.ufl.cise.plc;
import java.util.ArrayList;

class lexer implements ILexer{
ArrayList<token> tokens = new ArrayList<token>(20);
String currTok;


    public lexer(String input) {
        sepTok(input);
    }

    public void sepTok(String program){
        boolean letters = false;
        boolean equals = false;
        int row = 0;
        int column = 0;


        //this function will break the tokens up and store them in a container

        for(int i = 0; i < program.length(); i++) {

                // STRING LITs
            if (program.charAt(i) == '\"') {
                do {
                    i++;
                    column++;
                    currTok += program.charAt(i);
                } while (program.charAt(i) != '\"');

                tokens.add(new token(currTok, row, column, IToken.Kind.STRING_LIT));
            }
            /*if (program.charAt(i) == '\\') {

                if (program.charAt(i + 1) == 'b') {
                    tokens.add(new token("\\b", row, column));
                    i++;//skips the following character
                    column++;//accounts for the column that was skipped
                }
                else if (program.charAt(i + 1) == 't'){
                    tokens.add(new token("\\t", row, column));
                    i++;//skips the following character
                    column++;//accounts for the column that was skipped
                }
                else if (program.charAt(i + 1) == 'n'){
                    tokens.add(new token("\\n", row, column));
                    i++;//skips the following character
                    column++;//accounts for the column that was skipped
                }
                else if (program.charAt(i + 1) == 'f'){
                    tokens.add(new token("\\f", row, column));
                    i++;//skips the following character
                    column++;//accounts for the column that was skipped
                }
                else if (program.charAt(i + 1) == 'r'){
                    tokens.add(new token("\\r", row, column));
                    i++;//skips the following character
                    column++;//accounts for the column that was skipped
                }
            }*/

                  //IDENTIFIERS AND RESERVED WORDS
            if (Character.isLetter(program.charAt(i))) {
                   currTok += program.charAt(i);
                   letters = true;
            }
            if (!Character.isLetter(program.charAt(i)) && letters && Character.isDigit(program.charAt(i))) {
                   currTok += (program.charAt(i));
            }

            if (!Character.isLetter(program.charAt(i)) && !Character.isDigit(program.charAt(i)) && letters) {
                   letters = false;
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
       tokens.add(new token(" ", row, column, IToken.Kind.EOF));
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