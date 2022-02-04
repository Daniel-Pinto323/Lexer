package edu.ufl.cise.plc;

public class token implements IToken{

    String tokenString;
    int line;
    int col;
    Kind kind;

    public token(Kind k){
        kind = k;
    }


    public token(String x, int l, int c, Kind k){
        tokenString = x;
        line = l;
        col = c;
        kind = k;
    }

    public Kind getKind(){
        return kind;
    }

    public String getText(){
        return tokenString;
    }

    public SourceLocation getSourceLocation(){
        return new SourceLocation(line, col);
    }



    public int getIntValue(){
        return Integer.valueOf(tokenString);
    }



    public float getFloatValue(){
        Integer test = Integer.valueOf(tokenString);
        return test;
    }


    public boolean getBooleanValue(){
        return true;
    }

    public String getStringValue(){
        return tokenString;
    }
}