#!/usr/bin/ruby

def unit(s)
  puts s
end  

R0 = "R0"
$r0 = R0


R1 = "R1"
$r1 = R1


R2 = "R2"
$r2 = R2


R3 = "R3"
$r3 = R3


R4 = "R4"
$r4 = R4


R5 = "R5"
$r5 = R5


R6 = "R6"
$r6 = R6


R7 = "R7"
$r7 = R7


R8 = "R8"
$r8 = R8


R9 = "R9"
$r9 = R9


R10 = "R10"
$r10 = R10


R11 = "R11"
$r11 = R11


R12 = "R12"
$r12 = R12


R13 = "R13"
$r13 = R13


R14 = "R14"
$r14 = R14


R15 = "R15"
$r15 = R15


R16 = "R16"
$r16 = R16


R17 = "R17"
$r17 = R17


R18 = "R18"
$r18 = R18


R19 = "R19"
$r19 = R19


R20 = "R20"
$r20 = R20


R21 = "R21"
$r21 = R21


R22 = "R22"
$r22 = R22


R23 = "R23"
$r23 = R23


R24 = "R24"
$r24 = R24


R25 = "R25"
$r25 = R25


R26 = "R26"
$r26 = R26


R27 = "R27"
$r27 = R27


R28 = "R28"
$r28 = R28


R29 = "R29"
$r29 = R29


R30 = "R30"
$r30 = R30


R31 = "R31"
$r31 = R31


XP = R30
$xp = XP
SP = R29
$sp = SP
LP = R28
$lp = LP
BP = R27
$bp = BP

def ADD(ra,rb,rc)
	return unit("\t\tADD( #{ra} , #{rb} , #{rc} )\n")
end

def ADDC(ra,literal,rc)
	return unit("\t\tADDC( #{ra} , #{literal} , #{rc} )\n")
end

def ADDL(ra,literal,rc)
	return unit("\t\tADDL( #{ra} , #{literal} , #{rc} )\n")
end

def SUB(ra,rb,rc)
	return unit("\t\tSUB( #{ra} , #{rb} , #{rc} )\n")
end

def SUBC(ra,literal,rc)
	return unit("\t\tSUBC( #{ra} , #{literal} , #{rc} )\n")
end

def SUBL(ra,literal,rc)
	return unit("\t\tSUBL( #{ra} , #{literal} , #{rc} )\n")
end

def MUL(ra,rb,rc)
	return unit("\t\tMUL( #{ra} , #{rb} , #{rc} )\n")
end

def MULC(ra,literal,rc)
	return unit("\t\tMULC( #{ra} , #{literal} , #{rc} )\n")
end

def MULL(ra,literal,rc)
	return unit("\t\tMULL( #{ra} , #{literal} , #{rc} )\n")
end

def DIV(ra,rb,rc)
	return unit("\t\tDIV( #{ra} , #{rb} , #{rc} )\n")
end

def DIVC(ra,literal,rc)
	return unit("\t\tDIVC( #{ra} , #{literal} , #{rc} )\n")
end

def DIVL(ra,literal,rc)
	return unit("\t\tDIVL( #{ra} , #{literal} , #{rc} )\n")
end

def AND(ra,rb,rc)
	return unit("\t\tAND( #{ra} , #{rb} , #{rc} )\n")
end

def ANDC(ra,literal,rc)
	return unit("\t\tANDC( #{ra} , #{literal} , #{rc} )\n")
end

def ANDL(ra,literal,rc)
	return unit("\t\tANDL( #{ra} , #{literal} , #{rc} )\n")
end

def OR(ra,rb,rc)
	return unit("\t\tOR( #{ra} , #{rb} , #{rc} )\n")
end

def ORC(ra,literal,rc)
	return unit("\t\tORC( #{ra} , #{literal} , #{rc} )\n")
end

def ORL(ra,literal,rc)
	return unit("\t\tORL( #{ra} , #{literal} , #{rc} )\n")
end

def XOR(ra,rb,rc)
	return unit("\t\tXOR( #{ra} , #{rb} , #{rc} )\n")
end

def XORC(ra,literal,rc)
	return unit("\t\tXORC( #{ra} , #{literal} , #{rc} )\n")
end

def XORL(ra,literal,rc)
	return unit("\t\tXORL( #{ra} , #{literal} , #{rc} )\n")
end

def SHL(ra,rb,rc)
	return unit("\t\tSHL( #{ra} , #{rb} , #{rc} )\n")
end

def SHLC(ra,literal,rc)
	return unit("\t\tSHLC( #{ra} , #{literal} , #{rc} )\n")
end

def SHLL(ra,literal,rc)
	return unit("\t\tSHLL( #{ra} , #{literal} , #{rc} )\n")
end

def SHR(ra,rb,rc)
	return unit("\t\tSHR( #{ra} , #{rb} , #{rc} )\n")
end

def SHRC(ra,literal,rc)
	return unit("\t\tSHRC( #{ra} , #{literal} , #{rc} )\n")
end

def SHRL(ra,literal,rc)
	return unit("\t\tSHRL( #{ra} , #{literal} , #{rc} )\n")
end

def SRA(ra,rb,rc)
	return unit("\t\tSRA( #{ra} , #{rb} , #{rc} )\n")
end

def SRAC(ra,literal,rc)
	return unit("\t\tSRAC( #{ra} , #{literal} , #{rc} )\n")
end

def SRAL(ra,literal,rc)
	return unit("\t\tSRAL( #{ra} , #{literal} , #{rc} )\n")
end

def CMPEQ(ra,rb,rc)
	return unit("\t\tCMPEQ( #{ra} , #{rb} , #{rc} )\n")
end

def CMPEQC(ra,literal,rc)
	return unit("\t\tCMPEQC( #{ra} , #{literal} , #{rc} )\n")
end

def CMPEQL(ra,literal,rc)
	return unit("\t\tCMPEQL( #{ra} , #{literal} , #{rc} )\n")
end

def CMPLT(ra,rb,rc)
	return unit("\t\tCMPLT( #{ra} , #{rb} , #{rc} )\n")
end

def CMPLTC(ra,literal,rc)
	return unit("\t\tCMPLTC( #{ra} , #{literal} , #{rc} )\n")
end

def CMPLTL(ra,literal,rc)
	return unit("\t\tCMPLTL( #{ra} , #{literal} , #{rc} )\n")
end

def CMPLE(ra,rb,rc)
	return unit("\t\tCMPLE( #{ra} , #{rb} , #{rc} )\n")
end

def CMPLEC(ra,literal,rc)
	return unit("\t\tCMPLEC( #{ra} , #{literal} , #{rc} )\n")
end

def CMPLEL(ra,literal,rc)
	return unit("\t\tCMPLEL( #{ra} , #{literal} , #{rc} )\n")
end

def CMPNE(ra,rb,rc)
	return unit("\t\tCMPNE( #{ra} , #{rb} , #{rc} )\n")
end

def CMPNEC(ra,literal,rc)
	return unit("\t\tCMPNEC( #{ra} , #{literal} , #{rc} )\n")
end

def CMPNEL(ra,literal,rc)
	return unit("\t\tCMPNEL( #{ra} , #{literal} , #{rc} )\n")
end

def CMPGE(ra,rb,rc)
	return unit("\t\tCMPGE( #{ra} , #{rb} , #{rc} )\n")
end

def CMPGEC(ra,literal,rc)
	return unit("\t\tCMPGEC( #{ra} , #{literal} , #{rc} )\n")
end

def CMPGEL(ra,literal,rc)
	return unit("\t\tCMPGEL( #{ra} , #{literal} , #{rc} )\n")
end

def CMPGT(ra,rb,rc)
	return unit("\t\tCMPGT( #{ra} , #{rb} , #{rc} )\n")
end

def CMPGTC(ra,literal,rc)
	return unit("\t\tCMPGTC( #{ra} , #{literal} , #{rc} )\n")
end

def CMPGTL(ra,literal,rc)
	return unit("\t\tCMPGTL( #{ra} , #{literal} , #{rc} )\n")
end

def BNE(ra,literal,rc)
	return unit(" \t\tBNE( #{ra} , #{literal} , #{rc} )\n")
end

def BNEL(ra,literal,rc)
	return unit("\t\tBNEC( #{ra} , #{literal} , #{rc} )\n")
end

def BEQ(ra,literal,rc)
	return unit(" \t\tBEQ( #{ra} , #{literal} , #{rc} )\n")
end

def BEQL(ra,literal,rc)
	return unit("\t\tBEQC( #{ra} , #{literal} , #{rc} )\n")
end

def LD(ra,literal,rc)
	return unit(" \t\tLD( #{ra} , #{literal} , #{rc} )\n")
end

def LDL(ra,literal,rc)
	return unit("\t\tLDC( #{ra} , #{literal} , #{rc} )\n")
end

def ST(ra,literal,rc)
	return unit(" \t\tST( #{ra} , #{literal} , #{rc} )\n")
end

def STL(ra,literal,rc)
	return unit("\t\tSTC( #{ra} , #{literal} , #{rc} )\n")
end

def JMP(ra,rc)
	return unit("\t\tJMP( #{ra} , #{rc} )\n")
end

def JMPL(literal,rc)
	return unit("\t\tJMPC( #{literal} , #{rc} )\n")
end

def LDR(ra,rc)
	return unit("\t\tLDR( #{ra} , #{rc} )\n")
end

def LDRL(literal,rc)
	return unit("\t\tLDRC( #{literal} , #{rc} )\n")
end

def PUSH(x)
	return unit("\t\tPUSH( #{x} )\n")
end

def POP(x)
	return unit("\t\tPOP( #{x} )\n")
end

def LONG(x)
	return unit("\t\tLONG( #{x} )\n")
end

def STORAGE(x)
	return unit("\t\tSTORAGE( #{x} )\n")
end

def MOVE(x, y)
	return unit("\t\tMOVE( #{x} , #{y} )\n")
end

def CMOVE(x, y)
	return unit("\t\tCMOVE( #{x} , #{y} )\n")
end

def LMOVE(x, y)
	return unit("\t\tLMOVE( #{x} , #{y} )\n")
end

def LDARG(x, y)
	return unit("\t\tLDARG( #{x} , #{y} )\n")
end

def LDVAR(x, y)
	return unit("\t\tLDVAR( #{x} , #{y} )\n")
end

def CALL(x, y)
	return unit("\t\tCALL( #{x} , #{y} )\n")
end

