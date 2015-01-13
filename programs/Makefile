FILES=  examples/fonctions.uasm			\
	examples/memoire_video.uasm		\
	examples/interruptions_souris.uasm	\
	examples/interruptions_clavier.uasm	\
	examples/balle.uasm			\
	examples/balle_backbuffer.uasm		\
	examples/balle_micro.uasm
	
BINS=   examples/fonctions.bin			\
	examples/memoire_video.bin		\
	examples/interruptions_souris.bin	\
	examples/interruptions_clavier.bin	\
        examples/balle.bin			\
        examples/balle_backbuffer.bin		\
        examples/balle_micro.bin

	
BSim=java -jar bsim.jar

       
all: assemble
	@echo "Assemble the open files to generate bin files"
	@echo "Then open those files with Alpha"

.PHONY: clean bsim

bsim.jar:
	wget "http://6004.mit.edu/currentsemester/courseware/bsim.jar"
beta.uasm:
	wget "http://6004.mit.edu/currentsemester/courseware/beta.uasm"

clean:
	rm -frv dist/
	find . \( -iname "*.bak" -or -iname "*.bin" -or -iname "*.contents" -or -iname "*.coe" -or -iname "nohup.out" \) -delete
	find Gen -iname "*.uasm" -delete


assemble: bsim.jar beta.uasm ${FILES}
	${BSim} ${FILES}
	
package:
	rm -fr dist/
	mkdir -p dist/AlphaPrograms
	cp -riv --parents `git ls-files` dist/AlphaPrograms/
	cd dist; \
	zip -r AlphaPrograms.zip AlphaPrograms/
	
