all: httpget.class

httpget.class: httpget.java
	javac httpget.java

clean:
	rm httpget.class
