CLASSPATH = src/:jars/*

JC = javac
JDOC = javadoc
JVM = java

SRCS = $(shell find . -name *.java)
BINS = $(SRCS:.java=.class)
DOCS = docs/

TEST_BIN = org.junit.runner.JUnitCore MapCrdtTest
DEMO_BIN = CLISimulator

all: $(BINS)

test: all
	$(JVM) -cp $(CLASSPATH) $(TEST_BIN)

demo: all
	$(JVM) -cp $(CLASSPATH) $(DEMO_BIN)

docs:
	$(JDOC) -d $(DOCS) -cp $(CLASSPATH) $(SRCS)

%.class: %.java
	$(JC) -cp $(CLASSPATH) $<

clean:
	rm -f $(BINS)
	rm -rf $(DOCS)
