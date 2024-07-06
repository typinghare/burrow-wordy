BURROW_MODS_DIR = /opt/burrow/mods

all:
	rm -rf build/libs
	make jar
	cp build/libs/* $(BURROW_MODS_DIR)

jar:
	gradle jar
