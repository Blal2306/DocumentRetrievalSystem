all: assignmentP3.class dictionary_data.class docTable_data.class QueryTokenizer.class 

assignmentP3.class: assignmentP3.java
	javac assignmentP3.java 
	
dictionary_data.class: dictionary_data.java
	javac dictionary_data.java 
	
docTable_data.class: docTable_data.java
	javac docTable_data.java

QueryTokenizer.class: QueryTokenizer.java
	javac QueryTokenizer.java

clean:
	rm -f *.class 