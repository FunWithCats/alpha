all: balle_micro.uasm

beta.rb: genbeta.rb
	ruby genbeta.rb

balle_micro.uasm: balle_micro.rb beta.rb lib.rb
	ruby balle_micro.rb > balle_micro.uasm

clean:
	rm -frv *.uasm beta.rb *~ *.coe *.bin *.contents
