/* Project 1 - miniC compiler frontend
 * Damian Medina, April 2022, CS57
 */

grammar miniC;

/************************* start ***************************/
start : (decls=funcDecls)* (defs=funcDefs)+ EOF #StartExpr
      ;

/****************************** funcDefs ***********************************/
/* Multiple function definitions */
funcDefs : rest=funcDefs single=funcDef   #MultFuncDefs
      | single=funcDef                    #SingleFuncDef
      ;

/****************************** funcDef ***********************************/
/* A function definition */
funcDef : rtype=(INT|VOID) fname=NAME '(' ptype=(INT|VOID) pname=NAME? ')' b=block #functionDef
        ;

 /****************************** funcDecls ***********************************/
/* Multiple function declarations */
funcDecls : rest=funcDecls single=funcDecl      #MultFuncDecls
      | single=funcDecl                         #SingleFuncDecl
      ;

 /****************************** funcDecls ***********************************/
/* A function declaration */
funcDecl : (extern='extern')? ftype=(INT|VOID) func=NAME '(' ptype=(INT|VOID) NAME? ')' ';' #FunctionDecl
         ;

/****************************** Expr ***********************************/
expr : var=NAME                                             #Variable
    | NUMBER                                                #Number
    | op=LPREC uexpr=expr                                   #UExpr
    | left=expr op=HPREC right=expr                         #BExprMD
    | left=expr op=LPREC right=expr                         #BExprAS
    | func=NAME '(' input=expr? ')'                         #Call
    | left=expr relop=COMP right=expr                       #RExpr
    | '(' e=expr ')'                                        #ParensExpr
    ;

/****************************** Block ***********************************/
/* An empty or filled block */
block : '{' '}'                                             #EmptyBlock
      | '{' s=stmts '}'                                     #NonEmptyBlock
      ;

/****************************** Stmts ***********************************/
/* Multiple statements */
stmts : statements=stmts s=statement                        #MultStmts
      | s=statement                                         #SingleStmts
      ;

/****************************** Statement ***********************************/
/* A single statement */
statement : INT var=NAME ';'                                      #VarDecl
  | e=expr ';'                                                    #ExprStmt
  | left=NAME '=' e=expr ';'                                      #Assignment
  | 'return' (retexpr=expr)? ';'                                  #Return
  | 'if' '(' e=expr ')' ifblock=block ('else' eblock=block)?      #IfStmt
  | 'while' '(' e=expr ')' wblock=block                           #WhileStmt
  | b=block                                                       #BlockStmt
  | ';'                                                           #EmptyStmt
  ;


// Tokens
INT : ('int') ;
VOID : ('void') ;
DATATYPE : INT | VOID ;
NAME : [a-zA-Z_][a-zA-Z0-9_]* ;
NUMBER : [0-9]+ ;
HPREC : [*/] ;
LPREC : [+\-] ;
COMP : ('>') | ('<') | ('<=') | ('>=') | ('==') | ('!=') ;
NL : [\r\n]+ -> skip ;
WS : [ \t]+ -> skip ;