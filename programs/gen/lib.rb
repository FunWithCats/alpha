#!/usr/bin/ruby
require './beta.rb'

$prefixStack = []
$prefix="label"
$compteur = 0

def newLabel
  $compteur += 1
  return "#{$prefix}#{$compteur}"
end

def LABEL(l)
  return unit(l + ":\n")
end

def IF(ra,alors)
  labelfin = newLabel
  BEQ(ra,labelfin,R31)
  alors.call()
  LABEL(labelfin)
end

def IFELSE(ra,alors,sinon)
  labelelse = newLabel
  labelfin  = newLabel
  BEQ(ra,labelelse,R31)
  alors.call
  BEQ(R31,labelfin,R31)
  LABEL(labelelse)
  sinon.call
  LABEL(labelfin)
end

def FOR(cond, rcond, iter, corps)
  labelcond = newLabel
  labelfin  = newLabel
  LABEL(labelcond)
  cond.call
  BEQ(rcond, labelfin, R31)
  corps.call
  iter.call
  BEQ(R31,labelcond,R31)
  LABEL(labelfin)
end

def WHILE(cond, rcond, corps)
  labelcond = newLabel
  labelfin  = newLabel
  LABEL(labelcond)
  cond.call
  BEQ(rcond, labelfin, R31)
  corps.call
  BEQ(R31,labelcond,R31)
  LABEL(labelfin)
end  
  
def DOWHILE(cond, rcond, corps)
  labeldebut = newLabel
  LABEL(labeldebut)
  corps.call
  cond.call
  BNE(rcond, labeldebut, R31)
end

def DOUNTIL(cond, rcond, corps)
  labeldebut = newLabel
  LABEL(labeldebut)
  corps.call
  cond.call
  BEQ(rcond, labeldebut, R31)
end

def LOOP(corps)
  labeldebut = newLabel
  LABEL(labeldebut)
  corps.call
  BEQ(R31,labeldebut,R31)
end  


def LIGHTFUNCTION(name,nregs,corps)
  $prefixStack.push($prefix)
  $prefixStack.push($compteur)
  $prefix   = name
  $compteur = 0
  LABEL(name)
  1.upto(nregs)   { |i| PUSH("R#{i}") }
  corps.call
  nregs.downto(1) { |i| POP( "R#{i}") }
  JMP(LP,R31)
  $compteur=$prefixStack.pop
  $prefix=$prefixStack.pop
end


def FUNCTION(name,nregs,corps)
  $prefixStack.push($prefix)
  $prefixStack.push($compteur)
  $prefix   = name
  $compteur = 0
  LABEL(name)
  PUSH(LP)
  PUSH(BP)
  MOVE(SP,BP)
  1.upto(nregs)   { |i| PUSH("R#{i}") }
  corps.call
  nregs.downto(1) { |i| POP( "R#{i}") }
  POP(BP)
  POP(LP)
  JMP(LP,R31)
  $compteur=$prefixStack.pop
  $prefix=$prefixStack.pop
end


def LIGHTINTERRUPT(name, nregs, corps)
  $prefixStack.push($prefix)
  $prefixStack.push($compteur)
  $prefix   = name
  $compteur = 0
  LABEL(name)
  SUBC(XP,4,XP)
  0.upto(nregs)   { |i| PUSH("R#{i}") }
  corps.call
  nregs.downto(0) { |i| POP( "R#{i}") }
  JMP(XP,R31)
  $compteur=$prefixStack.pop
  $prefix=$prefixStack.pop
end  

def INTERRUPT(name, nregs, corps)
  $prefixStack.push($prefix)
  $prefixStack.push($compteur)
  $prefix   = name
  $compteur = 0
  LABEL(name)
  SUBC(XP,4,XP)
  PUSH(LP)
  PUSH(BP)
  MOVE(SP,BP)
  0.upto(nregs)   { |i| PUSH("R#{i}") }
  corps.call
  nregs.downto(0) { |i| POP( "R#{i}") }
  POP(BP)
  POP(LP)
  JMP(XP,R31)
  $compteur=$prefixStack.pop
  $prefix=$prefixStack.pop
end  

def COMMENT(comment)
  return unit("\n| #{comment}")
end

def INCLUDE(s)
  return unit(".include #{s}")
end

def VAR32(nom, valeur)
  return unit("#{nom}:	LONG(#{valeur})")
end

NOP = lambda{|| }

def CONSTANT(nom , valeur)
  return unit("#{nom} = #{valeur}")
end