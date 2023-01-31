
all: meow

meow:
	javac -d ./target/classes ./src/*.java
	
run: meow
	cd ./target/classes && clear && java src.Shell

build: meow
	jar cfe ./target/meow.jar src.Shell -C ./target/classes .

clean:
	rm -rf target
