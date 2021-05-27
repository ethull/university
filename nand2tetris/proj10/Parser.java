package assign4;

import tokenizer.Keyword;
import tokenizer.Token;
import tokenizer.Tokenizer;

import java.util.HashMap;

/**
 * Parse a Jack source file.
 * 
 * @author
 * @version 
 */
public class Parser {
    //vars to deal with symbol table
    // class lv symbol table
    HashMap<String, String> classSymbolTable = new HashMap<String, String>();
    // subroutine lv symbol table
    HashMap<String, String> subroutineSymbolTable;
    //current type, the type of the last variable processed
    String currentType;
    //current identifier, the name of the last identifier processed
    String currentIdentifier;


    // The tokenizer.
    private final Tokenizer lex;
    
    /**
     * Parse a Jack source file.
     * @param lex The tokenizer.
     */
    public Parser(Tokenizer lex)
    {
        this.lex = lex;
    }

    //check if an identifier exists
    public void checkIdentifier(){
        if (!subroutineSymbolTable.containsKey(currentIdentifier)){
            //if not in subroutine or class symbol tables identifier doesnt exist
            if (!classSymbolTable.containsKey(currentIdentifier)) parseError();
        }
        //note ability to check correct type not asked for in brief bullett points, hence not implemented
        //also ability to check subroutine call names are correct not asked for
    }

    //check if an identifier is an array or not
    public void checkIsArray(){
        if (subroutineSymbolTable.containsKey(currentIdentifier)){
            if (!subroutineSymbolTable.get(currentIdentifier).equals("Array")) parseError();
        } else {
            if (!classSymbolTable.get(currentIdentifier).equals("Array")) parseError();
        }
    }

    
    /**
     * Parse a Jack class file.
     * @throws ParsingFailure on failure.
     */
    public void parseClass()
    {
        //advance to class keyword, all other advances are done by eat() mtd
        lex.advance();

        //move to next line
        //lex.advance();

        //class keyword
        eat("class");
        //classname identifier
        eat();

        eat('{');
        while(parseClassVarDec());
        while(parseSubroutineDec());
        //System.out.println("\n " + lex.getTokenDetails());

        //cant eat last char because would need to advance to non-existant line
        if ('}' != lex.getSymbol()) parseError();
        //eat('}');
    }

    //parse class variable declaration, the boolean return var will tell use have many class vars to parse
    public boolean parseClassVarDec(){
        //is the current token static|field
        if (lex.getKeyword() == Keyword.STATIC){
            eat("static");
        } else if (lex.getKeyword() == Keyword.FIELD){
            eat("field");
        } else {
            //return false instead of throwing error since there could be no class vars left or none at all
            return false;
        }
        parseType();
        eat();
        //parseType and eat() save the last identifier/type used, save them permanently to symbol table
        classSymbolTable.put(currentIdentifier, currentType);
        //while symbol is ',' check for variables
		while (lex.getSymbol() == ',') {
            eat(',');
            eat();
            classSymbolTable.put(currentIdentifier, currentType);
        }
        eat(';');
        //assume there will be anouther class var
        return true;
    }

    
    //subroutine mtds
    //subroutine declaration
    public boolean parseSubroutineDec(){
        //create new subroutineSymbolTable
        subroutineSymbolTable = new HashMap<>();

        //is the current token constructor|function|method
        if (lex.getKeyword() == Keyword.CONSTRUCTOR){
            eat("constructor");
        } else if (lex.getKeyword() == Keyword.FUNCTION){
            eat("function");
        } else if (lex.getKeyword() == Keyword.METHOD){
            eat("method");
        } else {
            return false;
        }
        //is the current token void|parseTerm()
        if (lex.getKeyword() == Keyword.VOID){
            eat("void");
        } else {
            //if not void should be a term()
            parseType();
            //dont need to do anything since parseType() will eat the types and deal with errors
            //since previous keyword was correct, if not void or type() token is invalid
            //parseError();
            //return false;
        }
        //subroutineName
        eat();
        eat('(');
        parseParameterList();
        eat(')');
        parseSubroutineBody();
        return true;
    }

    //parse parameter list
    public void parseParameterList(){
        //if we hit a ')' symbol there are no params
        if(lex.getTokenType() == Token.SYMBOL){
            if(lex.getSymbol() == ')'){
                return;
            }
        }

        parseType();
        //varName
        eat();
        //add params to subroutines symbol table
        subroutineSymbolTable.put(currentIdentifier, currentType);
		while (lex.getSymbol() == ',') {
            eat(',');
            parseType();
            //varName
            eat();
            subroutineSymbolTable.put(currentIdentifier, currentType);
        }
    }

    //subroutine body
    public void parseSubroutineBody(){
        eat('{');
        while(parseVarDec());
        while(parseStatement());
        eat('}');
    }


    //parse type, doesnt need to return boolean since we never have type*
    public void parseType(){
        //int|char|boolean| identifier: classname
        if(lex.getTokenType() == Token.KEYWORD){
            if (lex.getKeyword() == Keyword.INT){
                eat("int");
                currentType = "int";
            } else if (lex.getKeyword() == Keyword.CHAR){
                eat("char");
                currentType = "char";
            } else if (lex.getKeyword() == Keyword.BOOLEAN){
                eat("boolean");
                currentType = "boolean";
            } else {
                //if its not the correct keyword the token is invalid
                parseError();
            }
        }else if (lex.getTokenType() == Token.IDENTIFIER){
            currentType = lex.getIdentifier();
            eat();
        } else {
            //if not a keyword or identifier token is invalid, since we expect a term
            parseError();
        }
    }

    //parse variable declaration
    public boolean parseVarDec(){
        if(lex.getTokenType() == Token.KEYWORD){
            if (lex.getKeyword() == Keyword.VAR){
                eat("var"); 
            } else {
                return false;
            }
        }else {
            return false;
        }
        parseType();
        //varName
        eat();
        subroutineSymbolTable.put(currentIdentifier, currentType);
		while (lex.getSymbol() == ',') {
            eat(',');
            //varName
            eat();
            subroutineSymbolTable.put(currentIdentifier, currentType);
        }
        eat(';');
        return true;
    }


    //statement related mtds
    //parse a statement: if,while,do,let,return
    public boolean parseStatement(){
        //are there any statements left or are there any at all
        if(lex.getTokenType() == Token.SYMBOL){
            if (lex.getSymbol() == '}'){
                return false;
            }
        }

        if (lex.getTokenType() == Token.KEYWORD){
            Keyword kw = lex.getKeyword();
            //call relavant statement parse mtd
            if (kw == Keyword.IF) parseIfStatement();
            else if (kw == Keyword.WHILE) parseWhileStatement();
            else if (kw == Keyword.DO) parseDoStatement();
            else if (kw == Keyword.LET) parseLetStatement();
            else if (kw == Keyword.RETURN) parseReturnStatement();
            else parseError(); //no keyword matches to a statement
        } else {
            //since we dont have '}' there should be a keyword
            parseError();
        }
        return true;
    }

    public void parseIfStatement(){
        eat("if");
        eat('(');
        parseExpression();
        eat(')');
        eat('{');
        while(parseStatement());
        eat('}');
		while (lex.getTokenType() == Token.KEYWORD && lex.getKeyword() == Keyword.ELSE){
            eat("else");
            eat('{');
            while(parseStatement());
            eat('}');
        }
    }
    public void parseWhileStatement(){
        eat("while");
        eat('(');
        parseExpression();
        eat(')');
        eat('{');
        while(parseStatement());
        eat('}');
    }
    public void parseDoStatement(){
        eat("do");
        eat(); //since parseSubroutineCall doesnt eat initial identifier, need to eat it here
        parseSubroutineCall();
        eat(';');
    }
    public void parseLetStatement(){
        eat("let");
        eat();

        //check if identifier exists
        checkIdentifier();

		if (lex.getTokenType() == Token.SYMBOL && lex.getSymbol() == '['){
            //check if am trying to index an element that isnt an array
            checkIsArray();

            eat('[');
            parseExpression();
            eat(']');
        }
        eat('=');
        parseExpression();
        eat(';');
    }
    public void parseReturnStatement(){
        eat("return");
        //if there isnt a ';' then there should be an expression
        if (!(lex.getTokenType() == Token.SYMBOL && lex.getSymbol() == ';')) parseExpression();
        eat(';');
    }


    //expr related mtds
    //parse an expr, term (op term)*
    public void parseExpression(){
        parseTerm();
        //while/if there is an op then eat it and the next term
        while(lex.getTokenType() == Token.SYMBOL && isOP(lex.getSymbol())){
            eat(lex.getSymbol());
            parseTerm();
        }
    }

    //parse term
    //  constant: integerConstant, stringConstant, keywordConstant
    //  varName
    //  varName [ expr ]
    //  subroutineCall
    public void parseTerm(){
        //integerConstant
        if (lex.getTokenType() == Token.INT_CONST){
            //eat(Integer(lex.getIntval()).toString());
            eat(Integer.toString(lex.getIntval()));
        //stringConstant
        } else if (lex.getTokenType() == Token.STRING_CONST) {
            eat(lex.getStringVal());
        //keywordConstant //starts with Keyword
        } else if (lex.getTokenType() == Token.KEYWORD) {
            //isKeywordConstant mtd removed since need to eat relative to keyword
            Keyword kw = lex.getKeyword();
            if (kw == Keyword.TRUE) eat("true");
            else if (kw == Keyword.FALSE) eat("false");
            else if (kw == Keyword.NULL) eat("null");
            else if (kw == Keyword.THIS) eat("this");
            else parseError(); //if token is keyword should be a keyword constant
        //(expression) //starts with Symbol
        } else if (lex.getTokenType() == Token.SYMBOL && lex.getSymbol() == '(') {
            eat('(');
            parseExpression();
            eat(')');
        //unaryOp term
        } else if (lex.getTokenType() == Token.SYMBOL && isUnaryOP(lex.getSymbol())) {
			eat(lex.getSymbol());
            parseTerm();
        } else {
            //all remaining avaliable options start with a Identifier
            //will need to advance to check token after Identifier
            if (lex.getTokenType() == Token.IDENTIFIER){
                //eat identifier //varName|subroutineCallIdentifier
                eat();
                //subroutineCall //starts with identifier( OR identifier
                if (lex.getTokenType() == Token.SYMBOL && (lex.getSymbol() == '.' || lex.getSymbol() == '(')){
                    parseSubroutineCall();
                //varName [expression] //starts with identifier[
                } else if (lex.getSymbol() == '[') {
                    //check if identifier exists and if it is an array
                    checkIdentifier();
                    checkIsArray();
                    eat('[');
                    parseExpression();
                    eat(']');
                } else {
                    //if just an identifier check it exists
                    checkIdentifier();
                }
            } else {
                //if not a already done its an error (cant have an empty term)
                parseError();
            }
        }


    }

    public void parseSubroutineCall(){
        //doesnt eat first identifier since is eaten by parseTerm
        //. or (
        if (lex.getSymbol() == '.'){
            eat('.');
            //subroutineName identifier
            eat();
            eat('(');
            parseExpressionList();
            eat(')');
        } else if (lex.getSymbol() == '('){
            eat('(');
            parseExpressionList();
            eat(')');
        }
    }

    public void parseExpressionList(){
        //if there is a ')' symbol then there are no expressions
        if(lex.getTokenType() == Token.SYMBOL){
            if (lex.getSymbol() == ')'){
                return;
            }
        }

        parseExpression();
		while (lex.getSymbol() == ',') {
            eat(',');
            parseExpression();
        }
    }

    //check if current symbol is a operation
	public boolean isOP(char symbol) {
        if (symbol == '+') return true;
        else if (symbol == '-') return true;
        else if (symbol == '*') return true;
        else if (symbol == '/') return true;
        else if (symbol == '&') return true;
        else if (symbol == '|') return true;
        else if (symbol == '<') return true;
        else if (symbol == '>') return true;
        else if (symbol == '=') return true;
        else return false;
	}

    //check if current symbol is a unary operation
	public boolean isUnaryOP(char symbol) {
        if (symbol == '-') return true;
        else if (symbol == '~') return true;
        else return false;
	}


    //mtds to eat tokens
    //  check if correct token -> advance

    //eat keyword
    public void eat(String s){
        if (Keyword.identify(s) != lex.getKeyword()) parseError();
        lex.advance();
    }

    //eat symbols, these are chars since a symbol is only one character
    public void eat(char c){
        if (c != lex.getSymbol()) parseError();
        lex.advance();
    }

    //eat identifier, dont need to pass a value since we are just checking that it is an identifier
    public void eat(){
        if (lex.getTokenType() != Token.IDENTIFIER) parseError();
        currentIdentifier = lex.getIdentifier();
        lex.advance();
    }

    //throw an error
    public void parseError(){
        throw new ParsingFailure();   
    }; 
    
    /**
     * A ParsingFailure exception is thrown on any form of
     * error detected during the parse.
     */
    public static class ParsingFailure extends RuntimeException
    {
        //ParsingFailure(String s){super(s);}
    }

}
