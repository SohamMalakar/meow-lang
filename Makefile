
all: meow

meow:
	javac -d ./target/classes ./src/*.java
	
run: meow
	cd ./target/classes && clear && java src.Shell

build: meow
	jar cf ./target/meow.jar -C ./target/classes .

clean:
	rm -rf target
