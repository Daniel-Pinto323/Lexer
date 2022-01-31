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

        for(int i =0; i < program.length(); i++) {

            if(program.charAt(i) == '\n'){

                column++;

            }

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
                    tokens.add(new token("==", row, column));
                    i+=1;//skips the following character
                    column++;//accounts for the column that was skipped
                }else{
                    currTok = "=";
                    tokens.add(new token("=", row, column));
                }
             }
          column++;
        }
       tokens.add(new token(" "));
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