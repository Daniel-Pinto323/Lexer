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


        int i = 0;

        while (i < program.length() -1) {

            //ASSIGN AND EQUALS
            if (program.charAt(i) == '=') {

                if (program.charAt(i + 1) == '=') {
                    currTok = "==";
                    tokens.add(new token("==", row, column, IToken.Kind.EQUALS));
                    i++;//skips the following character
                    column++;//accounts for the column that was skipped
                }else{
                    currTok = "=";
                    tokens.add(new token("=", row, column, IToken.Kind.ASSIGN));
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
                tokens.add(new token(currTok, row, startPos, IToken.Kind.INT_LIT));
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

                tokens.add(new token(currTok, row, startPos, IToken.Kind.IDENT));
                currTok = "";
            }


            if(program.charAt(i) == ' '){
                i++;
                column++;

            
            }
        }
    }


    public void checkReserved(){

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