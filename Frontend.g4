// Project 1 - miniC compiler frontend
// Damian Medina, April 2022, CS57

grammar Frontend;

// Rules
start : line (NL line)* EOF ;

line : funcDef
  | prototype
  | func
  | EOF
  ;

prototype :  func ';' ;

funcDef : func (NL)* '{' (ifStmt | varDec | elseStmt | assignment | funcCall)+ '}' ;

func : INT SPACE FUNCNAME (SPACE)* '(' (varDec|VOID) ')'
  | VOID SPACE FUNCNAME (SPACE)* '(' (varDec|VOID) ')'
  ;

funcCall : FUNCNAME '(' (VARIABLE | NUMBER| VOID)* ')' ;

varDec : INT SPACE VARIABLE (SPACE)* ';' ;

ifStmt : 'if' (SPACE)* '(' ')' (SPACE)* '{' (line)+ '}' ;

elseStmt : 'else' (SPACE)* '{' (line)+ '}' ;

assignment : VARIABLE (SPACE)? '=' (SPACE)? (NUMBER|VARIABLE|funcDef) ';' ;


// Tokens
INT : 'int' ;
VOID : 'void' ;
VARIABLE : [a-zA-Z][a-zA-Z0-9_-]* ;
FUNCNAME : [a-zA-Z_]+ ;
NUMBER : [0-9]+ ;
SPACE : [ \t]+ ;
NL : [\r\n]+ ;
UOP : [\-] ;
BINOP : [+\-*/] ;
COP : ('>') | ('<') | ('<=') | ('>=') | ('==') | ('!=') ;