/* miniC.java - main java file for miniC solution
 * Damian Medina, April 2022, CS57
*/
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;


/******************************************************************************/
/* Set */
/* Constructs a set for access by the visitors to locate undeclared or twice declared variables */
/******************************************************************************/
class extendedVisitor<T> extends miniCBaseVisitor<T> {
  static Set<String> variableSet = new HashSet<String>();
}

/******************************************************************************/
/* Expr */
/* Supports visitation of all the different types of expressions */
/******************************************************************************/
class ExprVisitor extends extendedVisitor<ASTExprNode> {

  /* High Precedence - Multiplication and Division */
  public ASTBExprNode visitBExprMD(miniCParser.BExprMDContext ctx) {
    // switch on operator
    switch (ctx.op.getText()) {
      // if multiplication
      case ("*") : 
        return new ASTBExprNode(this.visit(ctx.left), this.visit(ctx.right), ASTBOpType.MUL);
      // if division
      default :
        return new ASTBExprNode(this.visit(ctx.left), this.visit(ctx.right), ASTBOpType.DIV);
    }
  }

  /* Low Precedence - Addition and Subtraction */
  public ASTBExprNode visitBExprAS(miniCParser.BExprASContext ctx) {
    // switch on operator
    switch (ctx.op.getText()) {
      // if addition
      case ("+") : 
        return new ASTBExprNode(this.visit(ctx.left), this.visit(ctx.right), ASTBOpType.ADD);
      // if subtraction
      default :
        return new ASTBExprNode(this.visit(ctx.left), this.visit(ctx.right), ASTBOpType.SUB);
    }
  }

  /* Variable was used in an expression */
  public ASTVarNode visitVariable(miniCParser.VariableContext ctx) {
    String var = ctx.var.getText();
    if (!this.variableSet.contains(var)){
      System.err.println("Error: Use of undeclared variable: '"+var+"'");
    }
    return new ASTVarNode(var);
  }
  /* Number was used in an expression */
  public ASTIntLiteralNode visitNumber(miniCParser.NumberContext ctx) {
    return new ASTIntLiteralNode(Integer.parseInt(ctx.getText()));
  }

  /* Unary Expression */
  public ASTUExprNode visitUExpr(miniCParser.UExprContext ctx) {
    switch (ctx.op.getText()){
      // positive operator
      case ("+") :
        return new ASTUExprNode(this.visit(ctx.uexpr), ASTUOpType.POS);
      // negative operator
      default :
        return new ASTUExprNode(this.visit(ctx.uexpr), ASTUOpType.NEG);
    }
  }

  /* Function Call */
  public ASTCallNode visitCall(miniCParser.CallContext ctx){
    if (ctx.input == null){
      return new ASTCallNode(ctx.func.getText(), Optional.ofNullable(null));
    }
    return new ASTCallNode(ctx.func.getText(), Optional.of(this.visit(ctx.input)));
  }

  /* Relational Expression */
  public ASTRExprNode visitRExpr(miniCParser.RExprContext ctx){
    switch (ctx.relop.getText()){
      // Less than comparison
      case ("<") :
        return new ASTRExprNode(this.visit(ctx.left), this.visit(ctx.right), ASTROpType.LT);
      // Greater than comparison
      case (">") :
        return new ASTRExprNode(this.visit(ctx.left), this.visit(ctx.right), ASTROpType.GT);
      // Less than or equal to comparison
      case ("<=") :
        return new ASTRExprNode(this.visit(ctx.left), this.visit(ctx.right), ASTROpType.LEQ);
      // Greater than or equal to comparison
      case (">=") :
        return new ASTRExprNode(this.visit(ctx.left), this.visit(ctx.right), ASTROpType.GEQ);
      // Equal to comparison
      case ("==") :
        return new ASTRExprNode(this.visit(ctx.left), this.visit(ctx.right), ASTROpType.EQ);
      // Not equal comparsion
      default :
        return new ASTRExprNode(this.visit(ctx.left), this.visit(ctx.right), ASTROpType.NEQ);
    }
  }
  
  /* Expression in parentheses */
  public ASTExprNode visitParensExpr(miniCParser.ParensExprContext ctx){
    return this.visit(ctx.e);
  }
}

/******************************************************************************/
/* Stmts */
/* Visits multiple lines of statements */
/******************************************************************************/
class StmtsVisitor extends extendedVisitor<ArrayList<ASTStmtNode>> {
  public ArrayList<ASTStmtNode> stmts;

  /* Single Statement */
  public ArrayList<ASTStmtNode> visitSingleStmts(miniCParser.SingleStmtsContext ctx) {
    stmts = new ArrayList<ASTStmtNode>();
    stmts.add(new StatementVisitor().visit(ctx.s));
    return stmts;
  }

  /* Multiple Statements */
  public ArrayList<ASTStmtNode> visitMultStmts(miniCParser.MultStmtsContext ctx) {
    this.visit(ctx.statements);
    stmts.add(new StatementVisitor().visit(ctx.s));
    return stmts;
  }
}

/******************************************************************************/
/* Statement */
/* Supports visitation of each type of statement */
/******************************************************************************/
class StatementVisitor extends extendedVisitor<ASTStmtNode> {

  /* Variable Declaration */
  public ASTVarDeclNode visitVarDecl(miniCParser.VarDeclContext ctx){
    String var = ctx.var.getText();

    // if adding to set fails, the var was already in there
    if (!this.variableSet.add(var)){
      System.err.println("Error: Cannot redeclare variable: '"+var+"'");
    }
    return new ASTVarDeclNode(false, ASTDataType.INT_T, ctx.var.getText());
  }

  /* Expression Statement */
  public ASTExprNode visitExprStmt(miniCParser.ExprStmtContext ctx) {
    return new ExprVisitor().visit(ctx.e);
  }

  /* Assignment */
  public ASTAsgnNode visitAssignment(miniCParser.AssignmentContext ctx) {
    String var = ctx.left.getText();
    
    // if variable is not in set, it is undeclared and should not be assigned
    if (!this.variableSet.contains(var)){
      System.err.println("Error: Cannot assign undeclared variable: '"+var+"'");
    }
    return new ASTAsgnNode(var, new ExprVisitor().visit(ctx.e));
  }

  /* Return */
  public ASTRetNode visitReturn(miniCParser.ReturnContext ctx) {
    // there is not an expression being returned
    if (ctx.retexpr == null){
      return new ASTRetNode(Optional.ofNullable(null));
    }

    return new ASTRetNode(Optional.of(new ExprVisitor().visit(ctx.retexpr)));
  }

  /* If Statement */
  public ASTIfNode visitIfStmt(miniCParser.IfStmtContext ctx) {
    // there is not an else block
    if (ctx.eblock == null) {
      return new ASTIfNode(new ExprVisitor().visit(ctx.e), new BlockVisitor().visit(ctx.ifblock), Optional.ofNullable(null));
    }

    return new ASTIfNode(new ExprVisitor().visit(ctx.e), new BlockVisitor().visit(ctx.ifblock), Optional.of(new BlockVisitor().visit(ctx.eblock)));
  }

  /* While Statement */
  public ASTWhileNode visitWhileStmt(miniCParser.WhileStmtContext ctx){
    return new ASTWhileNode(new ExprVisitor().visit(ctx.e), new BlockVisitor().visit(ctx.wblock));
  }

  /* Empty */
  public ASTEmptyStmtNode visitEmptyStmt(miniCParser.EmptyStmtContext ctx){
    return new ASTEmptyStmtNode();
  }

  /* Block Statement */
  public ASTBlockNode visitBlockStmt(miniCParser.BlockStmtContext ctx) {
    return new BlockVisitor().visit(ctx.b);
  }
}

/*****************************************************************************/
/* Block */
/* Support visitation of empty and nonempty block */
/*****************************************************************************/
class BlockVisitor extends extendedVisitor<ASTBlockNode> {
  /* Empty block */
  public ASTBlockNode visitEmptyBlock(miniCParser.EmptyBlockContext ctx) {
    return new ASTBlockNode(new ArrayList<ASTStmtNode>());
  }

  /* Block with stuff */
  public ASTBlockNode visitNonEmptyBlock(miniCParser.NonEmptyBlockContext ctx){
    return new ASTBlockNode(new StmtsVisitor().visit(ctx.s));
  }
}

/****************************************************************************/
/* funcDecls */
/* Visits multiple function declarations */
/****************************************************************************/
class funcDeclsVisitor extends extendedVisitor<ArrayList<ASTDeclNode>> {
  public ArrayList<ASTDeclNode> funcDecls;

  /* Single Function Declarations */
  public ArrayList<ASTDeclNode> visitSingleFuncDecl(miniCParser.SingleFuncDeclContext ctx) {
    funcDecls = new ArrayList<ASTDeclNode>(); // construct empty array of decl
    funcDecls.add(new funcDeclVisitor().visit(ctx.single));
    return funcDecls;
  }

  /* Multiple Function Declarations */
  public ArrayList<ASTDeclNode> visitMultFuncDecls(miniCParser.MultFuncDeclsContext ctx) {
    this.visit(ctx.rest);     // visit all of the statements (except 1)
    funcDecls.add(new funcDeclVisitor().visit(ctx.single));
    return funcDecls;
  }
}

/****************************************************************************/
/* funcDecl */
/* Supports visitation of a single function declaration */
/****************************************************************************/
class funcDeclVisitor extends extendedVisitor<ASTFuncDeclNode> {
  public ASTFuncDeclNode visitFunctionDecl(miniCParser.FunctionDeclContext ctx) {   
    // get return type
    ASTDataType return_type = ctx.ftype.getText().equals("void") ? ASTDataType.VOID_T : ASTDataType.INT_T;

    // get parameter type
    ASTDataType param_type = ctx.ptype.getText().equals("void") ? ASTDataType.VOID_T : ASTDataType.INT_T;

    // is it extern?
    Boolean extern = (ctx.extern != null);
    
    return new ASTFuncDeclNode(extern, return_type, ctx.func.getText(),param_type);
  }
}

/****************************************************************************/
/* funcDefs */
/* Supports visitation of multiple function definitions */
/****************************************************************************/
class funcDefsVisitor extends extendedVisitor<ArrayList<ASTFuncDefNode>>{
  public ArrayList<ASTFuncDefNode> funcDefs;

  /* Single Function Definitions */
  public ArrayList<ASTFuncDefNode> visitSingleFuncDef(miniCParser.SingleFuncDefContext ctx) {
    funcDefs = new ArrayList<ASTFuncDefNode>(); // construct empty array of funcdefs
    funcDefs.add(new funcDefVisitor().visit(ctx.single));
    return funcDefs;
  }

  /* Multiple Function Definitions */
  public ArrayList<ASTFuncDefNode> visitMultFuncDefs(miniCParser.MultFuncDefsContext ctx) {
    this.visit(ctx.rest);
    funcDefs.add(new funcDefVisitor().visit(ctx.single));
    return funcDefs;
  }
}

/****************************************************************************/
/* funcDef */
/* Supports visitation of a single function */
/****************************************************************************/
class funcDefVisitor extends extendedVisitor<ASTFuncDefNode> {
  public ASTFuncDefNode visitFunctionDef(miniCParser.FunctionDefContext ctx){
    // clear the set when we enter a new function
    this.variableSet.clear(); 

    // get return type
    ASTDataType return_type = ctx.rtype.getText().equals("void") ? ASTDataType.VOID_T : ASTDataType.INT_T;

    // get parameter type
    ASTDataType param_type = ctx.ptype.getText().equals("void") ? ASTDataType.VOID_T : ASTDataType.INT_T;

    String func_name = ctx.fname.getText();   // get function name
    String param_name = "";         // feed in empty string if param is null
    
    if (ctx.pname != null){
      param_name = ctx.pname.getText();  // get parameter name if it is not null
      this.variableSet.add(param_name);  // parameter is a usable variable
    }
    
    return new ASTFuncDefNode(return_type, func_name, param_type, param_name, new BlockVisitor().visit(ctx.b));
  }
}

/****************************************************************************/
/* Root */
/****************************************************************************/
class RootVisitor extends extendedVisitor<ASTRootNode> {
  public ASTRootNode visitStartExpr(miniCParser.StartExprContext ctx){

    ArrayList<ASTDeclNode> decls = new ArrayList<ASTDeclNode>();
    // make sure there are function declarations
    if (ctx.decls != null){
      // construct array of declaration nodes from decls visitor
      decls = new funcDeclsVisitor().visit(ctx.decls);
    }

    // feed in empty assignments (I do not support globals)
    ArrayList<ASTAsgnNode> asgns = new ArrayList<ASTAsgnNode>();

    // Construct array of function definitions from func defs visitor
    ArrayList<ASTFuncDefNode> funcs = new funcDefsVisitor().visit(ctx.defs);

    return new ASTRootNode(decls, asgns, funcs);
  }
}

/****************************************************************************/
/* Main */
/****************************************************************************/
public class miniC {

  public static void main(String[] args) throws Exception {
    String inputFile = null;

    if ( args.length>0 ) inputFile = args[0];
    InputStream is = System.in;
    if ( inputFile!=null ) {
        is = new FileInputStream(inputFile);
    }

    CharStream input = CharStreams.fromStream(is);
    miniCLexer lexer = new miniCLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    miniCParser parser = new miniCParser(tokens);

    // Tell the parser to build a tree
    parser.setBuildParseTree(true);
    ParseTree tree = parser.start();    // parse from start rule

    /* uncomment to see parse tree */
    // System.out.println(tree.toStringTree(parser));

    ASTRootNode start = new RootVisitor().visit(tree);
    start.print();  // print the AST!
  }
  
}
