package edu.ufl.cise.plc;

public interface ILexer {

	IToken next() throws LexicalException;//increment position
	IToken peek() throws LexicalException;//do not increment position
}
