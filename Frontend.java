/* Frontend.java - main java file for Frontend solution
 * Based off of example provided
 * Damian Medina, April 2022, CS57
*/
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class Frontend {

  private static final boolean enableVerboseListener = true;
  private static final boolean enableWalker = true;

    // Verbose error listener sample taken from the ANTLR4 Reference
  public static class VerboseListener extends BaseErrorListener {
  
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg,
                            RecognitionException e)
        {
          List<String> stack = ((Parser)recognizer).getRuleInvocationStack();
          Collections.reverse(stack);
          System.err.println("rule stack: "+stack);
          System.err.println("line "+line+":"+charPositionInLine+" at "+
                            offendingSymbol+": "+msg);
        }
  }

  public static void main(String[] args) throws Exception {
    String inputFile = null;

    if ( args.length>0 ) inputFile = args[0];
    InputStream is = System.in;
    if ( inputFile!=null ) {
        is = new FileInputStream(inputFile);
    }

    CharStream input = CharStreams.fromStream(is);
    FrontendLexer lexer = new FrontendLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    FrontendParser parser = new FrontendParser(tokens);

    // Remove the default ConsoleErrorListener and add our custom one 
    if (enableVerboseListener) {
      parser.removeErrorListeners();
      parser.addErrorListener(new VerboseListener());
    }

    // Tell the parser to build a tree
    parser.setBuildParseTree(true);
    ParseTree tree = parser.start();  // parse as usual

    // If walker is selected, output the tree just like grun.
    if (enableWalker) {
      ParseTreeWalker walker = new ParseTreeWalker();
      walker.walk( new FrontendWalker(), tree );

      // show tree in text form
      // System.out.println(tree.toStringTree(parser));
    }
  }
}
