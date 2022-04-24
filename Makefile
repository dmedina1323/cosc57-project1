ANTLR4=java -jar /thayerfs/courses/22spring/cosc057/workspace/antlr-4.9.3-complete.jar

all: Frontend.class

FrontendParser.java: Frontend.g4
	$(ANTLR4) Frontend.g4

Frontend.class: FrontendParser.java Frontend.java FrontendWalker.java
	javac Frontend*.java

clean: 
	rm -f F*.class F*tokens F*interp F*class Frontend*Listener.java FrontendParser.java FrontendLexer.java
