#!/usr/bin/ruby

$instReg  = [ "ADD" , "SUB", "MUL", "DIV", "AND", "OR", "XOR", "SHL", "SHR", "SRA" , "CMPEQ", "CMPLT" , "CMPLE" , "CMPNE", "CMPGE", "CMPGT" ]
$instLit  = [ "BNE" , "BEQ" , "LD" , "ST" ]
$instJmp  = [ "JMP" , "LDR" ]
$instArr1 = [ "PUSH"  , "POP", "LONG" , "STORAGE" ]
$instArr2 = [ "MOVE"  , "CMOVE", "LMOVE" , "LDARG" , "LDVAR" , "CALL"]

$beta = File.new("beta.rb","w")

def output(s)
  $beta.puts(s + "\n")
end

def close()
  $beta.close
end  

def genRegularInst(inst)
  output("def #{inst}(ra,rb,rc)\n\treturn unit(\"\\t\\t#{inst}( \#{ra} , \#{rb} , \#{rc} )\\n\")\nend\n")
  output("def #{inst}C(ra,literal,rc)\n\treturn unit(\"\\t\\t#{inst}C( \#{ra} , \#{literal} , \#{rc} )\\n\")\nend\n")
  output("def #{inst}L(ra,literal,rc)\n\treturn unit(\"\\t\\t#{inst}L( \#{ra} , \#{literal} , \#{rc} )\\n\")\nend\n")
end  
   
def genLiteralInst(inst)
  output("def #{inst}(ra,literal,rc)\n\treturn unit(\" \\t\\t#{inst}( \#{ra} , \#{literal} , \#{rc} )\\n\")\nend\n")
  output("def #{inst}L(ra,literal,rc)\n\treturn unit(\"\\t\\t#{inst}C( \#{ra} , \#{literal} , \#{rc} )\\n\")\nend\n")
end  
   
def genJumpInst(inst)
  output("def #{inst}(ra,rc)\n\treturn unit(\"\\t\\t#{inst}( \#{ra} , \#{rc} )\\n\")\nend\n")
  output("def #{inst}L(literal,rc)\n\treturn unit(\"\\t\\t#{inst}C( \#{literal} , \#{rc} )\\n\")\nend\n")
end  
  
def genArr1Inst(inst)
  output("def #{inst}(x)\n\treturn unit(\"\\t\\t#{inst}( \#{x} )\\n\")\nend\n")
end  

def genArr2Inst(inst)
  output("def #{inst}(x, y)\n\treturn unit(\"\\t\\t#{inst}( \#{x} , \#{y} )\\n\")\nend\n")
end  

def genRegs
    (0..31).each do |i|
      output("R#{i} = \"R#{i}\"\n$r#{i} = R#{i}\n\n")
    end
    
    output("XP = R30\n$xp = XP")
    output("SP = R29\n$sp = SP")
    output("LP = R28\n$lp = LP")
    output("BP = R27\n$bp = BP\n")
end    

def genAllInst
  genRegs()
  
  $instReg.each do |inst|
    genRegularInst(inst)
  end  
  
  $instLit.each do |inst|
    genLiteralInst(inst)
  end  
  
  $instJmp.each do |inst|
    genJumpInst(inst)
  end  

  $instArr1.each do |inst|
    genArr1Inst(inst)
  end  

  $instArr2.each do |inst|
    genArr2Inst(inst)
  end  
end    

preambule= """#!/usr/bin/ruby
"""

unit="""def unit(s)
  puts s
end  
"""

output(preambule)
output(unit)
genAllInst()
close()