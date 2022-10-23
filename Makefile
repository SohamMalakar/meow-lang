
all: meow

meow:
	javac -d ./target/classes ./src/*.java
	cd ./target/classes && clear && java src.Shell

clean:
	rm -rf target
