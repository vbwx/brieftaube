dist/build: $(wildcard src/*.java)
	[ -d .tmp ] || mkdir .tmp
	if javac -d .tmp $^; then               \
		[ -f $@ ] || echo 0 > $@;           \
		build=`awk '{ print $$1 + 1 }' $@`; \
		echo $$build > $@;                  \
	fi

dist/Brieftaube.jar: src/Manifest dist/build
	jar cfm $@ $< res -C .tmp .

.PHONY: all
all: dist/Brieftaube.jar

.PHONY: clean
clean:
	rm -rf .tmp
