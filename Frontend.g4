// Project 1 - miniC compiler frontend
// Damian Medina, April 2022, CS57

grammar Frontend;

// Rules
start : funcDef (funcDef)* EOF ;

// Function Rules
funcDef : ('extern')? (INT|VOID) NAME '(' (INT|VOID)NAME? ')' (';'|statement)+ ;

funcCall : NAME '(' (NAME | NUMBER| VOID)* ')' ;

// Other Rules (things inside functions)

varDec : INT  NAME ';' ;

ifStmt : 'if' '(' relationalExpr ')' (statement)+ ;

elseStmt : 'else' (statement)+ ;

whileStmt : 'while' '(' relationalExpr ')' (statement)+ ;

expr : unaryExpr | binaryExpr | term;

unaryExpr : UOP term
  | UOP '(' term ')'
  ;

binaryExpr : binaryExpr BINOP binaryExpr
  | '(' binaryExpr BINOP binaryExpr ')'
  | '(' term BINOP term ')' 
  | term BINOP term
  | binaryExpr BINOP term
  | '(' binaryExpr BINOP term ')'
  | term BINOP binaryExpr
  | '(' term BINOP binaryExpr ')'
  ;

relationalExpr : expr COP expr
  | '(' expr COP expr ')' 
  ;

term : NAME | NUMBER | funcCall;

statement : varDec 
  | NAME '=' expr ';' 
  | 'return' '(' expr ')' ';'
  | 'return' (expr) ';'
  | 'return' ';'
  | ifStmt (elseStmt)? 
  | whileStmt
  | '{' (statement)+ '}'
  | funcCall ';'
  ;


// Tokens
INT : ('int') ;
VOID : ('void') ;
DATATYPE : INT | VOID ;
NAME : [a-z][a-zA-Z0-9_-]* ;
NUMBER : [0-9]+ ;
BINOP : [+\-*/] ;
UOP : [\-] ;
COP : ('>') | ('<') | ('<=') | ('>=') | ('==') | ('!=') ;
NL : [\r\n]+ -> skip ;
WS : [ \t]+ -> skip ;