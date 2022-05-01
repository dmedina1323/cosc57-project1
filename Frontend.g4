// Project 1 - miniC compiler frontend
// Damian Medina, April 2022, CS57

grammar Frontend;

// Rules
start : funcDef (funcDef)* EOF ;

// Function Rules
funcDef : ('extern')? (INT|VOID) SPACE NAME (SPACE)? '(' (INT|VOID) SPACE NAME (SPACE)? ')' (';'|(statement)+);

funcCall : NAME '(' (NAME | NUMBER| VOID)* ')' ;

// Other Rules (things inside functions)

varDec : INT SPACE NAME (SPACE)* ';' ;

ifStmt : 'if' (SPACE)* '(' relationalExpr ')' (SPACE)* (statement)+ ;

elseStmt : 'else' (SPACE)* (statement)+ ;

whileStmt : 'while' (SPACE)* '(' relationalExpr ')' (SPACE)* (statement)+ ;

expr : unaryExpr | binaryExpr | term;

unaryExpr : UOP term
  | UOP '(' term ')'
  ;

binaryExpr : binaryExpr BINOP binaryExpr
  | '(' binaryExpr BINOP binaryExpr ')'
  | '(' term (SPACE)* BINOP (SPACE)* term ')' 
  | term (SPACE)* BINOP (SPACE)* term
  ;

relationalExpr : expr (SPACE)* COP (SPACE)* expr
  | '(' expr (SPACE)* COP (SPACE)* expr ')' ;

term : NAME | NUMBER | funcCall;

statement : (SPACE)* varDec 
  | (SPACE)* NAME (SPACE)* '=' (SPACE)* expr ';' 
  | (SPACE)* 'return' (SPACE)* '(' expr ')' ';'
  | (SPACE)* 'return' (SPACE)* (expr) ';'
  | (SPACE)*  ifStmt (elseStmt)? 
  | (SPACE)*  whileStmt
  | (SPACE)* '{' (statement)+ '}'
  | (SPACE)* funcCall ';'
  ;


// Tokens
INT : ('int') ;
VOID : ('void') ;
DATATYPE : INT | VOID ;
NAME : [a-z][a-zA-Z0-9_-]* ;
NUMBER : [0-9]+ ;
SPACE : [ \t]+ ;
UOP : [\-] ;
BINOP : [+\-*/] ;
COP : ('>') | ('<') | ('<=') | ('>=') | ('==') | ('!=') ;
NL : [\r\n]+ -> skip ;