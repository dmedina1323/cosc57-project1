// Project 1 - miniC compiler frontend
// Damian Medina, April 2022, CS57

grammar Frontend;

// Rules
start : funcDef (funcDef)* EOF ;

// Function Rules
funcDef : (INT|VOID) SPACE NAME (SPACE)? '(' (INT|VOID) SPACE NAME (SPACE)? ')' (';'|(block)+);

funcCall : NAME '(' (NAME | NUMBER| VOID)* ')' ;

// Other Rules (things inside functions)

block : '{' (funcCall | varDec| ifStmt| elseStmt | assignment)+ '}' ;

varDec : INT SPACE NAME (SPACE)* ';' ;

ifStmt : 'if' (SPACE)* '(' ')' (SPACE)* '{' (block)+ '}' ;

elseStmt : 'else' (SPACE)* '{' (block)+ '}' ;

assignment : NAME (SPACE)? '=' (SPACE)? (NUMBER|NAME|funcCall) ';' ;


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