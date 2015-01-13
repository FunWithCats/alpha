#!/usr/bin/ruby
require './beta.rb'
require './lib.rb'

$horlogeTick ="horlogeTick"

$libdir = "../"
$libs = [ "bytes" , "logic" , "long" , "stack" , "video" ]

INCLUDE("#{$libdir}/beta.uasm")
$libs.each do |lib| INCLUDE("#{$libdir}/lib/#{lib}.uasm") end

$__DebutDuProgramme                = "__DebutDuProgramme"
$__InterruptionInstructionIllegale = "__InterruptionInstructionIllegale" 
$__InterruptionHorloge             = "__InterruptionHorloge"
$__InterruptionClavier             = "__InterruptionClavier"
$__InterruptionSouris              = "__InterruptionSouris"

BEQ(R31,$__DebutDuProgramme,R31)
BEQ(R31,$__InterruptionInstructionIllegale,R31)
BEQ(R31,$__InterruptionHorloge,R31)	
BEQ(R31,$__InterruptionClavier,R31)
BEQ(R31,$__InterruptionSouris,R31)

LIGHTINTERRUPT($__InterruptionInstructionIllegale, -1,  NOP)

LIGHTINTERRUPT($__InterruptionHorloge            ,  0 , lambda{||
  CMOVE(1,R0)
  ST(R0,$horlogeTick,R31)
})

LIGHTINTERRUPT($__InterruptionClavier, -1, NOP)
LIGHTINTERRUPT($__InterruptionSouris , -1, NOP)


VAR32($horlogeTick, 0)

LIGHTFUNCTION("attendHorloge", 1, lambda{||
  DOUNTIL(NOP , R1 , lambda{|| LD(R31,$horlogeTick,R1) })
  ST(R31, $horlogeTick, R1)
})


LIGHTFUNCTION("effaceBackBuffer", 1, lambda{||
  LMOVE("backbuffer" , R1)
  LMOVE("backbuffer + (2400 * 4)", R2)
  FOR(lambda{|| CMPLT(R1,R2,R3) } , R3 , lambda{|| ADDC(R1,4,R1) } , lambda{||
    ST(R31,0,R1) })
})

                                   
LIGHTFUNCTION("afficherBackBuffer", 5, lambda{||
  LMOVE("backbuffer" , R1)
  LMOVE(0x80000000 , R2)

  CMOVE(0          , R3)
  FOR( lambda{|| CMPLTC(R3,2400*4,R4) } , R4, lambda{|| ADDC(R3,4,R3)} , lambda{||
    ADD(R1,R3,R4)
    LD(R4,0,R4)
    ADD(R2,R3,R5)
    ST(R4,0,R5)
  })
})                                        



BALLE         = 0x000FFF00 + '0'.ord
MICROPARPIXEL = 353
CASELARGEUR   = 10  * MICROPARPIXEL
CASEHAUTEUR   = 20  * MICROPARPIXEL
ECRANLARGEUR  = 800 * MICROPARPIXEL
ECRANHAUTEUR  = 600 * MICROPARPIXEL

FUNCTION("afficherBalle" , 4 , lambda{||
  LDARG(0,R1)
  LDARG(1,R2)

  LMOVE(CASELARGEUR,R3)
  LMOVE(CASEHAUTEUR,R4)

  DIV(R1,R3,R1)
  DIV(R2,R4,R2)


  MULC(R2,80,R2)
  ADD(R2,R1,R2)
  MULC(R2,4,R2)

  LMOVE("backbuffer",R1)
  ADD(R1,R2,R2)
  LMOVE(BALLE, R1)

  ST(R1,0,R2)
})

                                   
def VECTEUR(nom, x , y)
  LABEL(nom)
  LONG(x)
  LONG(y)
  CONSTANT("#{nom}_x" , nom)                                 
  CONSTANT("#{nom}_y" , "#{nom} + 4")                                 
end

VECTEUR("position" , 20000 , 50000)                                
VECTEUR("vitesse"  ,     0 ,     0)


FUNCTION("prochainePosition", 7 , lambda{||
  LD(R31,"position_x",R1)
  LD(R31,"position_y",R2)
  LD(R31,"vitesse_x", R3)
  LD(R31,"vitesse_y", R4)
  LDARG(2,R7) # Temps

  MUL(R3,R7,R5)
  DIVC(R5,1000,R5)
  ADD(R1,R5,R1)

  MUL(R4,R7,R6)
  DIVC(R6,1000,R6)
  ADD(R2,R6,R2)

  CMPLTC(R1,0,R5)                                      
  IF(R5 , lambda{||
    MULC(R1,-1,R1)
    MULC(R3,-1,R3)
  })                                              
                                              
  CMPLTC(R2,0,R5)
  IF(R5 , lambda{||
    MULC(R2,-1,R2)
    MULC(R4,-1,R4)
  })

  CMPGEL(R1,ECRANLARGEUR ,R5)
  IF(R5, lambda{||
    MULC(R1,-1,R1)
    LMOVE(2*(ECRANLARGEUR - 1), R5)
    ADD(R1,R5,R1)

    MULC(R3,-1,R3)
  })                                                        

  CMPGEL(R2,ECRANHAUTEUR,R5)                                      
  IF(R5, lambda{||
    MULC(R2,-1,R2)
    LMOVE(2*(ECRANHAUTEUR-1),R5)
    ADD(R2,R5,R2)

    MULC(R4,-1,R4)
  })

  LDARG(0,R5)
  LDARG(1,R6)

  MUL(R5,R7,R5)
  DIVC(R5,1000,R5)
  ADD(R3,R5,R3)

  MUL(R6,R7,R6)
  DIVC(R6,1000,R6)
  ADD(R4,R6,R4)

  ST(R1,"position_x",R31)
  ST(R2,"position_y",R31)
  ST(R3,"vitesse_x",R31)
  ST(R4,"vitesse_y",R31)
})


LABEL($__DebutDuProgramme)
  CMOVE("pile",SP)

  LOOP( lambda{||
    CALL("attendHorloge",0)
    CALL("effaceBackBuffer",0)

    LD(R31,"position_x",R1)
    LD(R31,"position_y",R2)

    PUSH(R2)
    PUSH(R1)
    CALL("afficherBalle",2)

    CALL("afficherBackBuffer",0)

    LMOVE(  2000,R1)
    LMOVE(200000,R2)
    CMOVE(33,R3)

    PUSH(R3)
    PUSH(R2)
    PUSH(R1)
    CALL("prochainePosition",3)
  })

LABEL("backbuffer")
STORAGE(2400)
CONSTANT("pile" , ".")             
    
 
               