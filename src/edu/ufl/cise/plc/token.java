package edu.ufl.cise.plc;

public class token implements IToken{

    String token;
    int row;
    int col;


    public token(String x){

        token = x;
    }

    public token(String x, int r, int c){
        token = x;
        row = r;
        col = c;
    }

   public Kind getKind(){

     if(token == "="){
         return Kind.ASSIGN;
     }

     else if(token == "=="){

         return Kind.EQUALS;
     }

     else if(token == " ") {
         return Kind.EOF;
     }else{
         return Kind.IDENT;
     }

   }

   public String getText(){


       return token;
   }

   public SourceLocation getSourceLocation(){

       return new SourceLocation(row, col);
   }



   public int getIntValue(){


       return 2;
   }



   public float getFloatValue(){



       return 2;
   }


   public boolean getBooleanValue(){


       return true;
   }

   public String getStringValue(){


       return "";
   }
}
