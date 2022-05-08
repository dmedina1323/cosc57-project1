ANTLR4=java -jar /thayerfs/courses/22spring/cosc057/workspace/antlr-4.9.3-complete.jar -visitor

all: miniC.class

miniCParser.java: miniC.g4
	$(ANTLR4) miniC.g4

miniC.class: miniCParser.java miniCLexer.java miniCListener.java miniC.java miniCVisitor.java
	javac *.java

clean: 
	rm -f m*.class m*tokens m*interp m*class miniC*Listener.java miniCParser.java miniCLexer.java miniC*Visitor.java *Visitor.class AST*.class
